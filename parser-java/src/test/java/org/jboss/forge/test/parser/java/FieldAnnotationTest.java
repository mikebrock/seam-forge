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
package org.jboss.forge.test.parser.java;

import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.Annotation;
import org.jboss.forge.parser.java.Field;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.test.parser.java.common.AnnotationTest;
import org.jboss.forge.test.parser.java.common.MockEnumType;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class FieldAnnotationTest extends AnnotationTest<JavaClass, Field<JavaClass>>
{
   @Override
   public void resetTests()
   {
      InputStream stream = FieldAnnotationTest.class
               .getResourceAsStream("/org/jboss/forge/grammar/java/MockAnnotatedField.java");
      Field<JavaClass> field = JavaParser.parse(JavaClass.class, stream).getFields().get(0);
      setTarget(field);
   }

   @Test
   public void testParseEnumValueStaticImport() throws Exception
   {
      List<Annotation<JavaClass>> annotations = getTarget().getAnnotations();
      Annotation<JavaClass> annotation = annotations.get(annotations.size() - 1);
      MockEnumType enumValue = annotation.getEnumValue(MockEnumType.class);
      assertEquals(MockEnumType.FOO, enumValue);
   }
}
