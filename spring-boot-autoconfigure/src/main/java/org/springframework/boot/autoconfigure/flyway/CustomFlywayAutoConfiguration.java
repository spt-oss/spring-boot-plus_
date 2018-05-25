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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.flywaydb.core.CustomFlyway;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
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
@ConditionalOnClass(CustomFlyway.class)
@ConditionalOnBean(DataSource.class)
@ConditionalOnProperty(prefix = CustomFlywayProperties.PREFIX, name = "enabled", matchIfMissing = true)
@AutoConfigureBefore(FlywayAutoConfiguration.class)
@AutoConfigureAfter({
	/* @formatter:off */
	DataSourceAutoConfiguration.class,
	JdbcTemplateAutoConfiguration.class,
	HibernateJpaAutoConfiguration.class
	/* @formatter:on */
})
public class CustomFlywayAutoConfiguration extends FlywayAutoConfiguration {
	
	/**
	 * {@link CustomFlywayProperties}
	 * 
	 * @return {@link CustomFlywayProperties}
	 */
	@Bean
	@Primary
	public CustomFlywayProperties flywayProperties() {
		
		return new CustomFlywayProperties();
	}
	
	/**
	 * Custom {@link org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration.FlywayConfiguration}
	 */
	@Configuration
	@ConditionalOnMissingBean(Flyway.class)
	@EnableConfigurationProperties(FlywayProperties.class)
	public static class CustomFlywayConfiguration extends FlywayConfiguration {
		
		/**
		 * Constructor
		 * 
		 * @param properties {@link CustomFlywayProperties}
		 * @param dataSourceProperties {@link DataSourceProperties}
		 * @param resourceLoader {@link ResourceLoader}
		 * @param dataSource {@link DataSource}
		 * @param flywayDataSource {@link DataSource}
		 * @param migrationStrategy {@link FlywayMigrationStrategy}
		 * @param flywayCallbacks TODO remove
		 */
		public CustomFlywayConfiguration(
		/* @formatter:off */
			CustomFlywayProperties properties,
			DataSourceProperties dataSourceProperties,
			ResourceLoader resourceLoader,
			ObjectProvider<DataSource> dataSource,
			ObjectProvider<DataSource> flywayDataSource,
			ObjectProvider<FlywayMigrationStrategy> migrationStrategy,
			@SuppressWarnings("deprecation") ObjectProvider<List<org.flywaydb.core.api.callback.FlywayCallback>> flywayCallbacks) {
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
		@ConfigurationProperties(prefix = CustomFlywayProperties.PREFIX)
		@SuppressWarnings("deprecation")
		@Override
		public Flyway flyway() {
			
			org.flywaydb.core.api.configuration.Configuration config = super.flyway();
			
			String[] locations = Arrays.asList(config.getLocations())
			/* @formatter:off */
				.stream()
				.map(location -> location.getDescriptor())
				.collect(Collectors.toList())
				.toArray(new String[0]);
				/* @formatter:on */
			
			CustomFlyway flyway = new CustomFlyway();
			flyway.setDataSource(config.getDataSource());
			flyway.setCallbacks(config.getCallbacks());
			flyway.setLocations(locations);
			
			return flyway;
		}
	}
}
