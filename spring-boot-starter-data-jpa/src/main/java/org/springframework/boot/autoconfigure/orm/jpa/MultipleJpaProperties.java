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

package org.springframework.boot.autoconfigure.orm.jpa;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.boot.autoconfigure.jdbc.MultipleDataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Multiple {@link JpaProperties}
 */
@ConfigurationProperties(CustomJpaProperties.PREFIX)
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MultipleJpaProperties extends CustomJpaProperties {
	
	/**
	 * Multiple
	 */
	private Map<String, CustomJpaProperties> multiple = new HashMap<>();
	
	/**
	 * Get single {@link CustomJpaProperties}
	 * 
	 * <p>
	 * Note: This method rewrite multiple properties
	 * </p>
	 * 
	 * @param name single name
	 * @return single {@link CustomJpaProperties}
	 */
	public CustomJpaProperties getSingleJpaProperties(String name) {
		
		CustomJpaProperties properties = this.multiple.getOrDefault(name, new CustomJpaProperties());
		
		String pattern = Pattern.quote(MultipleDataSourceProperties.SINGLE_NAME_PLACEHOLDER);
		
		// Persistence unit name
		if (StringUtils.hasText(properties.getPersistenceUnitName())) {
			
			properties.setPersistenceUnitName(properties.getPersistenceUnitName().replaceAll(pattern, name));
		}
		
		CustomHibernate hibernate = properties.getHibernate();
		
		// Default schema
		if (StringUtils.hasText(hibernate.getDefaultSchema())) {
			
			hibernate.setDefaultSchema(hibernate.getDefaultSchema().replaceAll(pattern, name));
		}
		
		return properties;
	}
}
