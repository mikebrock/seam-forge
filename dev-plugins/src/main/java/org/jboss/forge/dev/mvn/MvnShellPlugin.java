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

package org.jboss.forge.dev.mvn;

import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.plugins.*;
import org.jboss.forge.shell.util.NativeSystemCall;

import javax.inject.Inject;
import java.io.IOException;

/**
 * @author Mike Brock .
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Alias("mvn")
@Topic("Project")
@RequiresProject
@RequiresFacet(MavenCoreFacet.class)
public class MvnShellPlugin implements Plugin
{
   private final Shell shell;

   @Inject
   public MvnShellPlugin(final Shell shell)
   {
      this.shell = shell;
   }

   @DefaultCommand
   public void run(final PipeOut out, final String... parms) throws IOException
   {
      if (shell.getCurrentProject() != null)
      {
         NativeSystemCall.execFromPath("mvn", parms, out, shell.getCurrentProject().getProjectRoot());
      }
      else
      {
         NativeSystemCall.execFromPath("mvn", parms, out, shell.getCurrentDirectory());
      }
   }
}
