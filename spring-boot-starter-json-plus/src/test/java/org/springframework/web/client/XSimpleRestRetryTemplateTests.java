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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.XLoggingInterceptorCustomizer;

/**
 * Tests for {@link XSimpleRestRetryTemplate}
 */
public class XSimpleRestRetryTemplateTests {
	
	/**
	 * {@link RestTemplate}
	 */
	private RestTemplate restTemplate;
	
	/**
	 * {@link Before}
	 */
	@Before
	public void before() {
		
		this.restTemplate = new RestTemplateBuilder()
		/* @formatter:off */
			.customizers(new XLoggingInterceptorCustomizer())
			.build();
			/* @formatter:on */
	}
	
	/**
	 * {@link XRestRetryTemplate#execute(java.util.function.Supplier)}
	 */
	@Test
	public void execute() {
		
		{
			XRestRetryTemplate retryTemplate = new XSimpleRestRetryTemplate()
			/* @formatter:off */
				.setBackOffPeriod(Duration.ofMillis(500));
				/* @formatter:on */
			
			AtomicInteger counter = new AtomicInteger();
			
			try {
				
				retryTemplate.execute(() -> {
					
					counter.incrementAndGet();
					
					return this.restTemplate.getForObject("http://foo.bar.baz.qux/foo", String.class);
				});
				
				fail();
			}
			catch (RestClientException e) {
				
				assertThat(counter.get()).isEqualTo(1);
			}
		}
		
		{
			XRestRetryTemplate retryTemplate = new XSimpleRestRetryTemplate(ResourceAccessException.class)
			/* @formatter:off */
				.setBackOffPeriod(Duration.ofMillis(500))
				.setMaxAttempts(3);
				/* @formatter:on */
			
			AtomicInteger counter = new AtomicInteger();
			
			try {
				
				retryTemplate.execute(() -> {
					
					counter.incrementAndGet();
					
					return this.restTemplate.getForObject("http://foo.bar.baz.qux/bar", String.class);
				});
				
				fail();
			}
			catch (ResourceAccessException e) {
				
				assertThat(counter.get()).isEqualTo(3);
			}
			catch (RestClientException e) {
				
				fail();
			}
		}
	}
}
