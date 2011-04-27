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

package org.jboss.forge.dev.mvn;

import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.parser.java.util.Assert;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.services.ProjectFactory;
import org.jboss.forge.project.services.ResourceFactory;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.shell.PromptType;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.ShellColor;
import org.jboss.forge.shell.plugins.*;
import org.jboss.forge.shell.util.PathspecParser;

import javax.inject.Inject;
import java.util.List;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Alias("maven")
@Topic("Project")
@RequiresProject
@RequiresFacet(MavenCoreFacet.class)
@RequiresResource(DirectoryResource.class)
public class MavenPlugin implements Plugin
{
   private final Shell shell;
   private final Project project;
   private final ProjectFactory factory;
   private final ResourceFactory resources;

   @Inject
   public MavenPlugin(final Shell shell, final Project project, final ProjectFactory factory,
            final ResourceFactory resources)
   {
      this.shell = shell;
      this.project = project;
      this.factory = factory;
      this.resources = resources;
   }

   @Command("set-parent")
   public void setParent(
            @Option(name = "parentId",
                     description = "dependency identifier of parent, ex: \"org.jboss.forge:forge-parent:1.0.0\"",
                     required = false) final Dependency gav,
            @Option(name = "parentRelativePath",
                     description = "relative location from the current project to the parent project root folder",
                     type = PromptType.FILE_PATH,
                     required = false) final String relativePath,
            @Option(name = "parentProjectRoot",
                     description = "absolute location of a project to use as this project's direct parent",
                     required = false) final Resource<?> path,
            final PipeOut out)
   {
      MavenCoreFacet mvn = project.getFacet(MavenCoreFacet.class);
      Parent parent = null;
      if (gav != null)
      {
         Assert.notNull(gav.getArtifactId(), "ArtifactId must not be null [" + gav.toCoordinates() + "]");
         Assert.notNull(gav.getGroupId(), "GroupId must not be null [" + gav.toCoordinates() + "]");
         Assert.notNull(gav.getVersion(), "Version must not be null [" + gav.toCoordinates() + "]");

         parent = new Parent();
         parent.setArtifactId(gav.getArtifactId());
         parent.setGroupId(gav.getGroupId());
         parent.setVersion(gav.getVersion());

         if (relativePath != null)
         {
            parent.setRelativePath(relativePath);
         }

         Model pom = mvn.getPOM();
         pom.setParent(parent);
         mvn.setPOM(pom);
      }
      else if ((path != null) && factory.containsProject(path.reify(DirectoryResource.class)))
      {
         Project parentProject = factory.findProject(path.reify(DirectoryResource.class));
         MavenCoreFacet parentCore = parentProject.getFacet(MavenCoreFacet.class);

         parent = new Parent();
         parent.setArtifactId(parentCore.getMavenProject().getArtifactId());
         parent.setGroupId(parentCore.getMavenProject().getGroupId());
         parent.setVersion(parentCore.getMavenProject().getVersion());

         if (relativePath != null)
         {
            parent.setRelativePath(relativePath);
         }

         Model pom = mvn.getPOM();
         pom.setParent(parent);
         mvn.setPOM(pom);
      }
      else if (relativePath != null)
      {
         PathspecParser parser = new PathspecParser(resources, shell.getCurrentProject().getProjectRoot(), relativePath);
         List<Resource<?>> resolvedResources = parser.resolve();
         if (!resolvedResources.isEmpty()
                  && factory.containsProject(resolvedResources.get(0).reify(DirectoryResource.class)))
         {
            Project parentProject = factory.findProject(resolvedResources.get(0).reify(DirectoryResource.class));
            MavenCoreFacet parentCore = parentProject.getFacet(MavenCoreFacet.class);

            parent = new Parent();
            parent.setArtifactId(parentCore.getMavenProject().getArtifactId());
            parent.setGroupId(parentCore.getMavenProject().getGroupId());
            parent.setVersion(parentCore.getMavenProject().getVersion());
            parent.setRelativePath(relativePath);

            Model pom = mvn.getPOM();
            pom.setParent(parent);
            mvn.setPOM(pom);
         }
         else
         {
            out.print(ShellColor.RED, "***ERROR***");
            out.println(" relative path did not resolve to a Project [" + relativePath + "]");
         }
      }
      else
      {
         out.print(ShellColor.RED, "***ERROR***");
         out.println(" you must specify a path to or dependency id of the parent project.");
      }

      if (parent != null)
      {
         String parentId = parent.getGroupId() + ":" + parent.getArtifactId() + ":"
                  + parent.getVersion() + " ("
                  + (parent.getRelativePath() == null ? " " : parent.getRelativePath() + ")");

         out.println("Set parent [ " + parentId + " ]");
      }
   }

   @Command("remove-parent")
   public void removeParent(final PipeOut out)
   {
      MavenCoreFacet mvn = project.getFacet(MavenCoreFacet.class);

      Model pom = mvn.getPOM();
      Parent parent = pom.getParent();

      if (parent != null)
      {
         String parentId = parent.getGroupId() + ":" + parent.getArtifactId() + ":"
                  + parent.getVersion() + " ("
                  + (parent.getRelativePath() == null ? " " : parent.getRelativePath() + ")");

         if (shell.promptBoolean("Are you sure you want to remove all parent information from this project? [ "
                  + parentId + "]", false))
         {
            out.println("Removed parent [ " + parentId + " ]");
            pom.setParent(null);
            mvn.setPOM(pom);
         }
         else
         {
            out.println("Aborted...");
         }
      }
      else
      {
         out.println("Nothing to remove...");
      }
   }
}
