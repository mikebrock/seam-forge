/*
 * 
 */

package org.jboss.forge.dev;

import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.facets.BaseFacet;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.PackagingFacet;
import org.jboss.forge.project.packaging.PackagingType;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.shell.plugins.RequiresPackagingType;
import org.jboss.forge.spec.javaee.CDIFacet;

import javax.inject.Inject;
import java.util.List;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Alias("forge.api")
@RequiresFacet({ DependencyFacet.class, PackagingFacet.class, CDIFacet.class })
@RequiresPackagingType(PackagingType.JAR)
public class ForgeAPIFacet extends BaseFacet
{

   @Inject
   private Shell shell;

   @Override
   public boolean install()
   {
      DependencyFacet deps = project.getFacet(DependencyFacet.class);

      List<Dependency> versions = deps.resolveAvailableVersions("org.jboss.forge:forge-shell-api:[,]");
      Dependency version = shell.promptChoiceTyped("Install which version of the Forge API?", versions);
      deps.setProperty("forge.api.version", version.getVersion());
      DependencyBuilder dep = DependencyBuilder.create("org.jboss.forge:forge-shell-api:${forge.api.version}");
      deps.addDependency(dep);
      return true;
   }

   @Override
   public boolean isInstalled()
   {
      Dependency dep = DependencyBuilder.create("org.jboss.forge:forge-shell-api");
      PackagingType packagingType = project.getFacet(PackagingFacet.class).getPackagingType();
      return project.getFacet(DependencyFacet.class).hasDependency(dep)
               && PackagingType.JAR.equals(packagingType);
   }
}
