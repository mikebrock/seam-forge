package org.jboss.forge.shell.command.convert;

import org.mvel2.ConversionHandler;

import java.net.MalformedURLException;
import java.net.URL;

public class URLConverter implements ConversionHandler
{

   @SuppressWarnings("rawtypes")
   @Override
   public boolean canConvertFrom(Class type)
   {
      return String.class.isAssignableFrom(type);
   }

   @Override
   public Object convertFrom(Object value)
   {
      try
      {
         return new URL((String) value);
      }
      catch (MalformedURLException e)
      {
         throw new IllegalArgumentException("Could not convert [" + value + "] to type java.net.URL");
      }
   }

}
