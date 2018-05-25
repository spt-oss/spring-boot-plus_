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

package org.springframework.boot.autoconfigure.jdbc;

import static org.assertj.core.api.Assertions.assertThat;

import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.MultipleDataSourceConfigurerAdapterTests.BarDataSourceConfiguration;
import org.springframework.boot.autoconfigure.jdbc.MultipleDataSourceConfigurerAdapterTests.BazDataSourceConfiguration;
import org.springframework.boot.autoconfigure.jdbc.MultipleDataSourceConfigurerAdapterTests.FooDataSourceConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.zaxxer.hikari.HikariDataSource;

/**
 * {@link Test}: {@link MultipleDataSourceConfigurerAdapter}
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
	/* @formatter:off */
	FooDataSourceConfiguration.class,
	BarDataSourceConfiguration.class,
	BazDataSourceConfiguration.class
	/* @formatter:on */
})
@ActiveProfiles({ "test", "test-multiple-datasource-config" })
public class MultipleDataSourceConfigurerAdapterTests {
	
	/**
	 * Foo
	 */
	private static final String FOO = "foo";
	
	/**
	 * Bar
	 */
	private static final String BAR = "bar";
	
	/**
	 * Baz
	 */
	private static final String BAZ = "baz";
	
	/**
	 * {@link DataSource} for {@link #FOO}
	 */
	@Autowired
	@Qualifier(FOO)
	private DataSource fooDataSource;
	
	/**
	 * {@link DataSource} for {@link #BAR}
	 */
	@Autowired
	private DataSource barDataSource;
	
	/**
	 * {@link DataSource} for {@link #BAZ}
	 */
	@Autowired
	private DataSource bazDataSource;
	
	/**
	 * {@link MultipleDataSourceConfigurerAdapter#dataSource()}
	 */
	@Test
	public void dataSource() {
		
		{
			HikariDataSource dataSource = (HikariDataSource) this.fooDataSource;
			
			assertThat(dataSource.getMaximumPoolSize()).isEqualTo(30000);
			assertThat(dataSource.getMinimumIdle()).isEqualTo(-1);
			assertThat(dataSource.getJdbcUrl()).isEqualTo("jdbc:h2:tcp://localhost/hikari-foo");
			assertThat(dataSource.getPoolName()).isEqualTo("hikari-foo");
		}
		
		{
			HikariDataSource dataSource = (HikariDataSource) this.barDataSource;
			
			assertThat(dataSource.getMaximumPoolSize()).isEqualTo(-1);
			assertThat(dataSource.getMinimumIdle()).isEqualTo(-1);
			assertThat(dataSource.getJdbcUrl()).isEqualTo("jdbc:mysql://localhost/bar");
			assertThat(dataSource.getPoolName()).isEqualTo(BAR);
		}
		
		{
			HikariDataSource dataSource = (HikariDataSource) this.bazDataSource;
			
			assertThat(dataSource.getMaximumPoolSize()).isEqualTo(40000);
			assertThat(dataSource.getMinimumIdle()).isEqualTo(10000);
			assertThat(dataSource.getJdbcUrl()).isEqualTo("jdbc:mysql://localhost/baz");
			assertThat(dataSource.getPoolName()).isEqualTo("datasource-baz");
		}
	}
	
	/**
	 * {@link MultipleDataSourceConfigurerAdapter} for {@link MultipleDataSourceConfigurerAdapterTests#FOO}
	 */
	@Configuration
	protected static class FooDataSourceConfiguration extends MultipleDataSourceConfigurerAdapter {
		
		@Bean
		@Qualifier(FOO)
		@ConfigurationProperties(DATASOURCE_PROPERTIES_PREFIX + FOO)
		@Override
		public DataSource dataSource() {
			
			return super.dataSource();
		}
	}
	
	/**
	 * {@link MultipleDataSourceConfigurerAdapter} for {@link MultipleDataSourceConfigurerAdapterTests#BAR}
	 */
	@Configuration
	protected static class BarDataSourceConfiguration extends MultipleDataSourceConfigurerAdapter {
		
		@Bean(BAR + DATASOURCE_BEAN_SUFFIX)
		@ConfigurationProperties(DATASOURCE_PROPERTIES_PREFIX + BAR)
		@Override
		public DataSource dataSource() {
			
			return super.dataSource();
		}
	}
	
	/**
	 * {@link MultipleDataSourceConfigurerAdapter} for {@link MultipleDataSourceConfigurerAdapterTests#BAZ}
	 */
	@Configuration
	protected static class BazDataSourceConfiguration extends MultipleDataSourceConfigurerAdapter {
		
		@Bean(BAZ + DATASOURCE_BEAN_SUFFIX)
		@ConfigurationProperties(DATASOURCE_PROPERTIES_PREFIX + BAZ)
		@Override
		public DataSource dataSource() {
			
			return super.dataSource();
		}
	}
}
