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
package org.jboss.forge.shell.events;

import java.io.File;

/**
 * Fired as a signal to the shell to bootstrap and accept user input. Should be fired only once per application runtime
 * unless followed by a subsequent {@link Shutdown} event.
 * <p/>
 * <strong>For example:</strong>
 * <p/>
 * <code>@Inject Event&lt;Startup&gt startup; <br/>...<br/>
 * startup.fire(new Startup());
 * </code>
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public final class Startup
{
   private File workingDirectory = new File("").getAbsoluteFile();
   private boolean restart;

   public Startup()
   {
   }

   public Startup(File workingDirectory)
   {
      this.workingDirectory = workingDirectory;
   }

   public Startup(File workingDirectory, boolean restart)
   {
      this.workingDirectory = workingDirectory;
      this.restart = restart;
   }

   public File getWorkingDirectory()
   {
      return workingDirectory;
   }

   public boolean isRestart()
   {
      return restart;
   }
}
