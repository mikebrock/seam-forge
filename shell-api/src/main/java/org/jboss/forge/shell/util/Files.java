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

package org.jboss.forge.shell.util;

import java.io.File;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class Files
{
   public static final String HOME_ALIAS = "~";
   public static final String SLASH = File.separator;

   /**
    * Replace instances of internal tokens with actual file equivalents.
    */
   public static String canonicalize(String target)
   {
      if (target.startsWith(Files.HOME_ALIAS))
      {
         String homePath = OSUtils.getUserHomePath();
         target = homePath + target.substring(1, target.length());
      }

      return target;
   }

   public static File getWorkingDirectory()
   {
      return new File("").getAbsoluteFile();
   }

}
