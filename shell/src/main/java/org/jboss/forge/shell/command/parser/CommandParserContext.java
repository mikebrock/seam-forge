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

package org.jboss.forge.shell.command.parser;

import org.jboss.forge.shell.command.OptionMetadata;

import java.util.*;

/**
 * @author Mike Brock .
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class CommandParserContext
{
   private int paramCount;
   private final Map<OptionMetadata, Object> valueMap = new HashMap<OptionMetadata, Object>();
   private OptionMetadata lastParsed;
   private String lastParsedToken;
   private final List<String> warnings = new ArrayList<String>();
   private final List<String> ignoredTokens = new ArrayList<String>();

   public void incrementParmCount()
   {
      paramCount++;
   }

   public void addWarning(String message)
   {
      if (!warnings.contains(message))
         warnings.add(message);
   }

   public void addIgnoredToken(String token)
   {
      ignoredTokens.add(token);
   }

   public int getParamCount()
   {
      return paramCount;
   }

   @Override
   public String toString()
   {
      return "CommandParserContext [paramCount=" + paramCount + "]";
   }

   /**
    * Return an unmodifiable view of the parsed statement options.
    */
   public Map<OptionMetadata, Object> getValueMap()
   {
      return Collections.unmodifiableMap(valueMap);
   }

   public List<String> getWarnings()
   {
      return Collections.unmodifiableList(warnings);
   }

   public List<String> getIgnoredTokens()
   {
      return Collections.unmodifiableList(ignoredTokens);
   }

   /**
    * Return a count of how many ordered params have already been parsed.
    */
   public int getOrderedParamCount()
   {
      int result = 0;
      for (OptionMetadata option : valueMap.keySet())
      {
         if (option.isOrdered())
         {
            result++;
         }
      }
      return result;
   }

   public void put(final OptionMetadata option, final Object value, final String rawValue)
   {
      lastParsed = option;
      valueMap.put(option, value);
      lastParsedToken = rawValue;
   }

   /**
    * @return the last parsed {@link OptionMetadata}
    */
   public OptionMetadata getLastParsed()
   {
      return lastParsed;
   }

   public boolean isLastOptionValued()
   {
      return (lastParsed != null) && (valueMap.get(lastParsed) != null);
   }

   public boolean isEmpty()
   {
      return valueMap.isEmpty();
   }

   public String getLastParsedToken()
   {
      return lastParsedToken;
   }

}
