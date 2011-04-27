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
import org.jboss.forge.shell.plugins.*;

import javax.inject.Inject;

/**
 * @author Mike Brock
 */
@Alias("rm")
@Topic("File & Resources")
@Help("Removes a resource")
public class RmPlugin implements Plugin
{
   private final Shell shell;

   @Inject
   public RmPlugin(final Shell shell)
   {
      this.shell = shell;
   }

   @DefaultCommand
   public void rm(
            @Option(name = "recursive", shortName = "r", help = "recursively delete resources", flagOnly = true) final boolean recursive,
                  @Option(name = "force", shortName = "f", help = "do not prompt to confirm operations", flagOnly = true) final boolean force,
                  @Option(description = "path", required = true) final Resource<?>[] paths)
   {
      for (Resource<?> resource : paths)
      {
         if (force || shell.promptBoolean("delete: " + resource.getName() + ": are you sure?"))
         {
            if (!resource.delete(recursive))
            {
               throw new RuntimeException("error deleting files.");
            }
         }
      }
   }
}
