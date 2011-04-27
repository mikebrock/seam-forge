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
package org.jboss.forge.project.dependencies.events;

import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.Dependency;

import java.util.Arrays;
import java.util.List;

/**
 * Fired when dependencies are removed from the current {@link Project}
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public final class RemovedDependencies
{
   private final List<Dependency> dependencies;
   private final Project project;

   public RemovedDependencies(Project project, Dependency... dependencies)
   {
      this.dependencies = Arrays.asList(dependencies);
      this.project = project;
   }

   public RemovedDependencies(Project project, List<Dependency> dependencies)
   {
      this.dependencies = dependencies;
      this.project = project;
   }

   /**
    * Return a list of all removed {@link Dependency} objects
    */
   public List<Dependency> getDependencies()
   {
      return dependencies;
   }

   /**
    * Get the {@link Project} from which this event was fired.
    */
   public Project getProject()
   {
      return project;
   }
}
