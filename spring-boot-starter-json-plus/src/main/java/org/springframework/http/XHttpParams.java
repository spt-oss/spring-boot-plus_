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
package org.springframework.http;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * HTTP parameters
 */
public class XHttpParams extends LinkedMultiValueMap<String, String> {
	
	@SuppressWarnings("javadoc")
	private static final long serialVersionUID = 1L;
	
	/**
	 * {@link #set(String, String)} for {@link Object}
	 * 
	 * @param key key
	 * @param value value
	 * @return {@link XHttpParams}
	 */
	public XHttpParams set(String key, Object value) {
		
		this.set(key, String.valueOf(value));
		
		return this;
	}
	
	/**
	 * {@link #add(String, String)} for {@link Object}
	 * 
	 * @param key key
	 * @param value value
	 * @return {@link XHttpParams}
	 */
	public XHttpParams add(String key, Object value) {
		
		this.add(key, String.valueOf(value));
		
		return this;
	}
	
	/**
	 * To query string
	 * 
	 * @return query string
	 */
	public String toQueryString() {
		
		return UriComponentsBuilder.fromPath("")
		/* @formatter:off */
			.queryParams(this)
			.build()
			.toString()
			.replaceAll("\\?", "");
			/* @formatter:on */
	}
}
