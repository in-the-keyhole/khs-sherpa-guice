package com.khs.guice.factory;

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

class ManagedSingletonBean extends ManagedBean {

	public ManagedSingletonBean(Class<?> type) {
		super(type);
	}

	public Object instance;

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public boolean isPrototype() {
		return false;
	}

	@Override
	public Object getInstance() {
		if(instance == null) {
			instance = this.createInstance();
		}
		return instance;
	}
	
}
