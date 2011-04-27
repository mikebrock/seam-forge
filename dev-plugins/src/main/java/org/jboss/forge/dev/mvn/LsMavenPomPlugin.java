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

package org.jboss.forge.dev.mvn;

import org.apache.maven.model.Dependency;
import org.jboss.forge.maven.resources.MavenDependencyResource;
import org.jboss.forge.maven.resources.MavenPomResource;
import org.jboss.forge.maven.resources.MavenProfileResource;
import org.jboss.forge.maven.resources.MavenRepositoryResource;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.shell.ShellColor;
import org.jboss.forge.shell.plugins.*;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * LsMavenPomPlugin
 * 
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @version $Revision: $
 */
@Alias("ls")
@RequiresResource(MavenPomResource.class)
@Topic("File & Resources")
@Help("Prints the contents current pom file")
public class LsMavenPomPlugin implements Plugin
{
   @Inject
   @Current
   private MavenPomResource pom;

   @DefaultCommand
   public void run(
            @Option(flagOnly = true, name = "all", shortName = "a", required = false) final boolean showAll,
                   @Option(flagOnly = true, name = "list", shortName = "l", required = false) final boolean list,
                   @Option(description = "path", defaultValue = ".") final Resource<?>[] paths,
                   final PipeOut out) throws IOException
   {
      if (showAll)
      {
         InputStream stream = pom.getResourceInputStream();
         StringBuilder buf = new StringBuilder();

         int c;
         while ((c = stream.read()) != -1)
         {
            buf.append((char) c);
         }
         out.println(buf.toString());
      }
      else
      {

         out.println();
         out.println(out.renderColor(ShellColor.RED, "[dependencies] "));
         List<Resource<?>> children = pom.listResources();
         for (Resource<?> child : children)
         {
            if (child instanceof MavenDependencyResource)
            {
               MavenDependencyResource resource = (MavenDependencyResource) child;
               Dependency dep = resource.getDependency();
               out.println(
                        out.renderColor(ShellColor.BLUE, dep.getGroupId())
                                 +
                                 out.renderColor(ShellColor.BOLD, " : ")
                                 +
                                 out.renderColor(ShellColor.BLUE, dep.getArtifactId())
                                 +
                                 out.renderColor(ShellColor.BOLD, " : ")
                                 +
                                 out.renderColor(ShellColor.NONE, dep.getVersion() == null ? "" : dep.getVersion())
                                 +
                                 out.renderColor(ShellColor.BOLD, " : ")
                                 +
                                 out.renderColor(ShellColor.NONE, dep.getType() == null ? "" : dep
                                          .getType().toLowerCase())
                                 +
                                 out.renderColor(ShellColor.BOLD, " : ")
                                 +
                                 out.renderColor(determineDependencyShellColor(dep.getScope()),
                                          dep.getScope() == null ? "compile" : dep.getScope()
                                                   .toLowerCase()));
            }
         }

         out.println();
         out.println(out.renderColor(ShellColor.RED, "[profiles] "));

         for (Resource<?> child : children)
         {
            if (child instanceof MavenProfileResource)
            {
               out.println(out.renderColor(ShellColor.BLUE, child.getName()));
            }
         }

         out.println();
         out.println(out.renderColor(ShellColor.RED, "[repositories] "));

         for (Resource<?> child : children)
         {
            if (child instanceof MavenRepositoryResource)
            {
               out.println(out.renderColor(ShellColor.BLUE, child.getName()) + " -> "
                        + ((MavenRepositoryResource) child).getURL());
            }
         }

      }
   }

   private ShellColor determineDependencyShellColor(final String string)
   {
      if (string == null)
      {
         return ShellColor.YELLOW;
      }
      if ("provided".equalsIgnoreCase(string))
         return ShellColor.GREEN;
      else if ("compile".equalsIgnoreCase(string))
         return ShellColor.YELLOW;
      else if ("runtime".equalsIgnoreCase(string))
         return ShellColor.MAGENTA;
      else if ("system".equalsIgnoreCase(string))
         return ShellColor.BLACK;
      else if ("test".equalsIgnoreCase(string))
         return ShellColor.BLUE;

      return ShellColor.NONE;
   }
}