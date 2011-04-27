/*
 * JBoss, by Red Hat.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.forge.shell.plugins.builtin;

import org.jboss.forge.resources.Resource;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.ShellColor;
import org.jboss.forge.shell.plugins.*;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Implementation of more & less, but called more. "More is less".
 * 
 * @author Mike Brock
 */
@Alias("more")
@Topic("Shell Environment")
public class MorePlugin implements Plugin
{
   private static final String MOREPROMPT = "--[SPACE:PageDn U:PageUp ENT:LineDn J:LineUp Q:Quit]--";
   private static final String SEARCH_FORWARD_PROMPT = "Search-Foward: ";
   private static final String SEARCH_BACKWARDS_PROMPT = "Search-Backwards: ";
   private static final String PATTERN_NOT_FOUND = "-- Pattern not found: ";
   private static final String INVALID_COMMAND = "-- Invalid command: ";

   private final Shell shell;

   @Inject
   public MorePlugin(final Shell shell)
   {
      this.shell = shell;
   }

   @DefaultCommand
   public void run(@PipeIn final InputStream pipeIn,
                   final Resource<?> file,
                   @Option(name = "noautoexit", shortName = "x", flagOnly = true) final boolean noAutoExit,
                   final PipeOut pipeOut)
            throws IOException
   {
      if (file != null)
      {
         InputStream fileInstream = null;
         try
         {
            fileInstream = file.getResourceInputStream();
            more(fileInstream, pipeOut, noAutoExit);
         }
         finally
         {
            if (fileInstream != null)
            {
               fileInstream.close();
            }
         }
      }
      else if (pipeIn != null)
      {
         more(pipeIn, pipeOut, noAutoExit);
      }
   }

   void more(final InputStream stream, final PipeOut out, boolean noAutoExit) throws IOException
   {
      byte[] buffer = new byte[128];
      int read;

      byte c;

      int height = shell.getHeight() - 1;
      int width = shell.getWidth();

      int lCounter = width;
      int y = 0;

      LineBuffer lineBuffer = new LineBuffer(stream, width);

      StringBuilder lastPattern = new StringBuilder();

      do
      {
         Mainloop: while ((read = lineBuffer.read(buffer)) != -1)
         {
            Bufferloop: for (int i = 0; i < read; i++)
            {
               if (--lCounter <= -1)
               {
                  lineBuffer.seenLine();
                  lCounter = width;
                  ++y;
               }

               switch (c = buffer[i])
               {
               case '\r':
                  continue Bufferloop;
               case '\n':
                  lineBuffer.seenLine();
                  lCounter = width;
                  ++y;

               default:
                  if (y >= height)
                  {
                     y = height;
                     out.println();
                     height = shell.getHeight() - 1;
                     switch (prompt(lineBuffer, out, lastPattern))
                     {
                     case -1:
                        y = 0;
                        continue Mainloop;
                     case -2:
                        y--;
                        continue Bufferloop;
                     case -3:
                        y = 0;
                        continue Bufferloop;
                     case 0:
                        noAutoExit = false;
                        break Mainloop;
                     }

                  }
               }

               out.write(c);
            }
         }

         if (noAutoExit)
         {
            switch (prompt(lineBuffer, out, lastPattern))
            {
            case -1:
               y = 0;
               break;
            case -2:
               y--;
               break;
            case -3:
               y = 0;
               break;
            case 0:
               noAutoExit = false;
               break;
            }
         }

      }
      while (noAutoExit);
   }

   private int prompt(final LineBuffer lineBuffer, final PipeOut out, final StringBuilder lastPattern)
            throws IOException
   {
      boolean backwards = false;

      do
      {
         String topBottomIndicator;
         if (lineBuffer.getCurrentLine() - shell.getHeight() + 1 == 0)
         {
            topBottomIndicator = " TOP";
         }
         else if (lineBuffer.atEnd())
         {
            topBottomIndicator = " END";
         }
         else
         {
            topBottomIndicator = "";
         }

         String prompt = MOREPROMPT + "[line:" + lineBuffer.getCurrentLine()
                  + topBottomIndicator + "]--";

         out.print(ShellColor.BOLD, prompt);
         int scanCode;

         switch (scanCode = shell.scan())
         {
         case 'e':
         case 'E':
         case 'j':
         case 'J':
         case 16:
            lineBuffer.rewindBuffer(shell.getHeight() - 1, lineBuffer.getCurrentLine() - 1);
            lineBuffer.setLineWidth(shell.getWidth());
            // y = 0;
            shell.clear();
            return -1;

            // continue Mainloop;
         case 'u':
         case 'U':
            lineBuffer.rewindBuffer(shell.getHeight() - 1, lineBuffer.getCurrentLine() - shell.getHeight());
            lineBuffer.setLineWidth(shell.getWidth());
            // y = 0;
            shell.clear();
            return -1;
            // continue Mainloop;

         case 'y':
         case 'Y':
         case 'k':
         case 'K':
         case 14:
         case '\n':
            // y--;
            lineBuffer.setLineWidth(shell.getWidth());

            shell.cursorLeft(prompt.length());
            shell.clearLine();
            return -2;
            // continue Bufferloop;
         case ' ':
            // y = 0;
            // height = shell.getHeight() - 1;
            lineBuffer.setLineWidth(shell.getWidth());

            shell.clearLine();
            shell.cursorLeft(prompt.length());
            return -3;
            // continue Bufferloop;
         case 'q':
         case 'Q':
            shell.clearLine();
            shell.cursorLeft(prompt.length());
            return 0;
            // break Mainloop;

         case '?':
            backwards = true;
         case '/':
            shell.clearLine();
            shell.cursorLeft(prompt.length());

            prompt = backwards ? SEARCH_BACKWARDS_PROMPT : SEARCH_FORWARD_PROMPT;
            String pattern;

            if (lastPattern != null)
            {
               prompt += "[ENT to repeat search '" + lastPattern + "']: ";
            }

            out.print(ShellColor.BOLD, prompt);
            pattern = shell.promptAndSwallowCR().trim();

            String searched;

            shell.clearLine();
            shell.cursorLeft(prompt.length() + pattern.length());

            prompt += "Scanning buffer...";
            out.print(ShellColor.BOLD, prompt);

            String p;
            if (pattern.equals("") && (lastPattern.length() != 0))
            {
               p = searched = lineBuffer.toString();
            }
            else
            {
               if (lastPattern.length() != 0)
               {
                  lastPattern.delete(0, lastPattern.length() - 1);
               }
               lastPattern.append(pattern);
               p = searched = pattern;
            }

            int result = lineBuffer.findPattern(p, backwards);

            if (result == -1)
            {
               shell.clearLine();
               shell.cursorLeft(prompt.length());
               shell.print(ShellColor.RED, PATTERN_NOT_FOUND + searched);

               shell.scan();
               shell.clearLine();
               shell.cursorLeft(PATTERN_NOT_FOUND.length() + searched.length());
            }
            else
            {
               lineBuffer.rewindBuffer(shell.getHeight() - 1, result);
               // y = 0;
               shell.clear();
               return -1;
            }
            break;

         default:
            shell.clearLine();
            shell.cursorLeft(prompt.length());
            out.print(ShellColor.RED, INVALID_COMMAND + ((char) scanCode));
            shell.scan();

            shell.clearLine();
            shell.cursorLeft(INVALID_COMMAND.length() + 1);

         }
      }
      while (true);
   }

   /**
    * A simple line buffer implementation. Marks every INDEX_MARK_SIZE lines for fast scanning and lower memory usage.
    */
   private static class LineBuffer extends InputStream
   {
      private final InputStream stream;
      private final StringBuilder curr;
      private final ArrayList<Integer> index;
      private boolean buffered = false;

      private int bufferPos;
      private int bufferLine;

      private int lineWidth;
      private int lineCounter;

      private static final int INDEX_MARK_SIZE = 100;
      private static final int MAX_PREBUFFER = 1024 * 5; // kkb

      int totalLines = 0;

      private LineBuffer(final InputStream stream, final int lineWidth)
      {
         this.stream = stream;
         curr = new StringBuilder();
         index = new ArrayList<Integer>();
         this.lineWidth = lineWidth;
         this.lineCounter = lineWidth - 1;
      }

      @Override
      public int read() throws IOException
      {
         if (buffered)
         {
            if (bufferPos < curr.length())
            {
               return curr.charAt(bufferPos++);
            }
            else
            {
               int c = stream.read();
               if (c == -1)
               {
                  return -1;
               }
               else
               {
                  buffered = false;
                  return read();
               }
            }
         }
         else
         {
            int c;
            int read;
            byte[] buffer = new byte[1024];
            int totalBytes = 0;
            while ((read = stream.read(buffer)) != -1)
            {

               for (int i = 0; i < read; i++)
               {
                  if ((c = buffer[i]) != -1)
                  {
                     curr.append((char) c);
                     if ((--lineCounter == 0) || (c == '\n'))
                     {
                        lineCounter = lineWidth - 1;
                        markLine();
                     }
                  }
               }

               if ((totalBytes += read) > MAX_PREBUFFER)
               {
                  break;
               }
            }
            buffered = true;
            return read();
         }
      }

      public void seenLine()
      {
         bufferLine++;
      }

      public void markLine()
      {
         if (++totalLines % INDEX_MARK_SIZE == 0)
         {
            index.add(curr.length());
         }
      }

      public int getCurrentLine()
      {
         return bufferLine;
      }

      public void setLineWidth(final int lineWidth)
      {
         this.lineWidth = lineWidth;
      }

      public int findLine(final int line)
      {
         int idxMark = line / INDEX_MARK_SIZE;

         if (idxMark > index.size())
         {
            return curr.length() - 1;
         }
         else
         {
            int cursor = idxMark == 0 ? 0 : index.get(idxMark - 1);
            int currLine = idxMark * INDEX_MARK_SIZE;
            int lCount = lineWidth;

            while ((cursor < curr.length()) && (currLine != line))
            {
               switch (curr.charAt(cursor++))
               {
               case '\r':
                  cursor++;
                  continue;
               case '\n':
                  lCount = lineWidth;
                  currLine++;
                  continue;
               }

               if (--lCount <= -1)
               {
                  currLine++;
                  lCount = lineWidth;
               }
            }

            return cursor;
         }
      }

      public int findPattern(final String pattern, final boolean backwards) throws IOException
      {
         Pattern p = Pattern.compile(".*" + pattern + ".*");
         int currentBuffer = bufferPos;
         int currentLine = bufferLine;

         int startLine;
         int cursor = 0;
         if (backwards)
         {
            bufferLine = 0;
            startLine = 0;
            bufferPos = 0;
         }
         else
         {
            cursor = startLine = bufferPos = findLine(bufferLine);
         }

         int line = bufferLine;
         int lCount = lineWidth;

         byte[] buffer = new byte[128];
         int read;

         while ((read = read(buffer)) != -1)
         {
            for (int i = 0; i < read; i++)
            {
               cursor++;

               switch (buffer[i])
               {
               case '\r':
                  i++;
               case '\n':
                  line++;
                  lCount = lineWidth;
                  if (p.matcher(curr.subSequence(startLine, cursor - 1)).matches())
                  {
                     return line;
                  }

                  startLine = cursor;
               }

               if (--lCount <= 0)
               {
                  line++;
                  lCount = lineWidth;
               }
            }
         }

         bufferPos = currentBuffer;
         bufferLine = currentLine;
         return -1;
      }

      public void rewindBuffer(final int height, final int toLine)
      {
         int renderFrom = toLine - height;
         if (renderFrom < 0)
         {
            bufferLine = 0;
            bufferPos = 0;
         }
         else
         {
            bufferPos = findLine(renderFrom);
            bufferLine = renderFrom;
         }
      }

      public boolean atEnd()
      {
         return bufferLine >= totalLines;
      }
   }
}
