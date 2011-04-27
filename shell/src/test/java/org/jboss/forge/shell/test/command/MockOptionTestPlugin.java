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
package org.jboss.forge.shell.test.command;

import org.jboss.forge.shell.PromptType;
import org.jboss.forge.shell.plugins.*;

import javax.inject.Singleton;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Alias("motp")
@Singleton
public class MockOptionTestPlugin implements Plugin
{
   private String suppliedOption = "";
   private String requiredOption = "";
   private Boolean booleanOptionOmitted = null;
   private String defaultCommandArg;

   @DefaultCommand
   public void defaultCommand(@Option(required = true) final String args)
   {
      setDefaultCommandArg(args);
   }

   @Command("suppliedOption")
   public void suppliedOption(@Option(name = "package",
            description = "Your java package",
            type = PromptType.JAVA_PACKAGE) final String option)
   {
      suppliedOption = option;
   }

   @Command("requiredOption")
   public void requiredOption(@Option(name = "package",
            required = true,
            description = "Your java package",
            type = PromptType.JAVA_PACKAGE) final String option)
   {
      requiredOption = option;
   }

   @Command("booleanOptionOmitted")
   public void booleanOptionOmitted(@Option(required = false,
            description = "Some boolean flag") final boolean option)
   {
      booleanOptionOmitted = option;
   }

   public String getSuppliedOption()
   {
      return suppliedOption;
   }

   public void setSuppliedOption(final String suppliedOption)
   {
      this.suppliedOption = suppliedOption;
   }

   public String getRequiredOption()
   {
      return requiredOption;
   }

   public void setRequiredOption(final String requiredOption)
   {
      this.requiredOption = requiredOption;
   }

   public Boolean getBooleanOptionOmitted()
   {
      return booleanOptionOmitted;
   }

   public void setBooleanOptionOmitted(final Boolean booleanOptionOmitted)
   {
      this.booleanOptionOmitted = booleanOptionOmitted;
   }

   public void setDefaultCommandArg(final String defaultCommandArg)
   {
      this.defaultCommandArg = defaultCommandArg;
   }

   public String getDefaultCommandArg()
   {
      return defaultCommandArg;
   }
}
