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

package org.springframework.boot.web.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.LoggingInterceptor;
import org.springframework.web.client.RestTemplate;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * {@link RestTemplateCustomizer}: {@link LoggingInterceptor}
 */
@RequiredArgsConstructor
public class LoggingInterceptorCustomizer implements RestTemplateCustomizer {
	
	/**
	 * {@link Logger}
	 */
	@NonNull
	private final Logger logger;
	
	/**
	 * Constructor
	 */
	public LoggingInterceptorCustomizer() {
		
		this(LoggerFactory.getLogger(RestTemplate.class));
	}
	
	@Override
	public void customize(RestTemplate restTemplate) {
		
		new RestTemplateBuilder()
		/* @formatter:off */
			.additionalInterceptors(new LoggingInterceptor(this.logger).setShowBody(this.isShowBody()))
			.configure(restTemplate);
			/* @formatter:on */
	}
	
	/**
	 * Is show body?
	 * 
	 * @return {@code true} if show
	 */
	protected boolean isShowBody() {
		
		Logger showBody = LoggerFactory.getLogger(this.logger.getName() + ".showBody");
		
		/* @formatter:off */
		return showBody.isTraceEnabled()
			|| showBody.isDebugEnabled()
			|| showBody.isInfoEnabled()
			|| showBody.isWarnEnabled()
			|| showBody.isErrorEnabled();
		/* @formatter:on */
	}
}
