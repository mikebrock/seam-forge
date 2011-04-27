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

package org.jboss.forge.shell.command;

import org.jboss.forge.resources.Resource;
import org.jboss.forge.shell.plugins.*;
import org.jboss.forge.shell.util.Annotations;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessBean;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class CommandLibraryExtension implements Extension
{
   private final Map<String, List<PluginMetadata>> plugins = new HashMap<String, List<PluginMetadata>>();

   public Map<String, List<PluginMetadata>> getPlugins()
   {
      return plugins;
   }

   @SuppressWarnings("unchecked")
   public void scan(@Observes final ProcessBean<?> event)
   {
      Bean<?> bean = event.getBean();

      Class<?> clazz = bean.getBeanClass();

      if (Plugin.class.isAssignableFrom(clazz))
      {
         PluginMetadata pluginMeta = getMetadataFor((Class<? extends Plugin>) clazz);

         if (!plugins.containsKey(pluginMeta.getName()))
         {
            plugins.put(pluginMeta.getName(), new ArrayList<PluginMetadata>());
         }

         plugins.get(pluginMeta.getName()).add(pluginMeta);
      }
   }

   public PluginMetadata getMetadataFor(final Class<? extends Plugin> plugin)
   {
      String name = getPluginName(plugin);

      PluginMetadata pluginMeta = new PluginMetadata();
      pluginMeta.setName(name);
      pluginMeta.setType(plugin);

      if (Annotations.isAnnotationPresent(plugin, Help.class))
      {
         pluginMeta.setHelp(Annotations.getAnnotation(plugin, Help.class).value());
      }
      else
      {
         pluginMeta.setHelp("");
      }

      if (Annotations.isAnnotationPresent(plugin, RequiresResource.class))
      {
         List<Class<? extends Resource<?>>> resourceTypes = Arrays.asList(Annotations.getAnnotation(plugin,
                  RequiresResource.class).value());

         pluginMeta.setResourceScopes(resourceTypes);
      }

      if (Annotations.isAnnotationPresent(plugin, Topic.class))
      {
         pluginMeta.setTopic(Annotations.getAnnotation(plugin, Topic.class).value());
      }

      processPluginCommands(pluginMeta, plugin);

      return pluginMeta;
   }

   @SuppressWarnings("rawtypes")
   private List<CommandMetadata> processPluginCommands(final PluginMetadata pluginMeta, final Class<?> plugin)
   {
      List<CommandMetadata> results = new ArrayList<CommandMetadata>();

      for (Method method : plugin.getMethods())
      {
         if (Annotations.isAnnotationPresent(method, Command.class))
         {
            Command command = Annotations.getAnnotation(method, Command.class);
            CommandMetadata commandMeta = new CommandMetadata();
            commandMeta.setMethod(method);
            commandMeta.setHelp(command.help());
            commandMeta.setParent(pluginMeta);

            // Default commands are invoked via the name of the plug-in, not by
            // plug-in + command
            if ("".equals(command.value()))
            {
               commandMeta.setName(method.getName().trim().toLowerCase());
            }
            else
            {
               commandMeta.setName(command.value());
            }

            // This works because @DefaultCommand is annotated by @Command
            if (Annotations.isAnnotationPresent(method, DefaultCommand.class))
            {
               if (pluginMeta.hasDefaultCommand())
               {
                  throw new IllegalStateException("Plugins may only have one @"
                           + DefaultCommand.class.getSimpleName()
                           + ", but [" + pluginMeta.getType() + "] has more than one.");
               }

               commandMeta.setDefault(true);
               commandMeta.setName(pluginMeta.getName());

               // favor help text from this annotation over others
               DefaultCommand def = Annotations.getAnnotation(method, DefaultCommand.class);
               if ((def.help() != null) && !def.help().trim().isEmpty())
               {
                  commandMeta.setHelp(def.help());
               }
            }

            if (Annotations.isAnnotationPresent(method, RequiresResource.class))
            {
               List<Class<? extends Resource>> resourceTypes = new ArrayList<Class<? extends Resource>>(
                        pluginMeta.getResourceScopes());

               resourceTypes.addAll(Arrays.asList(Annotations.getAnnotation(method, RequiresResource.class).value()));

               commandMeta.setResourceScopes(resourceTypes);
            }

            // fall back to the pluginMetadata for help text
            if ((commandMeta.getHelp() == null) || commandMeta.getHelp().trim().isEmpty())
            {
               commandMeta.setHelp(pluginMeta.getHelp());
            }

            Class<?>[] parameterTypes = method.getParameterTypes();
            Annotation[][] parameterAnnotations = method.getParameterAnnotations();

            int i = 0;
            for (Class<?> clazz : parameterTypes)
            {
               OptionMetadata optionMeta = new OptionMetadata();

               optionMeta.setType(clazz);
               optionMeta.setIndex(i);

               if (PipeOut.class.isAssignableFrom(clazz))
               {
                  optionMeta.setPipeOut(true);
               }

               for (Annotation annotation : parameterAnnotations[i])
               {
                  if (annotation instanceof Option)
                  {
                     Option option = (Option) annotation;
                     optionMeta.setParent(commandMeta);
                     optionMeta.setName(option.name());
                     optionMeta.setShortName(option.shortName());
                     optionMeta.setFlagOnly(option.flagOnly());
                     optionMeta.setDescription(option.description());
                     optionMeta.setDefaultValue(option.defaultValue());
                     optionMeta.setHelp(option.help());
                     optionMeta.setRequired(option.required());
                     optionMeta.setPromptType(option.type());
                     optionMeta.setCompleterType(option.completer());

                  }
                  else if (annotation instanceof PipeIn)
                  {
                     optionMeta.setPipeIn(true);
                  }

               }
               commandMeta.addOption(optionMeta);

               i++;
            }

            results.add(commandMeta);
         }
      }

      pluginMeta.addCommands(results);

      return results;
   }

   private String getPluginName(final Class<?> plugin)
   {
      String name = null;

      if (Annotations.isAnnotationPresent(plugin, Alias.class))
      {
         Alias named = Annotations.getAnnotation(plugin, Alias.class);
         if (named != null)
         {
            name = named.value();
         }
      }

      if ((name == null) || "".equals(name.trim()))
      {
         name = plugin.getSimpleName();
      }
      return name.toLowerCase();
   }
}
