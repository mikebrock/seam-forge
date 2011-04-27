package org.jboss.forge.shell.plugins.builtin;

import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.completer.ShellEnvCompleter;
import org.jboss.forge.shell.plugins.*;

import javax.inject.Inject;
import java.util.Map;

/**
 * @author Mike Brock .
 */
@Alias("set")
@Topic("Shell Environment")
@Help("Sets and lists environment variables")
public class SetPlugin implements Plugin
{
   private final Shell shell;

   @Inject
   public SetPlugin(final Shell shell)
   {
      this.shell = shell;
   }

   @DefaultCommand
   public void run(@Option(description = "varname",
                        completer = ShellEnvCompleter.class) final String variable,
                   @Option(description = "value") final String... value)
   {

      if (variable == null)
      {
         listVars();
      }
      else
      {
         shell.setProperty(variable, Echo.tokensToString(value));
      }
   }

   private void listVars()
   {
      for (Map.Entry<String, Object> entry : shell.getProperties().entrySet())
      {
         shell.println(entry.getKey() + "=" + entry.getValue());
      }
   }

}
