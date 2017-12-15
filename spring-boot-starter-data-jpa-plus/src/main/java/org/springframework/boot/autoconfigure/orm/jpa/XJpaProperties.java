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

import java.lang.reflect.Method;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

/**
 * Custom {@link JpaProperties}
 */
@ConfigurationProperties(XJpaProperties.PREFIX)
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class XJpaProperties extends JpaProperties {
	
	/**
	 * Prefix
	 */
	public static final String PREFIX = "spring.jpa";
	
	/**
	 * Persistence unit name
	 */
	private String persistenceUnitName;
	
	/**
	 * {@link XHibernate}
	 */
	private XHibernate hibernate = new XHibernate();
	
	@Override
	public Map<String, Object> getHibernateProperties(HibernateSettings settings) {
		
		return this.hibernate.getAdditionalProperties(this.getProperties(), settings);
	}
	
	/**
	 * Custom {@link org.springframework.boot.autoconfigure.orm.jpa.JpaProperties.Hibernate}
	 */
	@Data
	@EqualsAndHashCode(callSuper = true)
	@ToString(callSuper = true)
	public static class XHibernate extends Hibernate {
		
		/**
		 * "hibernate.default_schema"
		 * 
		 * @see "org.hibernate.cfg.AvailableSettings"
		 */
		protected static final String DEFAULT_SCHEMA = "hibernate.default_schema";
		
		/**
		 * Default schema
		 */
		private String defaultSchema;
		
		/**
		 * Get additional properties
		 * 
		 * @param existing existing properties
		 * @param settings {@link HibernateSettings}
		 * @return result properties
		 */
		protected Map<String, Object> getAdditionalProperties(Map<String, String> existing,
			HibernateSettings settings) {
			
			// Use properties in this class
			Method method = ReflectionUtils.findMethod(Hibernate.class, "getAdditionalProperties", Map.class,
				HibernateSettings.class);
			
			Assert.notNull(method, "Method 'getAdditionalProperties' not found");
			
			ReflectionUtils.makeAccessible(method);
			
			@SuppressWarnings("unchecked")
			Map<String, Object> result = (Map<String, Object>) ReflectionUtils.invokeMethod(method, this, existing,
				settings);
			
			// Default schema
			this.applyDefaultSchema(result);
			
			return result;
		}
		
		/**
		 * Apply default schema
		 * 
		 * @param properties properties
		 */
		protected void applyDefaultSchema(@NonNull Map<String, Object> properties) {
			
			if (StringUtils.hasText(this.defaultSchema)) {
				
				properties.put(DEFAULT_SCHEMA, this.defaultSchema);
			}
		}
	}
}
