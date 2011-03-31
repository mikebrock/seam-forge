/*
 * JBoss, by Red Hat.
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

package org.jboss.seam.forge.shell;

import org.jboss.seam.forge.project.dependencies.Dependency;
import org.jboss.seam.forge.project.dependencies.DependencyBuilder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PluginJar
{

   private static final String DELIM = "$";
   private final Dependency dep;
   private int version = 0;

   public PluginJar(String name) throws IllegalNameException
   {
      // group.Id_artifactId_4.jar
      // -------------------------0-12-------3-----4---5-------6---7
      Matcher m = Pattern.compile("^((.+?)\\$(.+?))(\\$(\\d+))+(\\$(.*?)).jar$").matcher(name);

      if (!m.matches())
      {
         throw new IllegalNameException("Invalid plugin file-name format detected: " + name);
      }

      DependencyBuilder builder = DependencyBuilder.create();
      builder.setGroupId(m.group(2));
      builder.setArtifactId(m.group(3));
      builder.setVersion(m.group(7));
      dep = builder;

      if (m.group(5) != null)
      {
         this.version = Integer.valueOf(m.group(5));
      }
   }

   public PluginJar(Dependency dep)
   {
      this.dep = dep;
   }

   public PluginJar(Dependency dep, int version)
   {
      this(dep);
      this.version = version;
   }

   /**
    * GroupId$ArtifactId$LoadedVersion$PluginVersion.jar
    */
   public String getFullName()
   {
      String result = getName();
      result += DELIM + version;
      result += DELIM + (dep.getVersion() == null ? "" : dep.getVersion());
      return result + ".jar";
   }

   /**
    * GroupId$ArtifactId
    * 
    * @return
    */
   public String getName()
   {
      return dep.getGroupId() + DELIM + dep.getArtifactId();
   }

   public Dependency getDependency()
   {
      return dep;
   }

   public int getVersion()
   {
      return version;
   }

   @Override
   public String toString()
   {
      return getFullName();
   }

   public class IllegalNameException extends RuntimeException
   {
      private static final long serialVersionUID = 3021789284719060665L;
      private String message;

      public IllegalNameException()
      {
         super.fillInStackTrace();
      }

      public IllegalNameException(String message)
      {
         this();
         this.message = message;
      }

      public IllegalNameException(String message, Throwable e)
      {
         this(message);
         super.initCause(e);
      }

      @Override
      public String getMessage()
      {
         return message;
      }
   }

   public boolean isSamePlugin(PluginJar jar)
   {
      return getName().equals(jar.getName());
   }
}
