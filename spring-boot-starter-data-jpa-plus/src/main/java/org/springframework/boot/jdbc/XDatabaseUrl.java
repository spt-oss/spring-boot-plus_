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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.CollectionUtils;

import lombok.AllArgsConstructor;
import lombok.NonNull;

/**
 * Database URL
 */
@AllArgsConstructor
public enum XDatabaseUrl {
	
	/**
	 * H2
	 */
	H2(";", ";") { // TODO @checkstyle:ignore
		
		@Override
		protected String convertParamKey(String key) {
			
			// foo-bar -> fooBar
			String converted = super.convertParamKey(key);
			
			// fooBar -> foo_Bar
			converted = converted.replaceAll("([a-z])([A-Z]+)", "$1_$2");
			
			// foo_Bar -> FOO_BAR
			return converted.toUpperCase();
		}
		
		@Override
		protected String convertParamValue(Object value) {
			
			String converted = String.valueOf(value);
			
			if (value instanceof Boolean) {
				
				converted = converted.toUpperCase();
			}
			
			return converted;
		}
	},
	
	/**
	 * MySQL
	 */
	MYSQL("?", "&") {
		
		@Override
		protected String convertParamKey(String key) {
			
			// foo-bar -> fooBar
			String converted = super.convertParamKey(key);
			
			// Rename
			converted = converted.replaceAll("^useSsl$", "useSSL");
			
			return converted;
		}
	};
	
	/**
	 * Parameter starter
	 */
	private String paramStarter;
	
	/**
	 * Paramter entry joiner
	 */
	private String paramEntryJoiner;
	
	/**
	 * Build
	 * 
	 * @param url URL
	 * @param params parameters
	 * @return URL
	 * @throws IllegalStateException if failed to build
	 */
	public static String build(@NonNull String url, Map<String, Object> params) throws IllegalStateException {
		
		for (XDatabaseUrl value : values()) {
			
			if (url.startsWith("jdbc:" + value.name().toLowerCase() + ":")) {
				
				return value.buildInternal(url, params);
			}
		}
		
		throw new IllegalStateException("Faile to build URL: " + url);
	}
	
	/**
	 * Build internal
	 * 
	 * @param url URL
	 * @param params params
	 * @return URL
	 */
	protected String buildInternal(@NonNull String url, Map<String, Object> params) {
		
		if (CollectionUtils.isEmpty(params)) {
			
			return url;
		}
		
		List<String> entries = new ArrayList<>();
		
		for (Entry<String, Object> entry : params.entrySet()) {
			
			StringBuilder query = new StringBuilder();
			query.append(this.convertParamKey(entry.getKey()));
			query.append("=");
			query.append(this.convertParamValue(entry.getValue()));
			
			entries.add(query.toString());
		}
		
		StringBuilder builder = new StringBuilder();
		builder.append(url);
		
		if (!entries.isEmpty()) {
			
			if (!url.contains(this.paramStarter)) {
				
				builder.append(this.paramStarter);
			}
			else {
				
				builder.append(this.paramEntryJoiner);
			}
			
			builder.append(String.join(this.paramEntryJoiner, entries));
		}
		
		return builder.toString();
	}
	
	/**
	 * Convert parameter key
	 * 
	 * @param key key
	 * @return key
	 */
	protected String convertParamKey(String key) {
		
		// foo-bar -> fooBar
		Matcher matcher = Pattern.compile("-[a-z]").matcher(key);
		
		StringBuilder builder = new StringBuilder();
		int index = 0;
		
		while (matcher.find()) {
			
			builder.append(key.substring(index, matcher.start()));
			builder.append(matcher.group(0).replaceAll("-", "").toUpperCase());
			
			index = matcher.end();
		}
		
		builder.append(key.substring(index));
		
		return builder.toString();
	}
	
	/**
	 * Convert parameter value
	 * 
	 * @param value value
	 * @return value
	 */
	protected String convertParamValue(Object value) {
		
		return String.valueOf(value);
	}
}
