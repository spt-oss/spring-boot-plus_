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

import org.springframework.util.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Custom {@link DatabaseDriver}
 */
@AllArgsConstructor
public enum XDatabaseDriver {
	
	/**
	 * Unknown
	 */
	UNKNOWN(null, null),
	
	/**
	 * MySQL replication
	 */
	MYSQL_REPLICATION("jdbc:mysql:replication", "com.mysql.jdbc.ReplicationDriver");
	
	/**
	 * JDBC URL prefix
	 */
	private String jdbcUrlPrefix;
	
	/**
	 * Driver class name
	 */
	@Getter
	private String driverClassName;
	
	/**
	 * {@link DatabaseDriver#fromJdbcUrl(String)}
	 * 
	 * @param url JDBC URL
	 * @return driver class name or {@code null}
	 */
	public static XDatabaseDriver fromJdbcUrl(String url) {
		
		if (StringUtils.hasText(url)) {
			
			for (XDatabaseDriver driver : values()) {
				
				if (url.startsWith(driver.jdbcUrlPrefix + ":")) {
					
					return driver;
				}
			}
		}
		
		return UNKNOWN;
	}
}
