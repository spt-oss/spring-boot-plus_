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

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.cache.CustomCacheProperties.XRedis;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.CustomRedisAutoConfiguration;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.RedisKeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.cache.MockRedisCacheManager;
import org.springframework.data.redis.cache.CustomRedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import lombok.NonNull;

/**
 * Custom {@link RedisCacheConfiguration}
 */
@Configuration
@AutoConfigureAfter(CustomRedisAutoConfiguration.class)
public class CustomRedisCacheConfiguration extends RedisCacheConfiguration {
	
	/**
	 * {@link CustomCacheProperties}
	 */
	private CustomCacheProperties cacheProperties;
	
	/**
	 * {@link CacheManagerCustomizers}
	 */
	private CacheManagerCustomizers customizerInvoker;
	
	/**
	 * Constructor
	 * 
	 * @param cacheProperties {@link CustomCacheProperties}
	 * @param customizerInvoker {@link CacheManagerCustomizers}
	 * @param redisCacheConfiguration {@link org.springframework.data.redis.cache.RedisCacheConfiguration}
	 */
	public CustomRedisCacheConfiguration(
	/* @formatter:off */
		@NonNull CustomCacheProperties cacheProperties,
		@NonNull CacheManagerCustomizers customizerInvoker,
		ObjectProvider<org.springframework.data.redis.cache.RedisCacheConfiguration> redisCacheConfiguration) {
		/* @formatter:on */
		
		super(cacheProperties, customizerInvoker, redisCacheConfiguration);
		
		this.cacheProperties = cacheProperties;
		this.customizerInvoker = customizerInvoker;
	}
	
	@Bean
	@Override
	public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory, ResourceLoader resourceLoader) {
		
		RedisCacheManager cacheManager = super.cacheManager(connectionFactory, resourceLoader);
		
		if (this.cacheProperties.getRedis().isMock()) {
			
			cacheManager = new MockRedisCacheManager(cacheManager);
		}
		else {
			
			cacheManager = new CustomRedisCacheManager(cacheManager);
		}
		
		return this.customizerInvoker.customize(cacheManager);
	}
	
	/**
	 * {@link Configuration}: {@link RedisKeyGenerator}
	 */
	@Configuration
	@ConditionalOnProperty(prefix = XRedis.PREFIX, name = "key", havingValue = "true", matchIfMissing = true)
	protected static class RedisKeyGeneratorConfiguration extends CachingConfigurerSupport {
		
		/**
		 * {@link KeyGenerator}
		 * 
		 * @return {@link KeyGenerator}
		 */
		@Bean
		@Override
		public RedisKeyGenerator keyGenerator() {
			
			return new RedisKeyGenerator();
		}
	}
}
