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

package org.springframework.security.web.header.writers;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.web.header.HeaderWriter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

/**
 * First match {@link DelegatingRequestMatcherHeaderWriter}
 */
public class XFirstMatchRequestMatcherHeaderWriter implements HeaderWriter {
	
	/**
	 * {@link HeaderWriter}
	 */
	private List<HeaderWriter> writers;
	
	/**
	 * Constructor
	 *
	 * @param writers the {@link HeaderWriter}
	 */
	public XFirstMatchRequestMatcherHeaderWriter(DelegatingRequestMatcherHeaderWriter... writers) {
		
		this.writers = Arrays.asList(writers);
	}
	
	@Override
	public void writeHeaders(HttpServletRequest request, HttpServletResponse response) {
		
		for (HeaderWriter writer : this.writers) {
			
			Field field = ReflectionUtils.findField(DelegatingRequestMatcherHeaderWriter.class, "requestMatcher");
			
			Assert.notNull(field, "Field 'requestMatcher' not found");
			
			ReflectionUtils.makeAccessible(field);
			
			if (((RequestMatcher) ReflectionUtils.getField(field, writer)).matches(request)) {
				
				writer.writeHeaders(request, response);
				
				break;
			}
		}
	}
}
