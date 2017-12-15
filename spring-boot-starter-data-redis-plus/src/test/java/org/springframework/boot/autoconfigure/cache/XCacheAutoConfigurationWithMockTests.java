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
import org.springframework.boot.autoconfigure.cache.XCacheAutoConfigurationWithMockTests.XOptionalConfiguration;
import org.springframework.boot.autoconfigure.cache.XRedisCacheConfiguration.XRedisKeyGeneratorConfiguration;
import org.springframework.boot.autoconfigure.data.redis.XRedisAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.XRedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Tests for {@link XCacheAutoConfiguration} with mock
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
	/* @formatter:off */
	XRedisAutoConfiguration.class,
	XCacheAutoConfiguration.class,
	XOptionalConfiguration.class
	/* @formatter:on */
})
@ActiveProfiles({ "test", "test-cache-config-with-mock" })
public class XCacheAutoConfigurationWithMockTests {
	
	/**
	 * {@link XRedisCacheManager}
	 */
	@Autowired
	private XRedisCacheManager cacheManager;
	
	/**
	 * {@link CachingConfigurer}
	 */
	@Autowired(required = false)
	private CachingConfigurer cachingConfigurer;
	
	/**
	 * {@link XRedisCacheConfiguration#cacheManager(RedisConnectionFactory, ResourceLoader)}
	 */
	@Test
	public void cacheManager() {
		
		assertThat(XRedisCacheManager.getCacheWriter(this.cacheManager)).isNotNull();
		assertThat(XRedisCacheManager.getDefaultCacheConfiguration(this.cacheManager)).isNotNull();
		assertThat(XRedisCacheManager.getInitialCacheConfigurations(this.cacheManager)).isNotNull();
		assertThat(this.cacheManager.getDynamicCacheConfigurations()).isNotEmpty();
	}
	
	/**
	 * {@link XRedisKeyGeneratorConfiguration#keyGenerator()}
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
		public CacheManagerCustomizer<XRedisCacheManager> cacheManagerCustomizer() {
			
			// TODO test
			return cacheManager -> {
				
				RedisCacheConfiguration defaultConfig = XRedisCacheManager.getDefaultCacheConfiguration(cacheManager);
				
				cacheManager.setDynamicCacheConfiguration("foo", defaultConfig.entryTtl(Duration.ZERO));
			};
		}
	}
}
