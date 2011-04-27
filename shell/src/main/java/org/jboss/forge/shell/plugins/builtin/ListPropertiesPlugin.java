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

import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.plugins.*;

import javax.inject.Inject;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Alias("list-properties")
@Topic("Shell Environment")
@Help("Lists all current shell properties")
public class ListPropertiesPlugin implements Plugin
{
   final Shell shell;

   @Inject
   public ListPropertiesPlugin(final Shell shell)
   {
      this.shell = shell;
   }

   @DefaultCommand
   public void listProperties()
   {
      Map<String, Object> properties = shell.getProperties();

      for (Entry<String, Object> entry : properties.entrySet())
      {
         String key = entry.getKey();
         Object value = entry.getValue();

         shell.print(key + "=");
         if (value != null)
         {
            shell.print(value.toString());
         }
         shell.println();
      }
   }
}
