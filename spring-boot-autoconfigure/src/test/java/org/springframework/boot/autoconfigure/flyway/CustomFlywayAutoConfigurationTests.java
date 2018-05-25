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

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.flywaydb.core.CustomFlyway;
import org.flywaydb.core.internal.placeholder.MysqlH2SqlReplacer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.flyway.CustomFlywayAutoConfigurationTests.InternalMigrationConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * {@link Test}: {@link CustomFlywayAutoConfiguration}
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
	/* @formatter:off */
	DataSourceAutoConfiguration.class,
	CustomFlywayAutoConfiguration.class,
	InternalMigrationConfiguration.class
	/* @formatter:on */
})
@ActiveProfiles({ "test", "test-flyway" })
public class CustomFlywayAutoConfigurationTests {
	
	/**
	 * Schema: foo
	 */
	private static final String SCHEMA_FOO = "foo";
	
	/**
	 * Schema: bar
	 */
	private static final String SCHEMA_BAR = "bar";
	
	/**
	 * {@link CustomFlyway}
	 */
	@Autowired
	private CustomFlyway flyway;
	
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
		
		ConditionalOnProperty property = AnnotationUtils.findAnnotation(CustomFlywayAutoConfiguration.class,
			ConditionalOnProperty.class);
		
		assertThat(property).isNotNull();
		assertThat(property.prefix()).isEqualTo(CustomFlywayProperties.PREFIX);
		assertThat(property.name()).isEqualTo(new String[] { "enabled" });
		assertThat(property.havingValue()).isEqualTo("");
		assertThat(property.matchIfMissing()).isEqualTo(true);
	}
	
	/**
	 * {@link org.springframework.boot.autoconfigure.flyway.CustomFlywayAutoConfiguration.CustomFlywayConfiguration#flyway()}
	 */
	@Test
	public void flyway() {
		
		assertThat(this.hasData(SCHEMA_FOO)).isEqualTo(false);
		assertThat(this.hasData(SCHEMA_BAR)).isEqualTo(false);
		
		this.flyway.migrate();
		
		assertThat(this.hasData(SCHEMA_FOO)).isEqualTo(true);
		assertThat(this.hasData(SCHEMA_BAR)).isEqualTo(true);
	}
	
	/**
	 * Has data?
	 * 
	 * @param name name
	 * @return {@code true} if has data
	 */
	private boolean hasData(String name) {
		
		try (Connection connection = this.dataSource.getConnection()) {
			
			try (ResultSet result = connection.prepareCall("select * from " + name + "." + name).executeQuery()) {
				
				return result.next() && result.getString("code").equals(name);
			}
		}
		catch (SQLException e) {
			
			// No table
			if (e.getMessage().contains("not found")) {
				
				return false;
			}
			
			throw new IllegalStateException(e);
		}
	}
	
	/**
	 * {@link Configuration}: Internal migration
	 */
	@Configuration
	protected static class InternalMigrationConfiguration {
		
		/**
		 * {@link Bean}: {@link CustomFlywayMigrationStrategy}
		 * 
		 * @return {@link CustomFlywayMigrationStrategy}
		 */
		@Bean
		public CustomFlywayMigrationStrategy migrationStrategy() {
			
			return flyway -> flyway.setPlaceholderReplacer(new MysqlH2SqlReplacer());
		}
	}
}
