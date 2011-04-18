/*
 * JBoss, Home of Professional Open Source
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

package org.jboss.seam.forge.scaffold.plugins;

import org.jboss.seam.forge.parser.java.JavaClass;
import org.jboss.seam.forge.parser.java.JavaSource;
import org.jboss.seam.forge.project.Project;
import org.jboss.seam.forge.project.facets.WebResourceFacet;
import org.jboss.seam.forge.resources.FileResource;
import org.jboss.seam.forge.resources.Resource;
import org.jboss.seam.forge.resources.java.JavaResource;
import org.jboss.seam.forge.scaffold.ScaffoldProvider;
import org.jboss.seam.forge.scaffold.plugins.events.ScaffoldGeneratedResources;
import org.jboss.seam.forge.scaffold.shell.ScaffoldProviderCompleter;
import org.jboss.seam.forge.shell.ShellMessages;
import org.jboss.seam.forge.shell.ShellPrompt;
import org.jboss.seam.forge.shell.plugins.*;
import org.jboss.seam.forge.shell.util.ConstraintInspector;

import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.Entity;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
@Alias("scaffold")
@Topic("UI Generation & Scaffolding")
@Help("Metawidget UI scaffolding")
@RequiresProject
public class ScaffoldPlugin implements Plugin
{
   @Inject
   @Current
   private Resource<?> currentResource;

   @Inject
   private Project project;
   @Inject
   private ShellPrompt prompt;

   @Inject
   private Instance<ScaffoldProvider> impls;

   @Inject
   private Event<ScaffoldGeneratedResources> generatedEvent;

   @Command("gen-indexes")
   public void generateIndex(
            final PipeOut out,
            @Option(name = "scaffoldType", required = false,
                     completer = ScaffoldProviderCompleter.class) final String scaffoldType,
            @Option(flagOnly = true, name = "overwrite") final boolean overwrite)
   {
      ScaffoldProvider provider = getScaffoldType(scaffoldType);
      List<Resource<?>> generatedResources = provider.generateIndex(project, overwrite);

      // TODO give plugins a chance to react to generated resources, use event bus?
      if (!generatedResources.isEmpty())
         generatedEvent.fire(new ScaffoldGeneratedResources(provider, generatedResources));
   }

   @Command("gen-templates")
   public void generateTemplates(
            @Option(name = "scaffoldType", required = false,
                     completer = ScaffoldProviderCompleter.class) final String scaffoldType,
            final PipeOut out,
            @Option(flagOnly = true, name = "overwrite") final boolean overwrite)
   {
      ScaffoldProvider provider = getScaffoldType(scaffoldType);
      List<Resource<?>> generatedResources = provider.generateTemplates(project, overwrite);

      // TODO give plugins a chance to react to generated resources, use event bus?
      if (!generatedResources.isEmpty())
         generatedEvent.fire(new ScaffoldGeneratedResources(provider, generatedResources));
   }

   private ScaffoldProvider getScaffoldType(String scaffoldType)
   {
      ScaffoldProvider scaffoldImpl = null;
      if (scaffoldType == null
               && prompt.promptBoolean("No scaffold type was selected, use default (Metawidget & JSF)?"))
      {
         scaffoldType = "metawidget";
      }
      else if (scaffoldType == null)
      {
         throw new RuntimeException("Re-run with --scaffoldType {...}");
      }

      for (ScaffoldProvider type : impls)
      {
         if (ConstraintInspector.getName(type.getClass()).equals(scaffoldType))
         {
            scaffoldImpl = type;
         }
      }

      if (!scaffoldImpl.installed(project)
               && prompt.promptBoolean("Scaffold provider [" + scaffoldType + "] is not installed. Install it?"))
      {
         scaffoldImpl.install(project);
      }
      else if (!scaffoldImpl.installed(project))
      {
         throw new RuntimeException("Aborted.");
      }

      if (project.hasFacet(WebResourceFacet.class))
      {
         FileResource<?> favicon = project.getFacet(WebResourceFacet.class).getWebResource("/favicon.ico");
         if (!favicon.exists())
         {
            favicon.setContents(getClass().getResourceAsStream("/org/jboss/seam/forge/scaffold/favicon.ico"));
         }
      }

      return scaffoldImpl;
   }

   private List<JavaResource> selectTargets(final PipeOut out, Resource<?>[] targets)
            throws FileNotFoundException
   {
      List<JavaResource> results = new ArrayList<JavaResource>();
      if (targets == null)
      {
         targets = new Resource<?>[] {};
      }
      for (Resource<?> r : targets)
      {
         if (r instanceof JavaResource)
         {
            JavaSource<?> entity = ((JavaResource) r).getJavaSource();
            if (entity instanceof JavaClass)
            {
               if (entity.hasAnnotation(Entity.class))
               {
                  results.add((JavaResource) r);
               }
               else
               {
                  displaySkippingResourceMsg(out, entity);
               }
            }
            else
            {
               displaySkippingResourceMsg(out, entity);
            }
         }
      }
      return results;
   }

   private void displaySkippingResourceMsg(final PipeOut out, final JavaSource<?> entity)
   {
      if (!out.isPiped())
      {
         ShellMessages.info(out, "Skipped non-@Entity Java resource ["
                  + entity.getQualifiedName() + "]");
      }
   }

   @Command("gen-from-entity")
   public void generateFromEntity(
            @Option(name = "scaffoldType", required = false,
                     completer = ScaffoldProviderCompleter.class) final String scaffoldType,
            @Option(flagOnly = true, name = "overwrite") final boolean overwrite,
            @Option(required = false) JavaResource[] targets,
            final PipeOut out) throws FileNotFoundException
   {
      if (((targets == null) || (targets.length < 1))
               && (currentResource instanceof JavaResource))
      {
         targets = new JavaResource[] { (JavaResource) currentResource };
      }

      List<JavaResource> javaTargets = selectTargets(out, targets);
      if (javaTargets.isEmpty())
      {
         ShellMessages.error(out, "Must specify a domain entity on which to operate.");
         return;
      }

      ScaffoldProvider provider = getScaffoldType(scaffoldType);

      for (JavaResource jr : javaTargets)
      {
         JavaClass entity = (JavaClass) (jr).getJavaSource();
         List<Resource<?>> generatedResources = provider.generateFromEntity(project, entity, overwrite);

         // TODO give plugins a chance to react to generated resources, use event bus?
         if (!generatedResources.isEmpty())
            generatedEvent.fire(new ScaffoldGeneratedResources(provider, generatedResources));

         ShellMessages.success(out, "Generated UI for [" + entity.getQualifiedName() + "]");
      }

   }

   public static Resource<?> createOrOverwrite(final ShellPrompt prompt, final FileResource<?> resource,
            final InputStream contents,
            final boolean overwrite)
   {
      if (!resource.exists() || overwrite
               || prompt.promptBoolean("[" + resource.getFullyQualifiedName() + "] File exists, overwrite?"))
      {
         resource.createNewFile();
         resource.setContents(contents);
         return resource;
      }
      return null;
   }

   public static Resource<?> createOrOverwrite(final ShellPrompt prompt, final FileResource<?> resource,
            final String contents,
            final boolean overwrite)
   {
      if (!resource.exists() || overwrite
               || prompt.promptBoolean("[" + resource.getFullyQualifiedName() + "] File exists, overwrite?"))
      {
         resource.createNewFile();
         resource.setContents(contents);
         return resource;
      }
      return null;
   }
}
