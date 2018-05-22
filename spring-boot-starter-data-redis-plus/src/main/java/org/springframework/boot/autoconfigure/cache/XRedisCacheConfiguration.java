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

package org.springframework.boot.autoconfigure.cache;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.cache.XCacheProperties.XRedis;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.XRedisAutoConfiguration;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.XRedisKeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.XMockRedisCacheManager;
import org.springframework.data.redis.cache.XRedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import lombok.NonNull;

/**
 * Custom {@link RedisCacheConfiguration}
 */
@Configuration
@AutoConfigureAfter(XRedisAutoConfiguration.class)
public class XRedisCacheConfiguration extends RedisCacheConfiguration {
	
	/**
	 * {@link XCacheProperties}
	 */
	private XCacheProperties cacheProperties;
	
	/**
	 * {@link CacheManagerCustomizers}
	 */
	private CacheManagerCustomizers customizerInvoker;
	
	/**
	 * Constructor
	 * 
	 * @param cacheProperties {@link XCacheProperties}
	 * @param customizerInvoker {@link CacheManagerCustomizers}
	 * @param redisCacheConfiguration {@link org.springframework.data.redis.cache.RedisCacheConfiguration}
	 */
	public XRedisCacheConfiguration(
	/* @formatter:off */
		@NonNull XCacheProperties cacheProperties,
		@NonNull CacheManagerCustomizers customizerInvoker,
		ObjectProvider<org.springframework.data.redis.cache.RedisCacheConfiguration> redisCacheConfiguration) {
		/* @formatter:on */
		
		super(
		/* @formatter:off */
			cacheProperties,
			customizerInvoker,
			redisCacheConfiguration
			/* @formatter:on */
		);
		
		this.cacheProperties = cacheProperties;
		this.customizerInvoker = customizerInvoker;
	}
	
	@Bean
	@Override
	public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory, ResourceLoader resourceLoader) {
		
		RedisCacheManager cacheManager = super.cacheManager(connectionFactory, resourceLoader);
		
		if (this.cacheProperties.getRedis().isMock()) {
			
			cacheManager = new XMockRedisCacheManager(cacheManager);
		}
		else {
			
			cacheManager = new XRedisCacheManager(cacheManager);
		}
		
		return this.customizerInvoker.customize(cacheManager);
	}
	
	/**
	 * {@link Configuration} for {@link XRedisKeyGenerator}
	 */
	@Configuration
	@ConditionalOnProperty(prefix = XRedis.PREFIX, name = "key", havingValue = "true", matchIfMissing = true)
	protected static class XRedisKeyGeneratorConfiguration extends CachingConfigurerSupport {
		
		/**
		 * {@link KeyGenerator}
		 * 
		 * @return {@link KeyGenerator}
		 */
		@Bean
		@Override
		public XRedisKeyGenerator keyGenerator() {
			
			return new XRedisKeyGenerator();
		}
	}
}
