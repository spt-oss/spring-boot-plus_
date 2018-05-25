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

package org.springframework.data.redis.cache;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

/**
 * {@link Test}: {@link MockRedisCacheManager}
 */
public class MockRedisCacheManagerTests {
	
	/**
	 * {@link MockRedisCacheManager#MockRedisCacheManager(RedisCacheManager)}
	 */
	@Test
	public void constructor() {
		
		RedisCacheManager sourceCacheManager = RedisCacheManager.builder(new LettuceConnectionFactory())
		/* @formatter:off */
			.transactionAware()
			.build();
			/* @formatter:on */
		
		MockRedisCacheManager cacheManager = new MockRedisCacheManager(sourceCacheManager);
		RedisCacheWriter cacheWriter = CustomRedisCacheManager.getCacheWriter(cacheManager);
		
		assertThat(cacheWriter.getClass()).isEqualTo(MockRedisCacheWriter.class);
		
		assertThat(MockRedisCacheWriter.getConnectionFactory(cacheWriter)).isEqualTo(
			MockRedisCacheWriter.getConnectionFactory(CustomRedisCacheManager.getCacheWriter(sourceCacheManager)));
		
		assertThat(CustomRedisCacheManager.getDefaultCacheConfiguration(cacheManager))
			.isEqualTo(CustomRedisCacheManager.getDefaultCacheConfiguration(sourceCacheManager));
		
		assertThat(CustomRedisCacheManager.getInitialCacheConfigurations(cacheManager))
			.isEqualTo(CustomRedisCacheManager.getInitialCacheConfigurations(sourceCacheManager));
		
		assertThat(cacheManager.isTransactionAware()).isTrue();
	}
}
