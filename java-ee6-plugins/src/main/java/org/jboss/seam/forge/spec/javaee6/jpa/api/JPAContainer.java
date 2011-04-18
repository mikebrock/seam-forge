package org.jboss.seam.forge.spec.javaee6.jpa.api;

import org.jboss.seam.forge.shell.util.BeanManagerUtils;
import org.jboss.seam.forge.spec.javaee6.jpa.container.*;

import javax.enterprise.inject.spi.BeanManager;

public enum JPAContainer
{
   JBOSS_AS6(JBossAS6Container.class),
   JBOSS_AS7(JBossAS6Container.class),
   GLASSFISH_3(GlassFish3Container.class),
   CUSTOM_JDBC(CustomJDBCContainer.class),
   CUSTOM_JTA(CustomJTAContainer.class),
   CUSTOM_NON_JTA(NonJTAContainer.class);

   private Class<? extends PersistenceContainer> containerType;

   private JPAContainer(final Class<? extends PersistenceContainer> containerType)
   {
      this.containerType = containerType;
   }

   public PersistenceContainer getContainer(final BeanManager manager)
   {
      return BeanManagerUtils.getContextualInstance(manager, containerType);
   }
}
