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

package org.jboss.forge.scaffold.plugins;

import org.jboss.forge.scaffold.events.AdvertiseGenProfile;
import org.jboss.forge.scaffold.plugins.shell.ProfileCompleter;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.plugins.*;
import org.jboss.fpak.GenerationContext;
import org.jboss.fpak.model.Definition;
import org.jboss.fpak.strategy.ParseStrategy;
import org.jboss.fpak.strategy.RunStrategy;
import org.jboss.fpak.strategy.builtin.DefaultParseStrategy;
import org.jboss.fpak.strategy.builtin.DefaultRunStrategy;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Mike Brock .
 */
@Alias("gen")
@Singleton
public class GenPlugin implements Plugin
{

   private static final String[] DEFAULT_SEARCH_PATHS = { "org/jboss/forge/scaffold/templates/fpak/",
            "~/.forge/plugins/scaffold/templates/fpak/" };

   private final Shell shell;
   private final Map<String, URL> registeredProfiles;

   @Inject
   public GenPlugin(final Shell shell)
   {
      this.shell = shell;
      this.registeredProfiles = new HashMap<String, URL>();
   }

   public Map<String, URL> getProfiles()
   {
      return registeredProfiles;
   }

   public void registerProfile(@Observes final AdvertiseGenProfile agp)
   {
      shell.printlnVerbose("loaded gen profile: " + agp.getName() + " (" + agp.getUrl() + ")");
      registeredProfiles.put(agp.getName(), agp.getUrl());
   }

   @DefaultCommand
   public void gen(
            final PipeOut out,
            @Option(description = "profile",
                     completer = ProfileCompleter.class,
                     required = true) final String profile,
            @Option(description = "args...") final String... args)
   {

      InputStream profileStream = findProfile(profile);

      if (profileStream == null)
      {
         out.println("no such profile: " + profile);
         return;
      }

      GenerationContext ctx = new GenerationContext();
      ctx.setWorkingDirectory(shell.getCurrentDirectory().getUnderlyingResourceObject());
      ctx.addInputStream(profileStream);
      ctx.setTemplateArgs(args);
      ctx.getGlobals().put("_out", out);

      ParseStrategy parseStrategy = new DefaultParseStrategy();
      Definition def = parseStrategy.doStrategy(ctx);

      RunStrategy runStrategy = new DefaultRunStrategy();
      runStrategy.doStrategy(ctx, def);
   }

   private InputStream findProfile(final String name)
   {
      ClassLoader cl = Thread.currentThread().getContextClassLoader();

      if (registeredProfiles.containsKey(name))
      {
         URL url = registeredProfiles.get(name);
         String path = url.getPath();
         int idx = path.indexOf('!');

         if (idx != -1)
         {
            return cl.getResourceAsStream(path.substring(idx + 2));
         }
         else
         {
            try
            {
               return new FileInputStream(path.substring(path.indexOf(':') + 1));
            }
            catch (FileNotFoundException e)
            {
               return null;
            }
         }
      }

      InputStream inStream;
      for (String path : DEFAULT_SEARCH_PATHS)
      {
         if ((inStream = cl.getResourceAsStream(path + name + ".fpk")) != null)
         {
            return inStream;
         }
      }

      return null;
   }
}
