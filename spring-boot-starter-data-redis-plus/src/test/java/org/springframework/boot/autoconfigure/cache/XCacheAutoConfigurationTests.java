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

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.XCacheAutoConfigurationTests.XOptionalConfiguration;
import org.springframework.boot.autoconfigure.cache.XRedisCacheConfiguration.XRedisKeyGeneratorConfiguration;
import org.springframework.boot.autoconfigure.data.redis.XRedisAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.interceptor.XRedisKeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.XRedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.OxmSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Tests for {@link XCacheAutoConfiguration}
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
	/* @formatter:off */
	XRedisAutoConfiguration.class,
	XCacheAutoConfiguration.class,
	XOptionalConfiguration.class
	/* @formatter:on */
})
public class XCacheAutoConfigurationTests {
	
	/**
	 * {@link XRedisCacheManager}
	 */
	@Autowired
	private XRedisCacheManager cacheManager;
	
	/**
	 * {@link CachingConfigurer}
	 */
	@Autowired
	private CachingConfigurer cachingConfigurer;
	
	/**
	 * {@link XRedisCacheConfiguration#cacheManager(RedisConnectionFactory, ResourceLoader)}
	 */
	@Test
	public void cacheManager() {
		
		assertThat(XRedisCacheManager.getCacheWriter(this.cacheManager)).isNotNull();
		assertThat(XRedisCacheManager.getDefaultCacheConfiguration(this.cacheManager)).isNotNull();
		assertThat(XRedisCacheManager.getInitialCacheConfigurations(this.cacheManager)).isNotNull();
		
		// TODO move foo/bar test to customizer
		Map<String, RedisCacheConfiguration> dynamicConfigs = this.cacheManager.getDynamicCacheConfigurations();
		
		assertThat(dynamicConfigs.size()).isEqualTo(2);
		assertThat(dynamicConfigs.containsKey(XOptionalConfiguration.KEY_FOO)).isTrue();
		assertThat(dynamicConfigs.containsKey(XOptionalConfiguration.KEY_BAR)).isTrue();
		
		{
			RedisCacheConfiguration config = dynamicConfigs.get(XOptionalConfiguration.KEY_FOO);
			
			assertThat(config.getTtl()).isEqualTo(XOptionalConfiguration.TTL_FOO);
			assertThat(config.getKeySerializationPair()).isEqualTo(XOptionalConfiguration.KEY_SERIALIZER_FOO);
			assertThat(config.getValueSerializationPair()).isEqualTo(XOptionalConfiguration.VALUE_SERIALIZER_FOO);
		}
		
		{
			RedisCacheConfiguration config = dynamicConfigs.get(XOptionalConfiguration.KEY_BAR);
			
			assertThat(config.getTtl()).isEqualTo(XOptionalConfiguration.TTL_BAR);
			assertThat(config.getKeySerializationPair()).isEqualTo(XOptionalConfiguration.KEY_SERIALIZER_BAR);
			assertThat(config.getValueSerializationPair()).isEqualTo(XOptionalConfiguration.VALUE_SERIALIZER_BAR);
		}
	}
	
	/**
	 * {@link XRedisKeyGeneratorConfiguration#keyGenerator()}
	 */
	@Test
	public void keyGenerator() {
		
		assertThat(this.cachingConfigurer.keyGenerator().getClass()).isEqualTo(XRedisKeyGenerator.class);
	}
	
	/**
	 * {@link Configuration} for optional
	 */
	@Configuration
	protected static class XOptionalConfiguration {
		
		/**
		 * Key for foo
		 */
		protected static final String KEY_FOO = "foo";
		
		/**
		 * Key for bar
		 */
		protected static final String KEY_BAR = "bar";
		
		/**
		 * TTL for {@link #KEY_FOO}
		 */
		protected static final Duration TTL_FOO = Duration.ofDays(1);
		
		/**
		 * TTL for {@link #KEY_BAR}
		 */
		protected static final Duration TTL_BAR = Duration.ofDays(2);
		
		/**
		 * {@link SerializationPair} for {@link #KEY_FOO}
		 */
		protected static final SerializationPair<String> KEY_SERIALIZER_FOO = SerializationPair
			.fromSerializer(new GenericToStringSerializer<>(String.class));
		
		/**
		 * {@link SerializationPair} for {@link #KEY_BAR}
		 */
		protected static final SerializationPair<String> KEY_SERIALIZER_BAR = SerializationPair
			.fromSerializer(new Jackson2JsonRedisSerializer<>(String.class));
		
		/**
		 * {@link SerializationPair} for {@link #KEY_FOO}
		 */
		protected static final SerializationPair<Object> VALUE_SERIALIZER_FOO = SerializationPair
			.fromSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
		
		/**
		 * {@link SerializationPair} for {@link #KEY_BAR}
		 */
		protected static final SerializationPair<Object> VALUE_SERIALIZER_BAR = SerializationPair
			.fromSerializer(new OxmSerializer());
		
		/**
		 * {@link CacheManagerCustomizer}
		 * 
		 * @return {@link CacheManagerCustomizer}
		 */
		@Bean
		public CacheManagerCustomizer<XRedisCacheManager> cacheManagerCustomizer() {
			
			return cacheManager -> {
				
				RedisCacheConfiguration defaultConfig = XRedisCacheManager.getDefaultCacheConfiguration(cacheManager);
				
				cacheManager.setDynamicCacheConfiguration(KEY_FOO, defaultConfig.entryTtl(TTL_FOO)
					.serializeKeysWith(KEY_SERIALIZER_FOO).serializeValuesWith(VALUE_SERIALIZER_FOO));
				
				cacheManager.setDynamicCacheConfiguration(KEY_BAR, defaultConfig.entryTtl(TTL_BAR)
					.serializeKeysWith(KEY_SERIALIZER_BAR).serializeValuesWith(VALUE_SERIALIZER_BAR));
			};
		}
	}
}
