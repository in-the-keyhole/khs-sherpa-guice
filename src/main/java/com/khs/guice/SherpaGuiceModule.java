package com.khs.guice;

import com.google.inject.AbstractModule;
import com.khs.guice.factory.SherpaGuiceManagedBeanFactory;
import com.khs.sherpa.context.ApplicationContext;
import com.khs.sherpa.context.GenericApplicationContext;
import com.khs.sherpa.exception.NoSuchManagedBeanExcpetion;

public class SherpaGuiceModule extends AbstractModule {

	@Override
	protected void configure() {
		// There must be a better way to get to the servlet context...
		ApplicationContext applicationContext = (ApplicationContext)SherpaGuiceContextListener.getServletContext().getAttribute(GenericApplicationContext.SHERPA_APPLICATION_CONTEXT_ATTRIBUTE);
		SherpaGuiceManagedBeanFactory factory = (SherpaGuiceManagedBeanFactory)applicationContext.getManagedBeanFactory();
		for (String name : factory.getBeanDefinitionNames()) {
			try {
				Class<?> type = factory.getType(name);
				binder().bind(type);
			} catch (NoSuchManagedBeanExcpetion e) {
				// this really shouldn't happen as we're iterating over the beans that are already in the factory
				e.printStackTrace();
			}
		}
	}
}
