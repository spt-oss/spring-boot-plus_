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

package org.springframework.boot.autoconfigure.orm.jpa;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.boot.autoconfigure.jdbc.XMultipleDataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * {@link XJpaProperties} for multiple
 */
@ConfigurationProperties(XJpaProperties.PREFIX)
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class XMultipleJpaProperties extends XJpaProperties {
	
	/**
	 * Multiple
	 */
	private Map<String, XJpaProperties> multiple = new HashMap<>();
	
	/**
	 * Get single {@link XJpaProperties}
	 * 
	 * <p>
	 * Note: This method rewrite multiple properties
	 * </p>
	 * 
	 * @param name single name
	 * @return single {@link XJpaProperties}
	 */
	public XJpaProperties getSingleJpaProperties(String name) {
		
		XJpaProperties properties = this.multiple.getOrDefault(name, new XJpaProperties());
		
		String pattern = Pattern.quote(XMultipleDataSourceProperties.SINGLE_NAME_PLACEHOLDER);
		
		// Persistence unit name
		if (StringUtils.hasText(properties.getPersistenceUnitName())) {
			
			properties.setPersistenceUnitName(properties.getPersistenceUnitName().replaceAll(pattern, name));
		}
		
		XHibernate hibernate = properties.getHibernate();
		
		// Default schema
		if (StringUtils.hasText(hibernate.getDefaultSchema())) {
			
			hibernate.setDefaultSchema(hibernate.getDefaultSchema().replaceAll(pattern, name));
		}
		
		return properties;
	}
}
