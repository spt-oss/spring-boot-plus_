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

package org.springframework.boot.autoconfigure.h2;

import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
import org.springframework.util.SocketUtils;
import org.springframework.util.StringUtils;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * H2 server auto configuration
 */
@Configuration
@ConditionalOnClass(Server.class)
@ConditionalOnProperty(prefix = XH2ServerProperties.PREFIX, name = "enabled", havingValue = "true")
@EnableConfigurationProperties(XH2ServerProperties.class)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class XH2ServerAutoConfiguration {
	
	/**
	 * {@link XH2ServerProperties}
	 */
	@NonNull
	private XH2ServerProperties properties;
	
	/**
	 * {@link Server}
	 * 
	 * @return {@link Server}
	 * @throws IllegalStateException if failed to start
	 */
	@Bean(initMethod = "start", destroyMethod = "stop")
	public Server h2Server() throws IllegalStateException {
		
		try {
			
			return Server.createTcpServer(this.buildArguments().toArray(new String[0]));
		}
		catch (SQLException e) {
			
			throw new IllegalStateException("Failed to build server", e);
		}
	}
	
	/**
	 * Build arguments
	 * 
	 * @return arguments
	 */
	protected List<String> buildArguments() {
		
		List<String> args = new ArrayList<>();
		args.add("-tcp");
		args.add("-tcpPort");
		args.add(String.valueOf(this.determinePort()));
		args.add("-baseDir");
		args.add(this.determineBaseDir());
		
		if (this.properties.isAllowOthers()) {
			
			args.add("-tcpAllowOthers");
		}
		
		if (this.properties.isDaemon()) {
			
			args.add("-tcpDaemon");
		}
		
		return args;
	}
	
	/**
	 * Determine port
	 * 
	 * @return port
	 * @throws IllegalStateException if failed to determine
	 */
	protected int determinePort() throws IllegalStateException {
		
		int port = this.properties.getPort();
		
		if (port != 0) {
			
			return port;
		}
		
		return SocketUtils.findAvailableTcpPort();
	}
	
	/**
	 * Determine base directory
	 * 
	 * @return base directory
	 * @throws IllegalStateException if failed to determine
	 */
	protected String determineBaseDir() throws IllegalStateException {
		
		String baseDir = this.properties.getBaseDir();
		
		if (StringUtils.hasText(baseDir)) {
			
			return baseDir;
		}
		
		try {
			
			return Files.createTempDirectory("H2-").toString();
		}
		catch (IOException e) {
			
			throw new IllegalStateException("Failed to determine base directory", e);
		}
	}
	
	/**
	 * {@link AbstractDependsOnBeanFactoryPostProcessor} for {@link DataSource}
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
