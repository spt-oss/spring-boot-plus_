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

package org.springframework.boot.autoconfigure.cache;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CustomCacheAutoConfigurationWithMockTests.XOptionalConfiguration;
import org.springframework.boot.autoconfigure.cache.CustomRedisCacheConfiguration.RedisKeyGeneratorConfiguration;
import org.springframework.boot.autoconfigure.data.redis.CustomRedisAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.cache.CustomRedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * {@link Test}: {@link CustomCacheAutoConfiguration} with mock
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
	/* @formatter:off */
	CustomRedisAutoConfiguration.class,
	CustomCacheAutoConfiguration.class,
	XOptionalConfiguration.class
	/* @formatter:on */
})
@ActiveProfiles({ "test", "test-cache-config-with-mock" })
public class CustomCacheAutoConfigurationWithMockTests {
	
	/**
	 * {@link CustomRedisCacheManager}
	 */
	@Autowired
	private CustomRedisCacheManager cacheManager;
	
	/**
	 * {@link CachingConfigurer}
	 */
	@Autowired(required = false)
	private CachingConfigurer cachingConfigurer;
	
	/**
	 * {@link CustomRedisCacheConfiguration#cacheManager(RedisConnectionFactory, ResourceLoader)}
	 */
	@Test
	public void cacheManager() {
		
		assertThat(CustomRedisCacheManager.getCacheWriter(this.cacheManager)).isNotNull();
		assertThat(CustomRedisCacheManager.getDefaultCacheConfiguration(this.cacheManager)).isNotNull();
		assertThat(CustomRedisCacheManager.getInitialCacheConfigurations(this.cacheManager)).isNotNull();
		assertThat(this.cacheManager.getDynamicCacheConfigurations()).isNotEmpty();
	}
	
	/**
	 * {@link RedisKeyGeneratorConfiguration#keyGenerator()}
	 */
	@Test
	public void keyGenerator() {
		
		assertThat(this.cachingConfigurer).isNull();
	}
	
	/**
	 * {@link Configuration} for optional
	 */
	@Configuration
	protected static class XOptionalConfiguration {
		
		/**
		 * {@link CacheManagerCustomizer}
		 * 
		 * @return {@link CacheManagerCustomizer}
		 */
		@Bean
		public CacheManagerCustomizer<CustomRedisCacheManager> cacheManagerCustomizer() {
			
			// TODO test
			return cacheManager -> {
				
				RedisCacheConfiguration defaultConfig = CustomRedisCacheManager
					.getDefaultCacheConfiguration(cacheManager);
				
				cacheManager.setDynamicCacheConfiguration("foo", defaultConfig.entryTtl(Duration.ZERO));
			};
		}
	}
}
