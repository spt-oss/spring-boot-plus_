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
package org.springframework.boot.autoconfigure.flyway;

import java.util.List;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.XFlyway;
import org.flywaydb.core.api.callback.FlywayCallback;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.jdbc.XDataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.XHibernateJpaAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ResourceLoader;

/**
 * Custom {@link FlywayAutoConfiguration}
 */
@Configuration
@ConditionalOnProperty(prefix = XFlywayProperties.PREFIX, name = "enabled", havingValue = "true")
@EnableConfigurationProperties(XFlywayProperties.class)
@AutoConfigureBefore(FlywayAutoConfiguration.class)
@AutoConfigureAfter({ XDataSourceAutoConfiguration.class, XHibernateJpaAutoConfiguration.class })
public class XFlywayAutoConfiguration extends FlywayAutoConfiguration {
	
	/**
	 * {@link XFlywayProperties}
	 * 
	 * @return {@link XFlywayProperties}
	 */
	@Bean
	@Primary
	public XFlywayProperties flywayProperties() {
		
		return new XFlywayProperties();
	}
	
	/**
	 * Custom {@link org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration.FlywayConfiguration}
	 */
	@Configuration
	@ConditionalOnMissingBean(Flyway.class)
	@EnableConfigurationProperties(FlywayProperties.class)
	public static class XFlywayConfiguration extends FlywayConfiguration {
		
		/**
		 * Constructor
		 * 
		 * @param properties {@link XFlywayProperties}
		 * @param dataSourceProperties {@link DataSourceProperties}
		 * @param resourceLoader {@link ResourceLoader}
		 * @param dataSource {@link DataSource}
		 * @param flywayDataSource {@link DataSource}
		 * @param migrationStrategy {@link FlywayMigrationStrategy}
		 * @param flywayCallbacks {@link FlywayCallback}
		 */
		public XFlywayConfiguration(
		/* @formatter:off */
			XFlywayProperties properties,
			DataSourceProperties dataSourceProperties,
			ResourceLoader resourceLoader,
			ObjectProvider<DataSource> dataSource,
			ObjectProvider<DataSource> flywayDataSource,
			ObjectProvider<FlywayMigrationStrategy> migrationStrategy,
			ObjectProvider<List<FlywayCallback>> flywayCallbacks) {
			/* @formatter:on */
			
			super(
			/* @formatter:off */
				properties,
				dataSourceProperties,
				resourceLoader,
				dataSource,
				flywayDataSource,
				migrationStrategy,
				flywayCallbacks
				/* @formatter:on */
			);
		}
		
		@Bean
		@ConfigurationProperties(prefix = XFlywayProperties.PREFIX)
		@Override
		public Flyway flyway() {
			
			org.flywaydb.core.api.configuration.FlywayConfiguration config = super.flyway();
			
			XFlyway flyway = new XFlyway();
			flyway.setDataSource(config.getDataSource());
			flyway.setCallbacks(config.getCallbacks());
			flyway.setLocations(config.getLocations());
			
			return flyway;
		}
	}
}
