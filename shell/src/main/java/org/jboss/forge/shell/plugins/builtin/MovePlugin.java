/*
 * JBoss, by Red Hat.
 * Copyright 2011, Red Hat, Inc., and individual contributors
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

import org.jboss.forge.project.services.ResourceFactory;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.resources.ResourceFlag;
import org.jboss.forge.shell.plugins.*;
import org.jboss.forge.shell.util.PathspecParser;

import javax.inject.Inject;
import java.util.List;

/**
 * Implementation of UNIX-style "mv" (move) command for use within the Forge Shell.
 * 
 * @author Mike Brock
 */
@Alias("mv")
@Topic("File & Resources")
@RequiresResource(DirectoryResource.class)
@Help("Renames a file or directory")
public class MovePlugin implements Plugin
{
   private final ResourceFactory resourceFactory;

   @Inject
   public MovePlugin(final ResourceFactory resourceFactory)
   {
      this.resourceFactory = resourceFactory;
   }

   @DefaultCommand
   public void rename(
            @Option(description = "source", required = true) final Resource<?> source,
                      @Option(description = "target", required = true) final String target,
                      @Option(name = "force", shortName = "f", description = "force operation", flagOnly = true) final boolean force,
                      final PipeOut out)
   {
      if (source instanceof FileResource)
      {
         Resource<?> sourceTarget = source.isFlagSet(ResourceFlag.Leaf) ? source.getParent() : source;
         List<Resource<?>> results = new PathspecParser(resourceFactory, sourceTarget, target).resolve();

         if (results.size() != 1)
         {
            out.println("ambiguous target file name: " + target);
         }
         else
         {
            Resource<?> targetResource = results.get(0);

            if (targetResource.exists())
            {
               if (targetResource instanceof DirectoryResource)
               {
                  targetResource = targetResource.getChild(source.getName());
               }
               else if (force && (targetResource instanceof FileResource))
               {
                  ((FileResource<?>) targetResource).delete(false);
               }
               else
               {
                  out.println("destination file exists: " + targetResource.getFullyQualifiedName());
                  return;
               }
            }

            ((FileResource<?>) source).renameTo(targetResource.getFullyQualifiedName());
         }
      }
      else

      {
         out.println("cannot rename resource type: " + source.getClass().getSimpleName());
      }
   }
}
