package org.jboss.forge.resources;

import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.services.ResourceFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DependencyResource extends FileResource<DependencyResource>
{
   private final Dependency dep;

   public DependencyResource(ResourceFactory resourceFactory, File file, Dependency dep)
   {
      super(resourceFactory, file);
      this.dep = dep;
   }

   @Override
   public List<Resource<?>> listResources()
   {
      return new ArrayList<Resource<?>>();
   }

   @Override
   public Resource<File> createFrom(File file)
   {
      throw new ResourceException("Not implemented.");
   }

   public Dependency getDependency()
   {
      return dep;
   }

   @Override
   public String toString()
   {
      return dep.toCoordinates();
   }
}
