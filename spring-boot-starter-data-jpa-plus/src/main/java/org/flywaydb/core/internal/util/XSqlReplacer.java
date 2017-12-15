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

package org.flywaydb.core.internal.util;

import java.util.HashMap;

/**
 * SQL replacer
 */
public abstract class XSqlReplacer extends PlaceholderReplacer { // TODO @checkstyle:ignore
	
	/**
	 * Constructor
	 */
	public XSqlReplacer() {
		
		super(new HashMap<String, String>(), "", "");
	}
	
	@Override
	public String replacePlaceholders(String sql) {
		
		if (!StringUtils.hasText(sql)) {
			
			return sql;
		}
		
		return this.replace(sql);
	}
	
	/**
	 * Reqplace
	 * 
	 * @param sql SQL
	 * @return replaced SQL
	 */
	protected abstract String replace(String sql);
}
