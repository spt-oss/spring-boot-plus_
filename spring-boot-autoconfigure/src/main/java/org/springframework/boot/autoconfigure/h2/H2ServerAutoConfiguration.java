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

package org.springframework.boot.autoconfigure.h2;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.h2.tools.Server;
import org.springframework.boot.autoconfigure.AbstractDependsOnBeanFactoryPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseFactoryBean;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * H2 server auto configuration
 */
@Configuration
@ConditionalOnClass(Server.class)
@ConditionalOnProperty(prefix = H2ServerProperties.PREFIX, name = "enabled", havingValue = "true")
@EnableConfigurationProperties(H2ServerProperties.class)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class H2ServerAutoConfiguration {
	
	/**
	 * {@link H2ServerProperties}
	 */
	@NonNull
	private H2ServerProperties properties;
	
	/**
	 * {@link Bean}: {@link Server}
	 * 
	 * @return {@link Server}
	 * @throws IllegalStateException if failed to start
	 */
	@Bean(initMethod = "start", destroyMethod = "stop")
	public Server h2Server() throws IllegalStateException {
		
		try {
			
			return Server.createTcpServer(this.properties.createArguments());
		}
		catch (SQLException e) {
			
			throw new IllegalStateException("Failed to build server", e);
		}
	}
	
	/**
	 * {@link AbstractDependsOnBeanFactoryPostProcessor}: {@link DataSource}
	 */
	@Configuration
	@ConditionalOnClass({ DataSource.class, EmbeddedDatabaseFactoryBean.class })
	public static class DataSourceDependsOnBeanFactoryPostProcessor extends AbstractDependsOnBeanFactoryPostProcessor {
		
		/**
		 * Constructor
		 */
		public DataSourceDependsOnBeanFactoryPostProcessor() {
			
			super(DataSource.class, EmbeddedDatabaseFactoryBean.class, "h2Server");
		}
	}
}
