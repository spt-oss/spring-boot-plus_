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

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * {@link CustomDataSourceProperties}: Multiple
 */
@ConfigurationProperties(CustomDataSourceProperties.PREFIX)
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MultipleDataSourceProperties extends CustomDataSourceProperties {
	
	/**
	 * Placeholder for single name
	 */
	public static final String SINGLE_NAME_PLACEHOLDER = "{name}";
	
	/**
	 * Multiple
	 */
	private Map<String, CustomDataSourceProperties> multiple = new HashMap<>();
	
	/**
	 * Get single {@link CustomDataSourceProperties}
	 * 
	 * <p>
	 * Note: This method rewrite multiple properties
	 * </p>
	 * 
	 * @param name name
	 * @return {@link CustomDataSourceProperties}
	 */
	public CustomDataSourceProperties getSingleProperties(String name) {
		
		CustomDataSourceProperties properties = this.multiple.getOrDefault(name, new CustomDataSourceProperties());
		
		String pattern = Pattern.quote(SINGLE_NAME_PLACEHOLDER);
		
		// Name
		if (StringUtils.hasText(properties.getName())) {
			
			properties.setName(properties.getName().replaceAll(pattern, name));
		}
		
		// URL
		if (StringUtils.hasText(properties.getUrl())) {
			
			properties.setUrl(properties.getUrl().replaceAll(pattern, name));
		}
		
		// Parameter
		Map<String, Map<String, Object>> parent = properties.getParameter();
		
		for (String parentKey : parent.keySet()) {
			
			Map<String, Object> child = parent.get(parentKey);
			
			for (String childKey : child.keySet()) {
				
				Object value = child.get(childKey);
				
				if (value instanceof String) {
					
					child.put(childKey, ((String) value).replaceAll(pattern, name));
				}
			}
		}
		
		return properties;
	}
}
