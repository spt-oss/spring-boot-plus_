/*
 * Copyright 2017-2019 the original author or authors.
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

package org.springframework.security.web.header.writers;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.web.header.HeaderWriter;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * OR {@link DelegatingRequestMatcherHeaderWriter}
 */
public class XOrDelegatingRequestMatcherHeaderWriter implements HeaderWriter {
	
	/**
	 * Writers
	 */
	private Map<RequestMatcher, HeaderWriter> writers = new LinkedHashMap<>();
	
	/**
	 * Add
	 * 
	 * @param matcher {@link RequestMatcher}
	 * @param writer {@link HeaderWriter}
	 * @return {@link XOrDelegatingRequestMatcherHeaderWriter}
	 */
	public XOrDelegatingRequestMatcherHeaderWriter add(RequestMatcher matcher, HeaderWriter writer) {
		
		this.writers.put(matcher, writer);
		
		return this;
	}
	
	@Override
	public void writeHeaders(HttpServletRequest request, HttpServletResponse response) {
		
		for (Entry<RequestMatcher, HeaderWriter> entry : this.writers.entrySet()) {
			
			if (entry.getKey().matches(request)) {
				
				entry.getValue().writeHeaders(request, response);
				
				break;
			}
		}
	}
}
