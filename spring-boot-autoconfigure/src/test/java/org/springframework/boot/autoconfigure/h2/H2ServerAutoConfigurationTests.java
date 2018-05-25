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

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.h2.server.Service;
import org.h2.tools.Server;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.zaxxer.hikari.HikariDataSource;

/**
 * {@link Test}: {@link H2ServerAutoConfiguration}
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { H2ServerAutoConfiguration.class, DataSourceAutoConfiguration.class })
@ActiveProfiles({ "test", "test-h2" })
public class H2ServerAutoConfigurationTests {
	
	/**
	 * {@link H2ServerProperties}
	 */
	@Autowired
	private H2ServerProperties h2ServerProperties;
	
	/**
	 * {@link Server}
	 */
	@Autowired
	private Server h2Server;
	
	/**
	 * {@link DataSourceProperties}
	 */
	@Autowired
	private DataSourceProperties dataSourceProperties;
	
	/**
	 * {@link DataSource}
	 */
	@Autowired
	private DataSource dataSource;
	
	/**
	 * Annotation
	 */
	@Test
	public void annotation() {
		
		ConditionalOnProperty property = AnnotationUtils.findAnnotation(H2ServerAutoConfiguration.class,
			ConditionalOnProperty.class);
		
		assertThat(property.prefix()).isEqualTo(H2ServerProperties.PREFIX);
		assertThat(property.name()).isEqualTo(new String[] { "enabled" });
		assertThat(property.havingValue()).isEqualTo("true");
		assertThat(property.matchIfMissing()).isEqualTo(false);
	}
	
	/**
	 * {@link H2ServerProperties}
	 */
	@Test
	public void h2ServerProperties() {
		
		{
			H2ServerProperties properties = new H2ServerProperties();
			
			assertThat(properties.getPort()).isEqualTo(0);
			assertThat(properties.getBaseDir()).isNull();
			assertThat(properties.isAllowOthers()).isEqualTo(false);
			assertThat(properties.isDaemon()).isEqualTo(true);
		}
		
		{
			assertThat(this.h2ServerProperties.getPort()).isEqualTo(56789);
			assertThat(this.h2ServerProperties.getBaseDir()).isEqualTo("./target/work/data");
			assertThat(this.h2ServerProperties.isAllowOthers()).isEqualTo(true);
			assertThat(this.h2ServerProperties.isDaemon()).isEqualTo(false);
		}
	}
	
	/**
	 * {@link Server}
	 */
	@Test
	public void h2Server() {
		
		assertThat(this.h2Server.isRunning(false)).isEqualTo(true);
		
		Service service = this.h2Server.getService();
		
		assertThat(service.getPort()).isEqualTo(this.h2ServerProperties.getPort());
		assertThat(service.getAllowOthers()).isEqualTo(this.h2ServerProperties.isAllowOthers());
		assertThat(service.isDaemon()).isEqualTo(this.h2ServerProperties.isDaemon());
	}
	
	/**
	 * {@link DataSource}
	 */
	@Test
	public void dataSource() {
		
		HikariDataSource dataSource = (HikariDataSource) this.dataSource;
		
		assertThat(dataSource.getJdbcUrl()).isEqualTo(this.dataSourceProperties.getUrl());
		
		try (Connection connection = this.dataSource.getConnection()) {
			
			connection.prepareStatement(dataSource.getConnectionTestQuery()).execute();
		}
		catch (SQLException e) {
			
			throw new IllegalStateException(e);
		}
	}
}
