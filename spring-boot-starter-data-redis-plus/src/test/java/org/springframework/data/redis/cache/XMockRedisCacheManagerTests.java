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

package org.springframework.data.redis.cache;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

/**
 * Tests for {@link XMockRedisCacheManager}
 */
public class XMockRedisCacheManagerTests {
	
	/**
	 * {@link XMockRedisCacheManager#XMockRedisCacheManager(RedisCacheManager)}
	 */
	@Test
	public void constructor() {
		
		RedisCacheManager sourceCacheManager = RedisCacheManager.builder(new LettuceConnectionFactory())
		/* @formatter:off */
			.transactionAware()
			.build();
			/* @formatter:on */
		
		XMockRedisCacheManager cacheManager = new XMockRedisCacheManager(sourceCacheManager);
		RedisCacheWriter cacheWriter = XRedisCacheManager.getCacheWriter(cacheManager);
		
		assertThat(cacheWriter.getClass()).isEqualTo(XMockRedisCacheWriter.class);
		
		assertThat(XMockRedisCacheWriter.getConnectionFactory(cacheWriter)).isEqualTo(
			XMockRedisCacheWriter.getConnectionFactory(XRedisCacheManager.getCacheWriter(sourceCacheManager)));
		
		assertThat(XRedisCacheManager.getDefaultCacheConfiguration(cacheManager))
			.isEqualTo(XRedisCacheManager.getDefaultCacheConfiguration(sourceCacheManager));
		
		assertThat(XRedisCacheManager.getInitialCacheConfigurations(cacheManager))
			.isEqualTo(XRedisCacheManager.getInitialCacheConfigurations(sourceCacheManager));
		
		assertThat(cacheManager.isTransactionAware()).isTrue();
	}
}
