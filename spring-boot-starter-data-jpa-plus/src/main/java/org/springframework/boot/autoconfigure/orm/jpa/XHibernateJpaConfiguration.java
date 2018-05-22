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
package org.springframework.boot.autoconfigure.orm.jpa;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.hibernate.boot.model.naming.ImplicitNamingStrategy; // TODO @checkstyle:ignore
import org.hibernate.boot.model.naming.PhysicalNamingStrategy; // TODO @checkstyle:ignore
import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl; // TODO @checkstyle:ignore
import org.hibernate.jpa.boot.spi.IntegratorProvider; // TODO @checkstyle:ignore
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.boot.jdbc.SchemaManagementProvider;
import org.springframework.boot.jdbc.metadata.DataSourcePoolMetadataProvider;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.util.StringUtils;

import lombok.NonNull;

/**
 * Custom {@link HibernateJpaConfiguration}
 */
@Configuration
public class XHibernateJpaConfiguration extends HibernateJpaConfiguration {
	
	/**
	 * {@link XJpaProperties}
	 */
	private XJpaProperties jpaProperties;
	
	/**
	 * {@link IntegratorProvider}
	 */
	private IntegratorProvider integratorProvider;
	
	/**
	 * Constructor
	 * 
	 * @param dataSource {@link DataSource}
	 * @param jpaProperties {@link XJpaProperties}
	 * @param jtaTransactionManager {@link JtaTransactionManager}
	 * @param transactionManagerCustomizers {@link TransactionManagerCustomizers}
	 * @param metadataProviders {@link DataSourcePoolMetadataProvider}
	 * @param schemaManagementProviders {@link SchemaManagementProvider}
	 * @param physicalNamingStrategy {@link PhysicalNamingStrategy}
	 * @param implicitNamingStrategy {@link ImplicitNamingStrategy}
	 * @param integratorProvider {@link IntegratorProvider}
	 * @param hibernatePropertiesCustomizers {@link HibernatePropertiesCustomizer}
	 */
	public XHibernateJpaConfiguration(
	/* @formatter:off */
		DataSource dataSource,
		@NonNull XJpaProperties jpaProperties,
		ObjectProvider<JtaTransactionManager> jtaTransactionManager,
		ObjectProvider<TransactionManagerCustomizers> transactionManagerCustomizers,
		ObjectProvider<Collection<DataSourcePoolMetadataProvider>> metadataProviders,
		ObjectProvider<List<SchemaManagementProvider>> schemaManagementProviders,
		ObjectProvider<PhysicalNamingStrategy> physicalNamingStrategy,
		ObjectProvider<ImplicitNamingStrategy> implicitNamingStrategy,
		@NonNull ObjectProvider<IntegratorProvider> integratorProvider,
		ObjectProvider<List<HibernatePropertiesCustomizer>> hibernatePropertiesCustomizers) {
		/* @formatter:on */
		
		super(
		/* @formatter:off */
			dataSource,
			jpaProperties,
			jtaTransactionManager,
			transactionManagerCustomizers,
			metadataProviders,
			schemaManagementProviders,
			physicalNamingStrategy,
			implicitNamingStrategy,
			hibernatePropertiesCustomizers
			/* @formatter:on */
		);
		
		this.jpaProperties = jpaProperties;
		this.integratorProvider = integratorProvider.getIfAvailable();
	}
	
	@Bean
	@Override
	public PlatformTransactionManager transactionManager() {
		
		JpaTransactionManager transactionManager = (JpaTransactionManager) super.transactionManager();
		
		String persistenceUnitName = this.jpaProperties.getPersistenceUnitName();
		
		if (StringUtils.hasText(persistenceUnitName)) {
			
			transactionManager.setPersistenceUnitName(persistenceUnitName);
		}
		
		return transactionManager;
	}
	
	@Bean
	@Override
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder) {
		
		LocalContainerEntityManagerFactoryBean factory = super.entityManagerFactory(builder);
		
		String persistenceUnitName = this.jpaProperties.getPersistenceUnitName();
		
		if (StringUtils.hasText(persistenceUnitName)) {
			
			factory.setPersistenceUnitName(persistenceUnitName);
		}
		
		return factory;
	}
	
	@Override
	protected void customizeVendorProperties(@NonNull Map<String, Object> vendorProperties) {
		
		super.customizeVendorProperties(vendorProperties);
		
		if (this.integratorProvider != null) {
			
			vendorProperties.put(EntityManagerFactoryBuilderImpl.INTEGRATOR_PROVIDER, this.integratorProvider);
		}
	}
}
