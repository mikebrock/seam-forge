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
package org.jboss.seam.forge.spec.javaee6.jpa;

import org.jboss.seam.forge.project.Project;
import org.jboss.seam.forge.project.dependencies.Dependency;
import org.jboss.seam.forge.project.dependencies.DependencyBuilder;
import org.jboss.seam.forge.project.dependencies.ScopeType;
import org.jboss.seam.forge.project.facets.DependencyFacet;
import org.jboss.seam.forge.project.facets.JavaSourceFacet;
import org.jboss.seam.forge.shell.ShellMessages;
import org.jboss.seam.forge.shell.ShellPrompt;
import org.jboss.seam.forge.shell.events.InstallFacets;
import org.jboss.seam.forge.shell.plugins.*;
import org.jboss.seam.forge.spec.javaee6.jpa.api.*;
import org.jboss.seam.forge.spec.javaee6.jpa.container.JavaEEDefaultContainer;
import org.jboss.shrinkwrap.descriptor.api.spec.jpa.persistence.PersistenceDescriptor;
import org.jboss.shrinkwrap.descriptor.api.spec.jpa.persistence.PersistenceUnitDef;

import javax.enterprise.event.Event;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
@Alias("persistence")
@RequiresFacet(JavaSourceFacet.class)
public class PersistencePlugin implements Plugin
{
   public static final String DEFAULT_UNIT_NAME = "forge-default";

   private static final String DEFAULT_UNIT_DESC = "Forge Persistence Unit";

   @Inject
   private Project project;

   @Inject
   private Event<InstallFacets> request;

   @Inject
   private ShellPrompt prompt;

   @Inject
   private BeanManager manager;

   @Command("setup")
   public void setup(
            @Option(name = "provider", required = true) JPAProvider jpap,
            @Option(name = "provider-version", required = false) String providerVersion,
            @Option(name = "container", required = true) JPAContainer jpac,
            @Option(name = "database", defaultValue = "DEFAULT") DatabaseType databaseType,
            @Option(name = "jndiDataSource") String jtaDataSource,
            @Option(name = "jdbcDriver") String jdbcDriver,
            @Option(name = "jdbcURL") String jdbcURL,
            @Option(name = "jdbcUsername") String jdbcUsername,
            @Option(name = "jdbcPassword") String jdbcPassword,
            @Option(name = "named", defaultValue = DEFAULT_UNIT_NAME) String unitName,
            PipeOut out)
   {
      installPersistence();
      DependencyFacet dependencyFacet = project.getFacet(DependencyFacet.class);
      PersistenceFacet jpa = project.getFacet(PersistenceFacet.class);
      PersistenceDescriptor config = jpa.getConfig();

      PersistenceUnitDef unit = config.persistenceUnit(unitName);

      unit.name(unitName).description(DEFAULT_UNIT_DESC);

      JPADataSource ds = new JPADataSource()
               .setJndiDataSource(jtaDataSource)
               .setDatabaseType(databaseType)
               .setJdbcDriver(jdbcDriver)
               .setDatabaseURL(jdbcURL)
               .setUsername(jdbcUsername)
               .setPassword(jdbcPassword);

      PersistenceContainer container = jpac.getContainer(manager);
      PersistenceProvider provider = jpap.getProvider(manager);

      container.setupConnection(unit, ds);
      provider.setup(unit, ds);

      jpa.saveConfig(config);

      List<Dependency> dependencies = new ArrayList<Dependency>();
      if (providerVersion != null)
      {
         for (Dependency dependency : provider.listDependencies())
         {
            if (container instanceof JavaEEDefaultContainer)
            {
               dependency = DependencyBuilder.create(dependency).setScopeType(ScopeType.PROVIDED);
            }
            dependencies.add(DependencyBuilder.create(dependency).setVersion(providerVersion));
         }
      }
      else
      {
         DependencyFacet deps = project.getFacet(DependencyFacet.class);
         List<Dependency> required = provider.listDependencies();

         for (Dependency dependency : required)
         {
            List<Dependency> versions = deps.resolveAvailableVersions(dependency);
            if (!versions.isEmpty())
            {
               Dependency choice = prompt.promptChoiceTyped(unitName, versions, versions.get(versions.size() - 1));

               if (container instanceof JavaEEDefaultContainer)
               {
                  choice = DependencyBuilder.create(choice).setScopeType(ScopeType.PROVIDED);
               }
               dependencies.add(choice);
            }
            else
            {
               ShellMessages.info(out, "Could not resolve versions for dependency [" + dependency + "]");
            }
         }
      }

      for (Dependency dependency : dependencies)
      {
         dependencyFacet.addDependency(dependency);
      }
   }

   private void installPersistence()
   {
      if (!project.hasFacet(PersistenceFacet.class))
      {
         request.fire(new InstallFacets(PersistenceFacet.class));
      }
   }
}
