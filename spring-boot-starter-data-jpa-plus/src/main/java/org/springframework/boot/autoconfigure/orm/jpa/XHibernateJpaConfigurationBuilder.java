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
import java.util.List;

import javax.sql.DataSource;

import org.hibernate.boot.model.naming.ImplicitNamingStrategy; // TODO @checkstyle:ignore
import org.hibernate.boot.model.naming.PhysicalNamingStrategy; // TODO @checkstyle:ignore
import org.hibernate.jpa.boot.spi.IntegratorProvider; // TODO @checkstyle:ignore
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.boot.jdbc.SchemaManagementProvider;
import org.springframework.boot.jdbc.metadata.DataSourcePoolMetadataProvider;
import org.springframework.transaction.jta.JtaTransactionManager;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Builder for {@link XHibernateJpaConfiguration}
 */
@RequiredArgsConstructor
public class XHibernateJpaConfigurationBuilder {
	
	/**
	 * {@link BeanFactory}
	 */
	@NonNull
	private final BeanFactory beanFactory;
	
	/**
	 * {@link JtaTransactionManager}
	 */
	@NonNull
	private final ObjectProvider<JtaTransactionManager> jtaTransactionManager;
	
	/**
	 * {@link TransactionManagerCustomizers}
	 */
	@NonNull
	private final ObjectProvider<TransactionManagerCustomizers> transactionManagerCustomizers;
	
	/**
	 * {@link DataSourcePoolMetadataProvider}
	 */
	@NonNull
	private final ObjectProvider<Collection<DataSourcePoolMetadataProvider>> metadataProviders;
	
	/**
	 * {@link SchemaManagementProvider}
	 */
	@NonNull
	private final ObjectProvider<List<SchemaManagementProvider>> schemaManagementProviders;
	
	/**
	 * {@link PhysicalNamingStrategy}
	 */
	@NonNull
	private final ObjectProvider<PhysicalNamingStrategy> physicalNamingStrategy;
	
	/**
	 * {@link ImplicitNamingStrategy}
	 */
	@NonNull
	private final ObjectProvider<ImplicitNamingStrategy> implicitNamingStrategy;
	
	/**
	 * {@link IntegratorProvider}
	 */
	@NonNull
	private final ObjectProvider<IntegratorProvider> integratorProvider;
	
	/**
	 * {@link HibernatePropertiesCustomizer}
	 */
	@NonNull
	private final ObjectProvider<List<HibernatePropertiesCustomizer>> hibernatePropertiesCustomizers;
	
	/**
	 * Build
	 * 
	 * @param dataSource {@link DataSource}
	 * @param jpaProperties {@link XJpaProperties}
	 * @return {@link XHibernateJpaConfiguration}
	 */
	public XHibernateJpaConfiguration build(DataSource dataSource, XJpaProperties jpaProperties) {
		
		XHibernateJpaConfiguration config = new XHibernateJpaConfiguration(
		/* @formatter:off */
			dataSource,
			jpaProperties,
			this.jtaTransactionManager,
			this.transactionManagerCustomizers,
			this.metadataProviders,
			this.schemaManagementProviders,
			this.physicalNamingStrategy,
			this.implicitNamingStrategy,
			this.integratorProvider,
			this.hibernatePropertiesCustomizers
			/* @formatter:on */
		);
		
		config.setBeanFactory(this.beanFactory);
		
		return config;
	}
}
