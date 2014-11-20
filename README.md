khs-sherpa-guice [![Build Status](https://travis-ci.org/in-the-keyhole/khs-sherpa-guice.svg?branch=master)](https://travis-ci.org/in-the-keyhole/khs-sherpa-guice)
================

Guice injection integration for sherpa

To get this working, follow these steps:

Add 

	<listener>
	    <listener-class>com.khs.guice.SherpaGuiceContextListener</listener-class>
	</listener>

to your web.xml after Sherpa's listener but before your application's listener.

In your application's context listener, add SherpaGuiceModule to the end of the list of modules to be loaded by the injector. Then, use something like this to let the SherpaGuice's factory know about the injector. Also shown is how to have Guice handle your servlet mapping without having to put it in web.xml.

After these "glue" pieces are in place, you can just use @Inject in your Sherpa endpoints to inject services or whatever else you need.

```java
public class MyApplicationContextListener extends GuiceServletContextListener implements ServletContextListener {
	private List<Module> modulesLoaded = new LinkedList<Module>();
	ServletContext servletContext;

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		servletContext = sce.getServletContext();
		super.contextInitialized(sce);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		super.contextDestroyed(sce);
		for (Module module : modulesLoaded) {
			if (module instanceof DestroyableModule) {
				((DestroyableModule) module).destroy();
			}
		}

		servletContext = null;
	}

	@Override
	protected Injector getInjector() {
		// add your application modules here
	
		modulesLoaded.add(new ServletModule() {
			@Override
			protected void configureServlets() {
				serve("/api/*").with(SherpaGuiceServlet.class);
			}
		});
		modulesLoaded.add(new SherpaGuiceModule());

		ApplicationContext applicationContext = (ApplicationContext)servletContext.getAttribute(GenericApplicationContext.SHERPA_APPLICATION_CONTEXT_ATTRIBUTE);
		SherpaGuiceManagedBeanFactory factory = (SherpaGuiceManagedBeanFactory) applicationContext.getManagedBeanFactory();
		Injector injector = Guice.createInjector(modulesLoaded);
		factory.setInjector(injector);
		return injector;
	}
}
```
