/*
 * JBoss, Home of Professional Open Source
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
package org.metawidget.forge;

import org.jboss.seam.forge.parser.JavaParser;
import org.jboss.seam.forge.parser.java.JavaClass;
import org.jboss.seam.forge.parser.java.util.Refactory;
import org.jboss.seam.forge.project.Project;
import org.jboss.seam.forge.project.dependencies.Dependency;
import org.jboss.seam.forge.project.dependencies.DependencyBuilder;
import org.jboss.seam.forge.project.facets.DependencyFacet;
import org.jboss.seam.forge.project.facets.JavaSourceFacet;
import org.jboss.seam.forge.project.facets.WebResourceFacet;
import org.jboss.seam.forge.resources.Resource;
import org.jboss.seam.forge.resources.java.JavaResource;
import org.jboss.seam.forge.scaffold.AccessStrategy;
import org.jboss.seam.forge.scaffold.ScaffoldProvider;
import org.jboss.seam.forge.scaffold.plugins.ScaffoldPlugin;
import org.jboss.seam.forge.shell.ShellMessages;
import org.jboss.seam.forge.shell.ShellPrintWriter;
import org.jboss.seam.forge.shell.ShellPrompt;
import org.jboss.seam.forge.shell.events.InstallFacets;
import org.jboss.seam.forge.shell.plugins.Alias;
import org.jboss.seam.forge.spec.javaee6.cdi.CDIFacet;
import org.jboss.seam.forge.spec.javaee6.jpa.PersistenceFacet;
import org.jboss.seam.forge.spec.javaee6.jsf.FacesFacet;
import org.jboss.seam.forge.spec.javaee6.servlet.ServletFacet;
import org.jboss.seam.render.TemplateCompiler;
import org.jboss.seam.render.template.CompiledTemplateResource;
import org.jboss.shrinkwrap.descriptor.api.Node;
import org.jboss.shrinkwrap.descriptor.api.spec.cdi.beans.BeansDescriptor;
import org.jboss.shrinkwrap.descriptor.impl.spec.servlet.web.WebAppDescriptorImpl;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
@Alias("metawidget")
public class MetawidgetScaffold implements ScaffoldProvider
{
   private static final String PARTIAL_STATE_SAVING = "javax.faces.PARTIAL_STATE_SAVING";
   private static final String SEAM_PERSIST_TRANSACTIONAL_ANNO = "org.jboss.seam.transaction.Transactional";
   private static final String SEAM_PERSIST_INTERCEPTOR = "org.jboss.seam.transaction.TransactionInterceptor";
   private static final String METAWIDGET_DISABLE_EVENT = "org.metawidget.faces.component.DONT_USE_PRERENDER_VIEW_EVENT";

   private static final String BACKING_BEAN_TEMPLATE = "org/metawidget/scaffold/BackingBean.jv";
   private static final String VIEW_TEMPLATE = "org/metawidget/scaffold/view.xhtml";
   private static final String CREATE_TEMPLATE = "org/metawidget/scaffold/create.xhtml";
   private static final String LIST_TEMPLATE = "org/metawidget/scaffold/list.xhtml";
   private static final String CONFIG_TEMPLATE = "org/metawidget/metawidget.xml";

   private final Dependency metawidget = DependencyBuilder.create("org.metawidget:metawidget");
   private final Dependency seamPersist = DependencyBuilder
            .create("org.jboss.seam.persistence:seam-persistence:[3.0.0-SNAPSHOT],[3.0.0.CR4,)");

   private CompiledTemplateResource viewTemplate;
   private CompiledTemplateResource createTemplate;
   private CompiledTemplateResource listTemplate;
   private CompiledTemplateResource configTemplate;

   @Inject
   private ShellPrompt prompt;

   @Inject
   private ShellPrintWriter writer;

   @Inject
   private TemplateCompiler compiler;

   @Inject
   private Event<InstallFacets> install;

   @PostConstruct
   public void init()
   {
      viewTemplate = compiler.compile(VIEW_TEMPLATE);
      createTemplate = compiler.compile(CREATE_TEMPLATE);
      listTemplate = compiler.compile(LIST_TEMPLATE);
      configTemplate = compiler.compile(CONFIG_TEMPLATE);
   }

   @Override
   public List<Resource<?>> generateFromEntity(final Project project, final JavaClass entity, final boolean overwrite)
   {
      List<Resource<?>> result = new ArrayList<Resource<?>>();
      try
      {
         JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
         WebResourceFacet web = project.getFacet(WebResourceFacet.class);

         if (!entity.hasMethodSignature("toString"))
         {
            Refactory.createToStringFromFields(entity);
            ShellMessages.info(writer, "Entity [" + entity.getName()
                     + "] does not have a .toString() method. Generating...");
            result.add(ScaffoldPlugin.createOrOverwrite(prompt, java.getJavaResource(entity), entity.toString(),
                     overwrite));
         }

         CompiledTemplateResource backingBeanTemplate = compiler.compile(BACKING_BEAN_TEMPLATE);
         HashMap<Object, Object> context = new HashMap<Object, Object>();
         context.put("entity", entity);

         // Create the Backing Bean for this entity
         JavaClass viewBean = JavaParser.parse(JavaClass.class, backingBeanTemplate.render(context));
         viewBean.setPackage(java.getBasePackage() + ".view");
         viewBean.addAnnotation(SEAM_PERSIST_TRANSACTIONAL_ANNO);
         result.add(ScaffoldPlugin.createOrOverwrite(prompt, java.getJavaResource(viewBean), viewBean.toString(),
                  overwrite));

         // Set context for view generation
         context = new HashMap<Object, Object>();
         String name = viewBean.getName();
         name = name.substring(0, 1).toLowerCase() + name.substring(1);
         context.put("beanName", name);
         context.put("entity", entity);

         // Generate views
         String type = entity.getName().toLowerCase();
         result.add(ScaffoldPlugin.createOrOverwrite(prompt, web.getWebResource("scaffold/" + type + "/view.xhtml"),
                  viewTemplate.render(context), overwrite));
         result.add(ScaffoldPlugin.createOrOverwrite(prompt, web.getWebResource("scaffold/" + type + "/create.xhtml"),
                  createTemplate.render(context),
                  overwrite));
         result.add(ScaffoldPlugin.createOrOverwrite(prompt, web.getWebResource("scaffold/" + type + "/list.xhtml"),
                  listTemplate.render(context), overwrite));
      }
      catch (Exception e)
      {
         throw new RuntimeException("Error generating default scaffolding.", e);
      }
      return result;
   }

   public void createMetawidgetConfig(final Project project, final boolean overwrite)
   {
      WebResourceFacet web = project.getFacet(WebResourceFacet.class);

      ScaffoldPlugin.createOrOverwrite(prompt, web.getWebResource("WEB-INF/metawidget.xml"),
               configTemplate.render(new HashMap<Object, Object>()), overwrite);
   }

   public void createPersistenceUtils(final Project project, final boolean overwrite)
   {
      JavaClass util = JavaParser.parse(JavaClass.class,
               getClass().getResourceAsStream("/org/metawidget/persistence/PersistenceUtil.jv"));
      JavaClass producer = JavaParser.parse(JavaClass.class,
               getClass().getResourceAsStream("/org/metawidget/persistence/DatasourceProducer.jv"));
      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);

      try
      {
         JavaResource producerResource = java.getJavaResource(producer);
         JavaResource utilResource = java.getJavaResource(util);

         ScaffoldPlugin.createOrOverwrite(prompt, producerResource, producer.toString(), overwrite);
         ScaffoldPlugin.createOrOverwrite(prompt, utilResource, util.toString(), overwrite);
      }
      catch (FileNotFoundException e)
      {
         throw new RuntimeException(e);
      }
   }

   @Override
   @SuppressWarnings("unchecked")
   public List<Resource<?>> install(final Project project)
   {
      if (!(project.hasFacet(WebResourceFacet.class) && project.hasFacet(PersistenceFacet.class)
               && project.hasFacet(CDIFacet.class) && project.hasFacet(FacesFacet.class)))
      {
         install.fire(new InstallFacets(WebResourceFacet.class, PersistenceFacet.class, CDIFacet.class,
                  FacesFacet.class));
      }

      DependencyFacet df = project.getFacet(DependencyFacet.class);
      CDIFacet cdi = project.getFacet(CDIFacet.class);
      ServletFacet servlet = project.getFacet(ServletFacet.class);
      if (!df.hasDependency(metawidget))
      {
         df.addDependency(prompt.promptChoiceTyped("Install which version of Metawidget?",
                  df.resolveAvailableVersions(metawidget)));
      }

      // fixme this needs to be fixed in SHRINKDESC
      WebAppDescriptorImpl webxml = (WebAppDescriptorImpl) servlet.getConfig();

      List<Node> list = webxml.getRootNode().get("context-param/param-name");

      // Hack to support JSF2 and metawidget
      ShellMessages.info(writer, "JSF2 and Metawidget currently require Partial State Saving to be disabled.");
      boolean pssUpdated = false;
      boolean mweUpdated = false;
      for (Node node : list)
      {
         if (PARTIAL_STATE_SAVING.equals(node.text()))
         {
            node.parent().getOrCreate("param-value").text("false");
            pssUpdated = true;
            continue;
         }
         if (METAWIDGET_DISABLE_EVENT.equals(node.text()))
         {
            node.parent().getOrCreate("param-value").text("true");
            continue;
         }
      }
      if (!mweUpdated)
      {
         webxml.contextParam(METAWIDGET_DISABLE_EVENT, "true");
      }
      if (!pssUpdated)
      {
         webxml.contextParam(PARTIAL_STATE_SAVING, "false");
      }
      servlet.saveConfig(webxml);

      if (!df.hasDependency(seamPersist))
      {
         df.addDependency(prompt.promptChoiceTyped("Install which version of Seam Persistence?",
                  df.resolveAvailableVersions(seamPersist)));

         BeansDescriptor config = cdi.getConfig();
         config.interceptor(SEAM_PERSIST_INTERCEPTOR);
         cdi.saveConfig(config);
      }
      createPersistenceUtils(project, true);
      createMetawidgetConfig(project, true);
      return generateTemplates(project, true);
   }

   @Override
   public boolean installed(final Project project)
   {
      DependencyFacet df = project.getFacet(DependencyFacet.class);

      return df.hasDependency(metawidget) && df.hasDependency(seamPersist)
               && project.hasFacet(WebResourceFacet.class)
               && project.hasFacet(PersistenceFacet.class)
               && project.hasFacet(CDIFacet.class)
               && project.hasFacet(FacesFacet.class)
               && project.getFacet(CDIFacet.class).getConfig().getInterceptors().contains(SEAM_PERSIST_INTERCEPTOR)
               && project.getFacet(WebResourceFacet.class).getWebResource("/WEB-INF/metawidget.xml").exists();
   }

   @Override
   public List<Resource<?>> generateIndex(Project project, boolean overwrite)
   {
      List<Resource<?>> result = new ArrayList<Resource<?>>();
      WebResourceFacet web = project.getFacet(WebResourceFacet.class);

      project.getFacet(ServletFacet.class).getConfig().welcomeFile("index.html");

      result.add(ScaffoldPlugin.createOrOverwrite(prompt, web.getWebResource("index.html"), getClass()
               .getResourceAsStream("/org/metawidget/templates/index.html"), overwrite));

      result.add(ScaffoldPlugin.createOrOverwrite(prompt, web.getWebResource("index.xhtml"),
               getClass().getResourceAsStream("/org/metawidget/templates/index.xhtml"), overwrite));

      generateTemplates(project, overwrite);
      return result;
   }

   @Override
   public List<Resource<?>> getGeneratedResources(Project project)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public AccessStrategy getAccessStrategy(Project project)
   {
      final FacesFacet faces = project.getFacet(FacesFacet.class);

      return new AccessStrategy()
      {
         @Override
         public List<String> getWebPaths(Resource<?> r)
         {
            return faces.getWebPaths(r);
         }

         @Override
         public Resource<?> fromWebPath(String path)
         {
            return faces.getResourceForWebPath(path);
         }
      };
   }

   @Override
   public List<Resource<?>> generateTemplates(Project project, boolean overwrite)
   {
      List<Resource<?>> result = new ArrayList<Resource<?>>();
      WebResourceFacet web = project.getFacet(WebResourceFacet.class);

      result.add(ScaffoldPlugin.createOrOverwrite(prompt, web.getWebResource("/resources/forge-template.xhtml"),
               getClass().getResourceAsStream("/org/metawidget/templates/forge-template.xhtml"), overwrite));

      result.add(ScaffoldPlugin.createOrOverwrite(prompt, web.getWebResource("/resources/forge.css"),
               getClass().getResourceAsStream("/org/metawidget/templates/forge.css"), overwrite));

      return result;
   }

}
