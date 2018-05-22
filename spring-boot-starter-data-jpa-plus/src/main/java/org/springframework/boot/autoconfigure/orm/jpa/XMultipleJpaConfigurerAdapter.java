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

package org.springframework.boot.autoconfigure.orm.jpa;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.sql.DataSource;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.XMultipleDataSourceConfigurerAdapter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Adapter for {@link XMultipleJpaConfigurer}
 */
@Configuration // TODO @checkstyle:ignore
@EnableConfigurationProperties(XMultipleJpaProperties.class)
@Import(XHibernateJpaConfigurationBuilderConfiguration.class)
public abstract class XMultipleJpaConfigurerAdapter extends XMultipleDataSourceConfigurerAdapter
	implements XMultipleJpaConfigurer {
	
	/**
	 * Bean suffix for {@link PlatformTransactionManager}
	 */
	public static final String TRANSACTION_MANAGER_BEAN_SUFFIX = "TransactionManager";
	
	/**
	 * Bean suffix for {@link LocalContainerEntityManagerFactoryBean}
	 */
	public static final String ENTITY_MANAGER_FACTORY_BEAN_SUFFIX = "EntityManagerFactory";
	
	/**
	 * {@link XHibernateJpaConfigurationBuilder}
	 */
	private XHibernateJpaConfigurationBuilder configBuilder;
	
	/**
	 * Get {@link XHibernateJpaConfigurationBuilder}
	 * 
	 * @return {@link XHibernateJpaConfigurationBuilder}
	 */
	protected XHibernateJpaConfigurationBuilder getConfigBuilder() {
		
		Assert.notNull(this.configBuilder, "Field 'configBuilder' must be set");
		
		return this.configBuilder;
	}
	
	/**
	 * {@link XMultipleJpaProperties}
	 */
	private XMultipleJpaProperties jpaProperties;
	
	/**
	 * Get {@link XMultipleJpaProperties}
	 * 
	 * @return {@link XMultipleJpaProperties}
	 */
	protected XMultipleJpaProperties getMultipleJpaProperties() {
		
		Assert.notNull(this.jpaProperties, "Field 'jpaProperties' must be set");
		
		return this.jpaProperties;
	}
	
	/**
	 * Get single {@link XJpaProperties}
	 * 
	 * @param name single name
	 * @return single {@link XJpaProperties}
	 */
	protected XJpaProperties getSingleJpaProperties(String name) {
		
		return this.getMultipleJpaProperties().getSingleJpaProperties(name);
	}
	
	@Override
	public void setBeanFactory(@NonNull BeanFactory beanFactory) throws BeansException {
		
		super.setBeanFactory(beanFactory);
		
		this.configBuilder = beanFactory.getBean(XHibernateJpaConfigurationBuilder.class);
		this.jpaProperties = beanFactory.getBean(XMultipleJpaProperties.class);
	}
	
	// @Bean
	@Override
	public DataSource dataSource() {
		
		return super.dataSource();
	}
	
	// @Bean
	@Override
	public PlatformTransactionManager transactionManager() {
		
		return this.createTransactionManager(this.determineSingleName(TRANSACTION_MANAGER_BEAN_SUFFIX));
	}
	
	// @Bean
	@Override
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		
		return this.createEntityManagerFactory(this.determineSingleName(TRANSACTION_MANAGER_BEAN_SUFFIX));
	}
	
	/**
	 * Create {@link PlatformTransactionManager}
	 * 
	 * @param name single name
	 * @return {@link PlatformTransactionManager}
	 */
	protected PlatformTransactionManager createTransactionManager(String name) {
		
		return this.createConfig(name).transactionManager();
	}
	
	/**
	 * Create {@link LocalContainerEntityManagerFactoryBean}
	 * 
	 * @param name single name
	 * @return {@link LocalContainerEntityManagerFactoryBean}
	 */
	protected LocalContainerEntityManagerFactoryBean createEntityManagerFactory(String name) {
		
		XHibernateJpaConfiguration config = this.createConfig(name);
		
		EntityManagerFactoryBuilder factoryBuilder = config.entityManagerFactoryBuilder(
		/* @formatter:off */
			config.jpaVendorAdapter(),
			new XSimpleObjectProvider<>(this.persistenceUnitManager())
			/* @formatter:on */
		);
		
		LocalContainerEntityManagerFactoryBean factory = config.entityManagerFactory(factoryBuilder);
		
		// Reset packages to scan
		Set<String> packagesToScan = new LinkedHashSet<>();
		packagesToScan.addAll(this.basePackages());
		
		if (this.basePackageClass() != null) {
			
			packagesToScan.add(ClassUtils.getPackageName(this.basePackageClass()));
		}
		
		for (Class<?> basePackageClass : this.basePackageClasses()) {
			
			packagesToScan.add(ClassUtils.getPackageName(basePackageClass));
		}
		
		if (!CollectionUtils.isEmpty(packagesToScan)) {
			
			factory.setPackagesToScan(packagesToScan.toArray(new String[0]));
		}
		
		return factory;
	}
	
	/**
	 * Create {@link XHibernateJpaConfiguration}
	 * 
	 * @param name single name
	 * @return {@link XHibernateJpaConfiguration}
	 */
	protected XHibernateJpaConfiguration createConfig(String name) {
		
		return this.configBuilder.build(this.dataSource(), this.getSingleJpaProperties(name));
	}
	
	/**
	 * {@link PersistenceUnitManager}
	 * 
	 * @return {@link PersistenceUnitManager}
	 */
	protected PersistenceUnitManager persistenceUnitManager() {
		
		return null;
	}
	
	/**
	 * Base packages
	 * 
	 * @return packages to scan
	 * @see EntityScan#basePackages()
	 * @see org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder.Builder#packages(String...)
	 */
	protected Collection<String> basePackages() {
		
		return Collections.emptyList();
	}
	
	/**
	 * Base package class
	 * 
	 * @return base package class
	 * @see EntityScan#basePackageClasses()
	 * @see org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder.Builder#packages(Class...)
	 */
	protected Class<?> basePackageClass() {
		
		return null;
	}
	
	/**
	 * Base package classes
	 * 
	 * @return base package classes
	 * @see EntityScan#basePackageClasses()
	 * @see org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder.Builder#packages(Class...)
	 */
	protected Collection<Class<?>> basePackageClasses() {
		
		return Collections.emptyList();
	}
	
	/**
	 * Simple {@link ObjectProvider}
	 * 
	 * @param <T> bean type
	 */
	@RequiredArgsConstructor
	protected static class XSimpleObjectProvider<T> implements ObjectProvider<T> {
		
		/**
		 * Bean
		 */
		private final T bean;
		
		@Override
		public T getObject() {
			
			throw new UnsupportedOperationException();
		}
		
		@Override
		public T getObject(Object... args) {
			
			throw new UnsupportedOperationException();
		}
		
		@Override
		public T getIfAvailable() {
			
			return this.bean;
		}
		
		@Override
		public T getIfUnique() {
			
			throw new UnsupportedOperationException();
		}
	}
}
