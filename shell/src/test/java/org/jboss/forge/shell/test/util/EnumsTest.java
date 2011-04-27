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
package org.jboss.forge.shell.test.util;

import org.jboss.forge.shell.test.completer.MockEnum;
import org.jboss.forge.shell.util.Enums;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class EnumsTest
{

   /**
    * Test method for {@link org.jboss.forge.shell.util.Enums#valueOf(java.lang.Class, java.lang.Object)}.
    */
   @Test
   public void testValueOf()
   {
      Enum<?> e = Enums.valueOf(MockEnum.class, "FOO");
      assertEquals(MockEnum.FOO, e);
   }

   @Test
   public void testHasValue() throws Exception
   {
      assertTrue(Enums.hasValue(MockEnum.class, "FOO"));
      assertFalse(Enums.hasValue(MockEnum.class, "WOO"));
   }

   @Test
   public void testGetValues() throws Exception
   {
      List<MockEnum> list = Enums.getValues(MockEnum.class);
      assertTrue(list.contains(MockEnum.BAR));
      assertTrue(list.contains(MockEnum.BAZ));
      assertTrue(list.contains(MockEnum.CAT));
      assertTrue(list.contains(MockEnum.DOG));
      assertTrue(list.contains(MockEnum.FOO));
      assertEquals(5, list.size());
   }

}
