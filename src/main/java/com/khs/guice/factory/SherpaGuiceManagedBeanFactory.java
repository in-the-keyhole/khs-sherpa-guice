package com.khs.guice.factory;

/*
 * Copyright 2014 the original author or authors.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;

import org.reflections.Reflections;

import com.google.inject.Injector;
import com.khs.sherpa.SherpaSettings;
import com.khs.sherpa.annotation.Endpoint;
import com.khs.sherpa.context.factory.InitManageBeanFactory;
import com.khs.sherpa.context.factory.ManagedBeanFactory;
import com.khs.sherpa.exception.NoSuchManagedBeanExcpetion;
import com.khs.sherpa.parser.BooleanParamParser;
import com.khs.sherpa.parser.CalendarParamParser;
import com.khs.sherpa.parser.DateParamParser;
import com.khs.sherpa.parser.DoubleParamPaser;
import com.khs.sherpa.parser.FloatParamParser;
import com.khs.sherpa.parser.IntegerParamParser;
import com.khs.sherpa.parser.JsonParamParser;
import com.khs.sherpa.parser.StringParamParser;

public class SherpaGuiceManagedBeanFactory implements ManagedBeanFactory, InitManageBeanFactory {

	private Set<ManagedBean> managedBeans = new LinkedHashSet<ManagedBean>();
	private Injector injector;
	
	public Injector getInjector() {
		return injector;
	}
	
	public void setInjector(Injector injector) {
		this.injector = injector;
		for (ManagedBean bean: managedBeans) {
			bean.setInjector(injector);
		}
	}
	
	public List<String> getBeanDefinitionNames() {
		List<String> names = new ArrayList<String>();
		for (ManagedBean bean: managedBeans) {
			names.add(bean.getName());
		}
		
		return names;
	}
	
	public boolean containsManagedBean(Class<?> type) {
		for (ManagedBean bean: managedBeans) {
			if (type.isAssignableFrom(bean.getType())) {
				return true;
			}
		}
		return false;
	}

	public boolean containsManagedBean(String name) {
		for (ManagedBean bean: managedBeans) {
			if (bean.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getManagedBean(Class<T> type) throws NoSuchManagedBeanExcpetion {
		for (ManagedBean bean: managedBeans) {
			if (type.isAssignableFrom(bean.getType())) {
				return (T) bean.getInstance();
			}
		}
		throw new NoSuchManagedBeanExcpetion(type.getName());
	}

	@SuppressWarnings("unchecked")
	public <T> Collection<T> getManagedBeans(Class<T> type) {
		List<T> list = new ArrayList<T>();
		for (ManagedBean bean: managedBeans) {
			if (type.isAssignableFrom(bean.getType())) {
				list.add((T)bean.getInstance());
			}
		}
		return list;
	}

	public Class<?> getManagedBeanType(String name) throws NoSuchManagedBeanExcpetion {
		for (ManagedBean bean: managedBeans) {
			if (bean.getName().equals(name)) {
				return bean.getType();
			}
		}
		throw new NoSuchManagedBeanExcpetion(name);
	}
	
	public Object getManagedBean(String name) throws NoSuchManagedBeanExcpetion {
		for (ManagedBean bean: managedBeans) {
			if (bean.getName().equals(name)) {
				return bean.getInstance();
			}
		}
		throw new NoSuchManagedBeanExcpetion(name);
	}

	@SuppressWarnings("unchecked")
	public <T> T getManagedBean(String name, Class<T> type) throws NoSuchManagedBeanExcpetion {
		return (T) this.getManagedBean(name);
	}

	public boolean isSingleton(String name) throws NoSuchManagedBeanExcpetion {
		for (ManagedBean bean: managedBeans) {
			if (bean.getName().equals(name)) {
				if (bean.isSingleton()) {
					return true;
				}
				
				return false;
			}
		}
		throw new NoSuchManagedBeanExcpetion(name);
	}

	public boolean isTypeMatch(String name, Class<?> type) throws NoSuchManagedBeanExcpetion {
		return this.getType(name).isAssignableFrom(type);
	}

	public Class<?> getType(String name) throws NoSuchManagedBeanExcpetion {
		return this.getManagedBeanType(name);
	}

	public int managedBeansCount() {
		return managedBeans.size();
	}
	
	public void loadManagedBeans(String path) {
		Reflections reflections = new Reflections(path);
		this.loadManagedBeans(reflections.getTypesAnnotatedWith(com.khs.sherpa.annotation.Endpoint.class));
	}
	
	public void loadManagedBeans(Set<Class<?>> types) {
		for (Class<?> type: types) {
			this.loadManagedBean(type);
		}
	}
	
	public void loadManagedBean(Class<?> type) {
		ManagedBean bean = new ManagedSingletonBean(type);
		bean.setInjector(injector);
		managedBeans.add(bean);
	}

	public Map<String, Object> getEndpointTypes() {
		Map<String, Object> map = new HashMap<String, Object>();
		for (ManagedBean bean: managedBeans) {
			if (bean.getType().isAnnotationPresent(Endpoint.class)) {
				map.put(bean.getName(), bean.getInstance());
			}
		}
		return map;
	}

	public void init(SherpaSettings settings, ServletContext context) {
		this.loadManagedBean(settings.userService());
		this.loadManagedBean(settings.tokenService());
		this.loadManagedBean(settings.activityService());
		this.loadManagedBean(settings.jsonProvider());
		
		this.loadManagedBean(StringParamParser.class);
		this.loadManagedBean(IntegerParamParser.class);
		this.loadManagedBean(DoubleParamPaser.class);
		this.loadManagedBean(FloatParamParser.class);
		this.loadManagedBean(BooleanParamParser.class);
		this.loadManagedBean(DateParamParser.class);
		this.loadManagedBean(CalendarParamParser.class);
		this.loadManagedBean(JsonParamParser.class);
		
		// load the root domain
		this.loadManagedBeans("com.khs.sherpa.endpoint");
	}
}
