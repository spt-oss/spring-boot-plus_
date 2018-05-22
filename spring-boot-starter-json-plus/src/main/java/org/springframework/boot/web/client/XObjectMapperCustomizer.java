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

package org.springframework.boot.web.client;

import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.XRestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.NonNull;

/**
 * {@link RestTemplateCustomizer} for simple {@link ObjectMapper}
 */
public class XObjectMapperCustomizer implements RestTemplateCustomizer {
	
	@Override
	public void customize(RestTemplate restTemplate) {
		
		ObjectMapper objectMapper = XRestTemplate.getObjectMapper(restTemplate);
		
		this.customize(objectMapper);
		
		XRestTemplate.setObjectMapper(restTemplate, objectMapper);
	}
	
	/**
	 * Customize
	 * 
	 * @param objectMapper {@link ObjectMapper}
	 */
	protected void customize(@NonNull ObjectMapper objectMapper) {
		
		objectMapper
		/* @formatter:off */
			.registerModules(new JavaTimeModule(), new Jdk8Module())
			.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
			.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			/* @formatter:on */
	}
}
