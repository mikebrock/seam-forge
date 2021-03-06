/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.forge.maven.resources;

import org.apache.maven.model.Dependency;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.resources.ResourceFlag;
import org.jboss.forge.resources.VirtualResource;

import java.util.Collections;
import java.util.List;

/**
 * MavenDependencyResource
 * 
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class MavenDependencyResource extends VirtualResource<Dependency>
{
   private final Dependency dependency;

   public MavenDependencyResource(Resource<?> parent, Dependency dependency)
   {
      super(null, parent);
      this.dependency = dependency;
      setFlag(ResourceFlag.Leaf);
   }

   /**
    * @return the dependency
    */
   public Dependency getDependency()
   {
      return dependency;
   }

   @Override
   public String getName()
   {
      return dependency.getGroupId() + ":" + dependency.getArtifactId();
   }

   @Override
   public List<Resource<?>> listResources()
   {
      return Collections.emptyList();
   }

   @Override
   public Dependency getUnderlyingResourceObject()
   {
      return dependency;
   }

   @Override
   public String toString()
   {
      return dependency.getGroupId() + ":" +
               dependency.getArtifactId() + ":" +
               dependency.getVersion() + ":" +
               dependency.getType() + ":" +
               dependency.getScope() + ":" +
               dependency.getClassifier();
   }

   @Override
   public boolean delete() throws UnsupportedOperationException
   {
      throw new UnsupportedOperationException("not supported");
   }

   @Override
   public boolean delete(boolean recursive) throws UnsupportedOperationException
   {
      throw new UnsupportedOperationException("not supported");
   }
}
