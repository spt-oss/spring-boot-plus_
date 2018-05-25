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

import org.flywaydb.core.XFlyway;
import org.flywaydb.core.internal.util.XMysqlToH2SqlReplacer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.flyway.XFlywayAutoConfigurationTests.XOptionalConfiguration;
import org.springframework.boot.autoconfigure.jdbc.XDataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Tests for {@link XFlywayAutoConfiguration}
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
	/* @formatter:off */
	XFlywayAutoConfiguration.class,
	XDataSourceAutoConfiguration.class,
	XOptionalConfiguration.class
	/* @formatter:on */
})
@ActiveProfiles({ "test", "test-flyway-config" })
public class XFlywayAutoConfigurationTests {
	
	/**
	 * Schema: foo
	 */
	private static final String SCHEMA_FOO = "foo";
	
	/**
	 * Schema: bar
	 */
	private static final String SCHEMA_BAR = "bar";
	
	/**
	 * {@link XFlyway}
	 */
	@Autowired
	private XFlyway flyway;
	
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
		
		ConditionalOnProperty property = AnnotationUtils.findAnnotation(XFlywayAutoConfiguration.class,
			ConditionalOnProperty.class);
		
		assertThat(property.prefix()).isEqualTo(XFlywayProperties.PREFIX);
		assertThat(property.name()).isEqualTo(new String[] { "enabled" });
		assertThat(property.havingValue()).isEqualTo("true");
		assertThat(property.matchIfMissing()).isEqualTo(false);
	}
	
	/**
	 * {@link org.springframework.boot.autoconfigure.flyway.XFlywayAutoConfiguration.XFlywayConfiguration#flyway()}
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
				
				if (!result.next()) {
					
					return false;
				}
				
				return result.getString("code").equals(name);
			}
		}
		catch (SQLException e) {
			
			// No table
			if (e.getMessage().contains("not found")) {
				
				return false;
			}
			
			throw new IllegalStateException("Failed to execute SQL", e);
		}
	}
	
	/**
	 * {@link Configuration} for optional
	 */
	@Configuration
	protected static class XOptionalConfiguration {
		
		/**
		 * {@link XFlywayMigrationStrategy}
		 * 
		 * @return {@link XFlywayMigrationStrategy}
		 */
		@Bean
		public XFlywayMigrationStrategy migrationStrategy() {
			
			return flyway -> flyway.setPlaceholderReplacer(new XMysqlToH2SqlReplacer());
		}
	}
}
