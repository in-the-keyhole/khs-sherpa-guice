package com.khs.guice;

/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class SherpaGuiceContextListener implements ServletContextListener {
	// I couldn't figure out any other way to get the Managed Bean Factory to the SherpaGuiceModule :(
	private static ServletContext servletContext;

	public static void setServletContext(ServletContext context) {
		servletContext = context;
	}

	public static ServletContext getServletContext() {
		return servletContext;
	}

	public void contextInitialized(ServletContextEvent servletContextEvent) {
		servletContext = servletContextEvent.getServletContext();
	}

	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		servletContext = null;
	}
}
