package org.jboss.seam.forge.spec.javaee6.jpa.api;

import org.jboss.seam.forge.shell.util.BeanManagerUtils;
import org.jboss.seam.forge.spec.javaee6.jpa.provider.EclipseLinkProvider;
import org.jboss.seam.forge.spec.javaee6.jpa.provider.HibernateProvider;
import org.jboss.seam.forge.spec.javaee6.jpa.provider.OpenJPAProvider;

import javax.enterprise.inject.spi.BeanManager;

public enum JPAProvider
{
   HIBERNATE(HibernateProvider.class),
   OPENJPA(OpenJPAProvider.class),
   ECLIPSELINK(EclipseLinkProvider.class);

   private Class<? extends PersistenceProvider> type;

   private JPAProvider(Class<? extends PersistenceProvider> type)
   {
      this.type = type;
   }

   public PersistenceProvider getProvider(BeanManager manager)
   {
      return BeanManagerUtils.getContextualInstance(manager, type);
   }
}
