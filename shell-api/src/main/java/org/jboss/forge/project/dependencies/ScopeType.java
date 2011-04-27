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

package org.jboss.forge.project.dependencies;

/**
 * Represents the various dependency scopes.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public enum ScopeType
{
   COMPILE("compile"),
   PROVIDED("provided"),
   RUNTIME("runtime"),
   TEST("test"),
   SYSTEM("system"),
   IMPORT("import"),
   OTHER("");

   private String scope;

   private ScopeType(final String scope)
   {
      this.scope = scope;
   }

   public String getScope()
   {
      return scope;
   }

   public static ScopeType from(final String type)
   {
      ScopeType result = null;

      if ((type != null) && !type.trim().isEmpty())
      {
         result = OTHER;
         for (ScopeType scopeType : ScopeType.values())
         {
            if (scopeType.getScope().equalsIgnoreCase(type.trim()))
            {
               result = scopeType;
            }
         }
      }
      return result;
   }
}
