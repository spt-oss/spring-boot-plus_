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
package org.springframework.web.client;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import lombok.NonNull;

/**
 * Simple {@link XRestRetryTemplate}
 */
public class XSimpleRestRetryTemplate implements XRestRetryTemplate {
	
	/**
	 * {@link Logger}
	 */
	private static final Logger logger = LoggerFactory.getLogger(RestTemplate.class);
	
	/**
	 * {@link RetryTemplate}
	 */
	private final RetryTemplate retryTemplate;
	
	/**
	 * {@link SimpleRetryPolicy}
	 */
	private final SimpleRetryPolicy retryPolicy;
	
	/**
	 * Constructor
	 */
	public XSimpleRestRetryTemplate() {
		
		this(RestClientException.class);
	}
	
	/**
	 * Constructor
	 * 
	 * @param classes classes
	 * @param <E> {@link RestClientException} type
	 */
	@SafeVarargs
	public <E extends RestClientException> XSimpleRestRetryTemplate(@NonNull Class<E>... classes) {
		
		Map<Class<? extends Throwable>, Boolean> exceptions = new HashMap<>();
		
		for (Class<E> clazz : classes) {
			
			exceptions.put(clazz, true);
		}
		
		SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy(1, exceptions);
		
		RetryTemplate retryTemplate = new RetryTemplate();
		retryTemplate.setRetryPolicy(retryPolicy);
		
		this.retryTemplate = retryTemplate;
		this.retryPolicy = retryPolicy;
	}
	
	/**
	 * Set back off period
	 * 
	 * @param period period
	 * @return {@link XSimpleRestRetryTemplate}
	 */
	public XSimpleRestRetryTemplate setBackOffPeriod(@NonNull Duration period) {
		
		FixedBackOffPolicy policy = new FixedBackOffPolicy();
		policy.setBackOffPeriod(period.toMillis());
		
		this.retryTemplate.setBackOffPolicy(policy);
		
		return this;
	}
	
	/**
	 * Set max attempts
	 * 
	 * @param maxAttempts max attempts
	 * @return {@link XSimpleRestRetryTemplate}
	 */
	public XSimpleRestRetryTemplate setMaxAttempts(int maxAttempts) {
		
		this.retryPolicy.setMaxAttempts(maxAttempts);
		
		return this;
	}
	
	@Override
	public <T> T execute(@NonNull Supplier<T> supplier) throws RestClientException {
		
		int maxAttempts = this.retryPolicy.getMaxAttempts();
		
		if (maxAttempts <= 1) {
			
			return supplier.get();
		}
		
		return this.retryTemplate.execute(context -> {
			
			try {
				
				return supplier.get();
			}
			catch (RestClientException e) {
				
				int retryCount = context.getRetryCount() + 1;
				
				logger.warn("Failed in retry ({}/{}): {}", retryCount, maxAttempts, e.toString());
				
				throw e;
			}
		});
	}
}
