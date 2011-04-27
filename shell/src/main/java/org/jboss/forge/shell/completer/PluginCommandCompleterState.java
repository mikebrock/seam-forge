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
package org.jboss.forge.shell.completer;

import org.jboss.forge.shell.command.CommandMetadata;
import org.jboss.forge.shell.command.OptionMetadata;
import org.jboss.forge.shell.command.PluginMetadata;
import org.jboss.forge.shell.command.parser.CommandParserContext;

/**
 * Holds state during TAB completion.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class PluginCommandCompleterState extends BaseCommandCompleterState
{

   private PluginMetadata plugin;
   private CommandMetadata command;
   private OptionMetadata option;
   private CommandParserContext commandContext;

   public PluginCommandCompleterState(final String initialBuffer, final String lastBuffer, final int initialIndex)
   {
      super(initialBuffer, lastBuffer, initialIndex);
   }

   /*
    * Mutable state
    */
   public PluginMetadata getPlugin()
   {
      return plugin;
   }

   public void setPlugin(final PluginMetadata plugin)
   {
      this.plugin = plugin;
   }

   public CommandMetadata getCommand()
   {
      return command;
   }

   public void setCommand(final CommandMetadata command)
   {
      this.command = command;
   }

   public OptionMetadata getOption()
   {
      return option;
   }

   public void setOption(final OptionMetadata option)
   {
      this.option = option;
   }

   public CommandParserContext getCommandContext()
   {
      return commandContext;
   }

   public void setCommandContext(final CommandParserContext commandContext)
   {
      this.commandContext = commandContext;
   }
}
