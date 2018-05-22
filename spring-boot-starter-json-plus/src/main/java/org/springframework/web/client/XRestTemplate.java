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

package org.springframework.web.client;

import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.NonNull;

/**
 * Custom {@link RestTemplate}
 */
public class XRestTemplate extends RestTemplate implements XRestOperations {
	
	/**
	 * Get {@link ObjectMapper}
	 * 
	 * @param restTemplate {@link RestTemplate}
	 * @return {@link ObjectMapper}
	 * @throws IllegalStateException if failed to get
	 */
	public static ObjectMapper getObjectMapper(RestTemplate restTemplate) throws IllegalStateException {
		
		return getMappingJackson2HttpMessageConverter(restTemplate).getObjectMapper();
	}
	
	/**
	 * Set {@link ObjectMapper}
	 * 
	 * @param restTemplate {@link RestTemplate}
	 * @param objectMapper {@link ObjectMapper}
	 * @throws IllegalStateException if failed to set
	 */
	public static void setObjectMapper(RestTemplate restTemplate, ObjectMapper objectMapper)
		throws IllegalStateException {
		
		getMappingJackson2HttpMessageConverter(restTemplate).setObjectMapper(objectMapper);
	}
	
	/**
	 * Get {@link MappingJackson2HttpMessageConverter}
	 * 
	 * @param restTemplate {@link RestTemplate}
	 * @return {@link MappingJackson2HttpMessageConverter}
	 * @throws IllegalStateException if failed to get
	 */
	public static MappingJackson2HttpMessageConverter getMappingJackson2HttpMessageConverter(
		@NonNull RestTemplate restTemplate) throws IllegalStateException {
		
		for (HttpMessageConverter<?> converter : restTemplate.getMessageConverters()) {
			
			if (converter instanceof MappingJackson2HttpMessageConverter) {
				
				return (MappingJackson2HttpMessageConverter) converter;
			}
		}
		
		throw new IllegalStateException("'MappingJackson2HttpMessageConverter' not found");
	}
}
