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

import org.hibernate.boot.model.naming.ImplicitNamingStrategy; // TODO @checkstyle:ignore
import org.hibernate.boot.model.naming.PhysicalNamingStrategy; // TODO @checkstyle:ignore
import org.hibernate.jpa.boot.spi.IntegratorProvider; // TODO @checkstyle:ignore
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.boot.jdbc.SchemaManagementProvider;
import org.springframework.boot.jdbc.metadata.DataSourcePoolMetadataProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.jta.JtaTransactionManager;

/**
 * {@link Configuration} for {@link XHibernateJpaConfigurationBuilder}
 */
@Configuration
public class XHibernateJpaConfigurationBuilderConfiguration {
	
	/**
	 * {@link XHibernateJpaConfigurationBuilder}
	 * 
	 * @param beanFactory {@link BeanFactory}
	 * @param jtaTransactionManager {@link JtaTransactionManager}
	 * @param transactionManagerCustomizers {@link TransactionManagerCustomizers}
	 * @param metadataProviders {@link DataSourcePoolMetadataProvider}
	 * @param schemaManagementProviders {@link SchemaManagementProvider}
	 * @param physicalNamingStrategy {@link PhysicalNamingStrategy}
	 * @param implicitNamingStrategy {@link ImplicitNamingStrategy}
	 * @param integratorProvider {@link IntegratorProvider}
	 * @param hibernatePropertiesCustomizers {@link HibernatePropertiesCustomizer}
	 * @return {@link XHibernateJpaConfigurationBuilder}
	 */
	@Bean
	@ConditionalOnMissingBean
	public XHibernateJpaConfigurationBuilder jpaConfigurationBuilder(
	/* @formatter:off */
		BeanFactory beanFactory,
		ObjectProvider<JtaTransactionManager> jtaTransactionManager,
		ObjectProvider<TransactionManagerCustomizers> transactionManagerCustomizers,
		ObjectProvider<Collection<DataSourcePoolMetadataProvider>> metadataProviders,
		ObjectProvider<List<SchemaManagementProvider>> schemaManagementProviders,
		ObjectProvider<PhysicalNamingStrategy> physicalNamingStrategy,
		ObjectProvider<ImplicitNamingStrategy> implicitNamingStrategy,
		ObjectProvider<IntegratorProvider> integratorProvider,
		ObjectProvider<List<HibernatePropertiesCustomizer>> hibernatePropertiesCustomizers) {
		/* @formatter:on */
		
		return new XHibernateJpaConfigurationBuilder(
		/* @formatter:off */
			beanFactory,
			jtaTransactionManager,
			transactionManagerCustomizers,
			metadataProviders,
			schemaManagementProviders,
			physicalNamingStrategy,
			implicitNamingStrategy,
			integratorProvider,
			hibernatePropertiesCustomizers
			/* @formatter:on */
		);
	}
}
