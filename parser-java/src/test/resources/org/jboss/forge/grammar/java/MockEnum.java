package org.jboss.forge.grammar.java;

@SuppressWarnings
public enum MockEnum
{
   FOO, BAR, BAZ;
   
   private MockEnum()
   {
   }
   
   String getName()
   {
      return name();
   }
}
