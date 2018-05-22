/*
 * Copyright 2017-2018 the original author or authors.
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

package org.springframework.boot.autoconfigure.jdbc;

import java.lang.reflect.Method;

import javax.sql.DataSource;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceConfiguration.Hikari;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import lombok.NonNull;

/**
 * Adapter for {@link XMultipleDataSourceConfigurer}
 */
@Configuration // TODO @checkstyle:ignore
@EnableConfigurationProperties(XMultipleDataSourceProperties.class)
public abstract class XMultipleDataSourceConfigurerAdapter implements XMultipleDataSourceConfigurer, BeanFactoryAware {
	
	/**
	 * Bean suffix for {@link DataSource}
	 */
	public static final String DATASOURCE_BEAN_SUFFIX = "DataSource";
	
	/**
	 * Properties prefix for {@link DataSource}
	 */
	public static final String DATASOURCE_PROPERTIES_PREFIX = "spring.datasource.hikari.multiple.";
	
	/**
	 * {@link XMultipleDataSourceProperties}
	 */
	private XMultipleDataSourceProperties dataSourceProperties;
	
	/**
	 * Get {@link XMultipleDataSourceProperties}
	 * 
	 * @return {@link XMultipleDataSourceProperties}
	 */
	protected XMultipleDataSourceProperties getMultipleDataSourceProperties() {
		
		Assert.notNull(this.dataSourceProperties, "Field 'dataSourceProperties' must be set");
		
		return this.dataSourceProperties;
	}
	
	/**
	 * Get single {@link XDataSourceProperties}
	 * 
	 * @param name single name
	 * @return single {@link XDataSourceProperties}
	 */
	protected XDataSourceProperties getSingleDataSourceProperties(String name) {
		
		return this.getMultipleDataSourceProperties().getSingleDataSourceProperties(name);
	}
	
	@Override
	public void setBeanFactory(@NonNull BeanFactory beanFactory) throws BeansException {
		
		this.dataSourceProperties = beanFactory.getBean(XMultipleDataSourceProperties.class);
	}
	
	// @Bean
	@Override
	public DataSource dataSource() {
		
		return this.createDataSource(this.determineSingleName(DATASOURCE_BEAN_SUFFIX));
	}
	
	/**
	 * Create {@link DataSource}
	 * 
	 * @param name single name
	 * @return {@link DataSource}
	 */
	protected DataSource createDataSource(String name) {
		
		return new Hikari().dataSource(this.getSingleDataSourceProperties(name));
	}
	
	/**
	 * Determine single name
	 * 
	 * @param beanSuffix bean suffix
	 * @return single name
	 */
	protected String determineSingleName(@NonNull String beanSuffix) {
		
		Assert.hasLength(beanSuffix, "Invalid '@Bean' suffix");
		
		// Method name from bean suffix
		String methodName = beanSuffix.substring(0, 1).toLowerCase() + beanSuffix.substring(1);
		
		// Method from sub-class
		Method method = ReflectionUtils.findMethod(this.getClass(), methodName);
		
		Assert.notNull(method, "Method '" + methodName + "' is not found");
		
		// Qualifier annotation from method
		Qualifier qualifier = AnnotationUtils.findAnnotation(method, Qualifier.class);
		
		if (qualifier != null) {
			
			String value = qualifier.value();
			
			Assert.isTrue(value.length() > 0, "'@Qualifier#value' must be set");
			
			return value;
		}
		
		// Bean annotation from method
		Bean bean = AnnotationUtils.findAnnotation(method, Bean.class);
		
		Assert.notNull(bean, "'@Bean' must be set");
		Assert.isTrue(bean.name().length > 0, "'@Bean#name' must be set");
		
		// Single name from bean name
		String singleName = bean.name()[0].replaceAll(beanSuffix + "$", "");
		
		Assert.isTrue(!singleName.isEmpty(), "'@Bean#name' must be 'xxx" + beanSuffix + "'");
		
		return singleName;
	}
}
