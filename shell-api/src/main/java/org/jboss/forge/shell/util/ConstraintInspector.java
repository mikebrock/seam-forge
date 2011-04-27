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
package org.jboss.forge.shell.util;

import org.jboss.forge.project.Facet;
import org.jboss.forge.project.packaging.PackagingType;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.shell.plugins.RequiresPackagingType;
import org.jboss.forge.shell.plugins.RequiresProject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Used to inspect types that may or may not depend on {@link Facet}s or {@link PackagingType}s
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class ConstraintInspector
{
   /**
    * Return the name of the given bean type.
    */
   public static String getName(final Class<?> type)
   {
      String result = type.getSimpleName();

      if (Annotations.isAnnotationPresent(type, Alias.class))
      {
         Alias annotation = Annotations.getAnnotation(type, Alias.class);
         if ((annotation.value() != null) && !annotation.value().trim().isEmpty())
         {
            result = annotation.value();
         }
      }

      return result;
   }

   /**
    * Inspect the given {@link Class} for any dependencies to {@link Facet} types.
    */
   public static List<Class<? extends Facet>> getFacetDependencies(final Class<?> type)
   {
      List<Class<? extends Facet>> result = new ArrayList<Class<? extends Facet>>();

      if (Annotations.isAnnotationPresent(type, RequiresFacet.class))
      {
         RequiresFacet requires = Annotations.getAnnotation(type, RequiresFacet.class);
         if (requires.value() != null)
         {
            result.addAll(Arrays.asList(requires.value()));
         }
      }

      return result;
   }

   /**
    * Inspect the given {@link Class} for any dependencies to {@link PackagingType} types.
    */
   public static List<PackagingType> getCompatiblePackagingTypes(final Class<?> type)
   {
      List<PackagingType> result = new ArrayList<PackagingType>();

      if (Annotations.isAnnotationPresent(type, RequiresPackagingType.class))
      {
         RequiresPackagingType requires = Annotations.getAnnotation(type, RequiresPackagingType.class);
         if (requires.value() != null)
         {
            result.addAll(Arrays.asList(requires.value()));
         }
      }

      return result;
   }

   /**
    * Inspect the given {@link Class} type for a dependency on an active project.
    */
   public static boolean requiresProject(final Class<?> type)
   {
      return Annotations.isAnnotationPresent(type, RequiresProject.class);
   }
}
