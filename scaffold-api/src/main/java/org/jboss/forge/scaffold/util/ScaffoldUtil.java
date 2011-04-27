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
package org.jboss.forge.scaffold.util;

import org.jboss.forge.resources.FileResource;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.shell.ShellPrompt;

import java.io.InputStream;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class ScaffoldUtil
{
   public static Resource<?> createOrOverwrite(final ShellPrompt prompt, final FileResource<?> resource,
            final InputStream contents,
            final boolean overwrite)
   {
      if (!resource.exists() || overwrite
               || prompt.promptBoolean("[" + resource.getFullyQualifiedName() + "] File exists, overwrite?"))
      {
         resource.createNewFile();
         resource.setContents(contents);
         return resource;
      }
      return null;
   }

   public static Resource<?> createOrOverwrite(final ShellPrompt prompt, final FileResource<?> resource,
            final String contents,
            final boolean overwrite)
   {
      if (!resource.exists() || overwrite
               || prompt.promptBoolean("[" + resource.getFullyQualifiedName() + "] File exists, overwrite?"))
      {
         resource.createNewFile();
         resource.setContents(contents);
         return resource;
      }
      return null;
   }

}
