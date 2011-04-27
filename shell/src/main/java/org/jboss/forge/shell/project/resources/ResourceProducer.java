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

package org.jboss.forge.shell.project.resources;

import org.jboss.forge.resources.Resource;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.plugins.Current;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.InjectionPoint;
import java.lang.reflect.*;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ResourceProducer
{
   @Produces
   @Current
   @SuppressWarnings({ "rawtypes", "unchecked" })
   public Resource getCurrentResource(final InjectionPoint ip, final Shell shell)
   {
      Resource<?> currentResource = shell.getCurrentResource();
      Type type = null;

      Member member = ip.getMember();
      if (member instanceof Field)
      {
         type = ((Field) member).getType();
      }
      else if (member instanceof Method)
      {
         AnnotatedParameter<?> annotated = (AnnotatedParameter<?>) ip.getAnnotated();
         type = annotated.getBaseType();
      }
      else if (member instanceof Constructor<?>)
      {
         AnnotatedParameter<?> annotated = (AnnotatedParameter<?>) ip.getAnnotated();
         type = annotated.getBaseType();
      }

      try
      {
         Class<? extends Resource> resourceClass = currentResource.getClass();
         if ((type instanceof Class) && ((Class) type).isAssignableFrom(resourceClass))
         {
            return currentResource;
         }
         else if (type instanceof ParameterizedType)
         {
            ParameterizedType t = (ParameterizedType) type;
            Type rawType = t.getRawType();
            if ((rawType instanceof Class) && ((Class) rawType).isAssignableFrom(resourceClass))
            {
               return currentResource;
            }
         }
      }
      catch (Exception e)
      {
         throw new IllegalStateException("Could not @Inject Resource type into InjectionPoint:" + ip, e);
      }

      return null;
   }
}
