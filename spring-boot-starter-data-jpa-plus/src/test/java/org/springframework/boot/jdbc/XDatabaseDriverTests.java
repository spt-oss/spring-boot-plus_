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
package org.springframework.boot.jdbc;

import static org.assertj.core.api.Assertions.assertThat;

import javax.sql.DataSource;

import org.junit.Test;

import com.zaxxer.hikari.HikariDataSource;

/**
 * {@link XDatabaseDriver} tests
 */
public class XDatabaseDriverTests {
	
	/**
	 * {@link XDatabaseDriver#UNKNOWN}
	 */
	private static final XDatabaseDriver UNKNOWN = XDatabaseDriver.UNKNOWN;
	
	/**
	 * {@link XDatabaseDriver#MYSQL_REPLICATION}
	 */
	private static final XDatabaseDriver MYSQL_REPLICATION = XDatabaseDriver.MYSQL_REPLICATION;
	
	/**
	 * {@link XDatabaseDriver#fromJdbcUrl(String)}
	 */
	@Test
	public void detectDriverClassName() {
		
		assertThat(XDatabaseDriver.fromJdbcUrl("jdbc:mysql")).isEqualTo(UNKNOWN);
		assertThat(XDatabaseDriver.fromJdbcUrl("jdbc:mysql:")).isEqualTo(UNKNOWN);
		assertThat(XDatabaseDriver.fromJdbcUrl("jdbc:mysql://host")).isEqualTo(UNKNOWN);
		assertThat(XDatabaseDriver.fromJdbcUrl("jdbc:mysql:loadbalance")).isEqualTo(UNKNOWN);
		assertThat(XDatabaseDriver.fromJdbcUrl("jdbc:mysql:loadbalance://host")).isEqualTo(UNKNOWN);
		assertThat(XDatabaseDriver.fromJdbcUrl("jdbc:mysql:replication")).isEqualTo(UNKNOWN);
		assertThat(XDatabaseDriver.fromJdbcUrl("jdbc:mysql:replication:")).isEqualTo(MYSQL_REPLICATION);
		assertThat(XDatabaseDriver.fromJdbcUrl("jdbc:mysql:replication://host")).isEqualTo(MYSQL_REPLICATION);
		
		{
			String url = "jdbc:mysql:replication://master,slave/schema";
			
			DataSource dataSource = DataSourceBuilder.create()
			/* @formatter:off */
				.driverClassName(XDatabaseDriver.fromJdbcUrl(url).getDriverClassName())
				.url(url)
				/* @formatter:on */
				.build();
			
			assertThat(((HikariDataSource) dataSource).getDriverClassName())
				.isEqualTo(MYSQL_REPLICATION.getDriverClassName());
		}
	}
}
