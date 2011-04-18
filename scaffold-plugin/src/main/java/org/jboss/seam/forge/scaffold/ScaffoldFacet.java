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
package org.jboss.seam.forge.scaffold;

import org.jboss.seam.forge.project.facets.BaseFacet;
import org.jboss.seam.forge.project.packaging.PackagingType;
import org.jboss.seam.forge.shell.plugins.Alias;
import org.jboss.seam.forge.shell.plugins.RequiresFacet;
import org.jboss.seam.forge.shell.plugins.RequiresPackagingType;
import org.jboss.seam.forge.spec.javaee6.cdi.CDIFacet;
import org.jboss.seam.forge.spec.javaee6.jpa.PersistenceFacet;
import org.jboss.seam.forge.spec.javaee6.jsf.FacesFacet;
import org.jboss.seam.forge.spec.javaee6.servlet.ServletFacet;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Alias("forge.scaffold")
@RequiresFacet({ ServletFacet.class, CDIFacet.class, FacesFacet.class, PersistenceFacet.class })
@RequiresPackagingType(PackagingType.WAR)
public class ScaffoldFacet extends BaseFacet
{
   @Override
   public boolean isInstalled()
   {
      return project.hasFacet(ServletFacet.class) && project.hasFacet(FacesFacet.class)
               && project.hasFacet(CDIFacet.class);
   }

   @Override
   public boolean install()
   {
      project.registerFacet(this);
      return true;
   }

}
