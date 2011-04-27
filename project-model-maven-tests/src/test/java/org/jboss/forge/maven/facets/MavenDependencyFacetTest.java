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

package org.jboss.forge.maven.facets;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.maven.util.ProjectModelTest;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.services.ProjectFactory;
import org.jboss.forge.project.services.ResourceFactory;
import org.jboss.forge.shell.util.ResourceUtil;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class MavenDependencyFacetTest extends ProjectModelTest
{
   @Deployment
   public static JavaArchive getTestArchive()
   {
      return createTestArchive()
               .addManifestResource(
                        "META-INF/services/org.jboss.forge.project.dependencies.DependencyResolverProvider");
   }

   @Inject
   private ProjectFactory projectFactory;

   @Inject
   private ResourceFactory resourceFactory;

   private static Project testProject;

   @Before
   @Override
   public void postConstruct() throws IOException
   {
      super.postConstruct();

      if (testProject == null)
      {
         testProject = projectFactory.findProjectRecursively(
                  ResourceUtil.getContextDirectory(resourceFactory.getResourceFrom(new File(
                           "src/test/resources/test-pom"))));
      }
   }

   @Test
   public void testHasDependency() throws Exception
   {
      DependencyFacet deps = testProject.getFacet(DependencyFacet.class);

      DependencyBuilder prettyfaces = DependencyBuilder.create("com.ocpsoft:prettyfaces-jsf2:3.0.2-SNAPSHOT");
      assertTrue(deps.hasDependency(prettyfaces));
   }

   @Test
   public void testAddDependency() throws Exception
   {
      Dependency dependency =
               DependencyBuilder.create("org.jboss:test-dependency:1.0.0.Final");

      Project project = getProject();
      DependencyFacet deps = project.getFacet(DependencyFacet.class);
      assertFalse(deps.hasDependency(dependency));
      deps.addDependency(dependency);
      assertTrue(deps.hasDependency(dependency));
   }

   @Test
   public void testRemoveDependency() throws Exception
   {
      Dependency dependency =
               DependencyBuilder.create("org.jboss:test-dependency2:1.0.1.Final");

      Project project = getProject();
      DependencyFacet deps = project.getFacet(DependencyFacet.class);

      assertFalse(deps.hasDependency(dependency));
      deps.addDependency(dependency);
      assertTrue(deps.hasDependency(dependency));
      deps.removeDependency(dependency);
      assertFalse(deps.hasDependency(dependency));
   }

   @Test
   public void testAddProperty() throws Exception
   {
      String version = "1.0.2.Final";
      Project project = getProject();
      DependencyFacet deps = project.getFacet(DependencyFacet.class);
      deps.setProperty("version", version);
      assertEquals(version, deps.getProperty("version"));
   }

   @Test
   @Ignore
   public void testDoResolveVersions() throws Exception
   {
      Project project = getProject();
      DependencyFacet deps = project.getFacet(DependencyFacet.class);
      List<Dependency> versions = deps.resolveAvailableVersions("com.ocpsoft:prettyfaces-jsf2");
      assertTrue(versions.size() > 4);
   }

   @Test
   public void testHasManagedDependency() throws Exception
   {
      DependencyFacet manDeps = testProject.getFacet(DependencyFacet.class);

      DependencyBuilder javaeeSpec = DependencyBuilder.create("org.jboss.spec:jboss-javaee-6.0:1.0.0.Final:import:pom");
      assertTrue(manDeps.hasManagedDependency(javaeeSpec));
   }

   @Test
   public void testAddManagedDependency() throws Exception
   {
      Dependency dependency =
               DependencyBuilder.create("org.jboss.seam:seam-bom:3.0.0.Final:import:pom");

      Project project = getProject();
      DependencyFacet manDeps = project.getFacet(DependencyFacet.class);
      assertFalse(manDeps.hasManagedDependency(dependency));
      manDeps.addManagedDependency(dependency);
      assertTrue(manDeps.hasManagedDependency(dependency));
   }

   @Test
   public void testRemoveManagedDependency() throws Exception
   {
      Dependency dependency =
               DependencyBuilder.create("org.jboss.seam:seam-bom:3.0.0.Final:import:pom");

      Project project = getProject();
      DependencyFacet manDeps = project.getFacet(DependencyFacet.class);

      assertTrue(manDeps.hasManagedDependency(dependency));
      manDeps.removeManagedDependency(dependency);
      assertFalse(manDeps.hasManagedDependency(dependency));
   }
}
