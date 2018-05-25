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

package org.springframework.http.client;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;

import org.springframework.util.ReflectionUtils;

/**
 * {@link SimpleClientHttpResponse} utilities
 */
public class SimpleClientHttpResponses {
	
	/**
	 * Constructor
	 */
	protected SimpleClientHttpResponses() {
		
		/* NOP */
	}
	
	/**
	 * Get status line
	 * 
	 * @param response {@link SimpleClientHttpResponse}
	 * @return status line
	 * @throws IOException if failed to get
	 */
	public static String getStatusLine(SimpleClientHttpResponse response) throws IOException {
		
		Field field = ReflectionUtils.findField(SimpleClientHttpResponse.class, "connection");
		ReflectionUtils.makeAccessible(field);
		
		return ((HttpURLConnection) ReflectionUtils.getField(field, response)).getHeaderField(0);
	}
}
