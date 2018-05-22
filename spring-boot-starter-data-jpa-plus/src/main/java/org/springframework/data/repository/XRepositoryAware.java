/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.repository;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.util.Assert;

import lombok.NonNull;

/**
 * Aware for {@link Repository}
 * 
 * @param <R> repository type
 */
public abstract class XRepositoryAware<R extends Repository<?, ?>> // TODO @checkstyle:ignore
	implements BeanFactoryAware {
	
	/**
	 * {@link Repository}
	 */
	private R repository;
	
	/**
	 * {@link Repository}
	 * 
	 * @return {@link Repository}
	 */
	protected R repository() {
		
		Assert.notNull(this.repository, String.format("Repository not found: %s", this.getClass()));
		
		return this.repository;
	}
	
	@Override
	public void setBeanFactory(@NonNull BeanFactory beanFactory) throws BeansException {
		
		this.repository = beanFactory.getBean(getTypeParameterClass(this, XRepositoryAware.class, 0));
	}
	
	/**
	 * Get type parameter class TODO move to utilities
	 * 
	 * @param object object
	 * @param genericClass generic class
	 * @param index index
	 * @param <T> parameter type
	 * @return parameter class
	 * @throws IllegalStateException if failed to get
	 */
	protected static <T> Class<T> getTypeParameterClass(Object object, Class<?> genericClass, int index)
		throws IllegalStateException {
		
		Class<?> targetClass = AopUtils.getTargetClass(object);
		
		Set<Type> genericTypes = new HashSet<>();
		genericTypes.add(targetClass.getGenericSuperclass());
		genericTypes.addAll(Arrays.asList(targetClass.getGenericInterfaces()));
		
		ParameterizedType paramedType = null;
		
		for (Type genericType : genericTypes) {
			
			if (!ParameterizedType.class.isInstance(genericType)) {
				
				continue;
			}
			
			ParameterizedType castType = (ParameterizedType) genericType;
			
			if (castType.getRawType().equals(genericClass)) {
				
				paramedType = castType;
				
				break;
			}
		}
		
		if (paramedType == null) {
			
			throw new IllegalStateException(String.format("Parameter type is not set: %s", genericClass));
		}
		
		Type[] paramTypes = paramedType.getActualTypeArguments();
		
		if (paramTypes.length <= index) {
			
			throw new IllegalStateException(String.format("Invalid parameter index: %s, %d", genericClass, index));
		}
		
		Type paramType = paramTypes[index];
		
		if (!Class.class.isInstance(paramType)) {
			
			throw new IllegalStateException(String.format("Parameter type is not actual: %s", genericClass));
		}
		
		@SuppressWarnings("unchecked")
		Class<T> paramClass = (Class<T>) paramType;
		
		return paramClass;
	}
}
