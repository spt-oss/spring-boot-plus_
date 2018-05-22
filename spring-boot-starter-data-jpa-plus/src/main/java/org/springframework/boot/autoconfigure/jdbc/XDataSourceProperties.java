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

package org.springframework.boot.autoconfigure.jdbc;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DatabaseDriver;
import org.springframework.boot.jdbc.XDatabaseDriver;
import org.springframework.boot.jdbc.XDatabaseUrl;
import org.springframework.util.StringUtils;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Custom {@link DataSourceProperties}
 */
@ConfigurationProperties(XDataSourceProperties.PREFIX)
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class XDataSourceProperties extends DataSourceProperties {
	
	/**
	 * Prefix
	 */
	public static final String PREFIX = "spring.datasource";
	
	/**
	 * Parameter
	 */
	private Map<String, Map<String, Object>> parameter = new LinkedHashMap<>();
	
	@Override
	public String determineDriverClassName() {
		
		if (StringUtils.hasText(this.getUrl())) {
			
			String className = XDatabaseDriver.fromJdbcUrl(this.getUrl()).getDriverClassName();
			
			if (StringUtils.hasText(className)) {
				
				return className;
			}
		}
		
		return super.determineDriverClassName();
	}
	
	@Override
	public String determineUrl() {
		
		String url = super.determineUrl();
		
		return XDatabaseUrl.build(url, this.parameter.get(DatabaseDriver.fromJdbcUrl(url).getId()));
	}
}
