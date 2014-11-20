khs-sherpa-guice [![Build Status](https://travis-ci.org/in-the-keyhole/khs-sherpa-guice.svg?branch=master)](https://travis-ci.org/in-the-keyhole/khs-sherpa-guice)
================

Guice injection integration for sherpa

To get this working, follow these steps:

Add 

	<listener>
	    <listener-class>com.khs.guice.SherpaGuiceContextListener</listener-class>
	</listener>

to your web.xml after Sherpa's listener but before your application's listener.

In your application's context listener, add SherpaGuiceModule to the end of the list of modules to be loaded by the injector. Then, use something like this to let the SherpaGuice's factory know about the injector.

```java
ApplicationContext applicationContext = (ApplicationContext)servletContext.getAttribute(GenericApplicationContext.SHERPA_APPLICATION_CONTEXT_ATTRIBUTE);
SherpaGuiceManagedBeanFactory factory = (SherpaGuiceManagedBeanFactory) applicationContext.getManagedBeanFactory();
Injector injector = Guice.createInjector(modulesLoaded);
factory.setInjector(injector);
```
