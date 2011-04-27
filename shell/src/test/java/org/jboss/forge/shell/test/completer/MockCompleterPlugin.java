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
package org.jboss.forge.shell.test.completer;

import org.jboss.forge.shell.plugins.Command;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.Plugin;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class MockCompleterPlugin implements Plugin
{
   private boolean defaultInvoked = false;
   private boolean command1Invoked = false;
   private boolean command2Invoked = false;
   private boolean command3Invoked = false;
   private boolean command4Invoked = false;

   @DefaultCommand
   public void defaultCommand()
   {
      defaultInvoked = true;
   }

   @Command("command1")
   public void command1(@Option(description = "Option One", required = true) final int number,
            @Option(required = false) final String optional)
   {
      command1Invoked = true;
   }

   @Command("command2")
   public void command2(@Option(name = "option", description = "Option Two", required = true) final int number,
            @Option(required = false) final String optional)
   {
      command2Invoked = true;
   }

   @Command("command3")
   public void command3(
            @Option(name = "option", description = "Option One", required = true) final int number,
            @Option(name = "option2",
                     description = "Option Two",
                     required = true,
                     defaultValue = "default",
                     completer = MockValueCompleter.class) final int number2,
            @Option(required = false) final String optional)
   {
      command3Invoked = true;
   }

   @Command("command4")
   public void command4(
            @Option(name = "option", description = "Option One", required = true) final MockEnum number)
   {
      command4Invoked = true;
   }

   public boolean isCommand1Invoked()
   {
      return command1Invoked;
   }

   public boolean isCommand2Invoked()
   {
      return command2Invoked;
   }

   public boolean isCommand3Invoked()
   {
      return command3Invoked;
   }

   public boolean isCommand4Invoked()
   {
      return command4Invoked;
   }

   public boolean isDefaultInvoked()
   {
      return defaultInvoked;
   }
}
