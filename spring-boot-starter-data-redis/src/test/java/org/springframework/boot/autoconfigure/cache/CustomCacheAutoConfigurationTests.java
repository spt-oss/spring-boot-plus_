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
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CustomCacheAutoConfigurationTests.XOptionalConfiguration;
import org.springframework.boot.autoconfigure.cache.CustomRedisCacheConfiguration.RedisKeyGeneratorConfiguration;
import org.springframework.boot.autoconfigure.data.redis.CustomRedisAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.interceptor.RedisKeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.cache.CustomRedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.OxmSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * {@link Test}: {@link CustomCacheAutoConfiguration}
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
	/* @formatter:off */
	CustomRedisAutoConfiguration.class,
	CustomCacheAutoConfiguration.class,
	XOptionalConfiguration.class
	/* @formatter:on */
})
public class CustomCacheAutoConfigurationTests {
	
	/**
	 * {@link CustomRedisCacheManager}
	 */
	@Autowired
	private CustomRedisCacheManager cacheManager;
	
	/**
	 * {@link CachingConfigurer}
	 */
	@Autowired
	private CachingConfigurer cachingConfigurer;
	
	/**
	 * {@link CustomRedisCacheConfiguration#cacheManager(RedisConnectionFactory, ResourceLoader)}
	 */
	@Test
	public void cacheManager() {
		
		assertThat(CustomRedisCacheManager.getCacheWriter(this.cacheManager)).isNotNull();
		assertThat(CustomRedisCacheManager.getDefaultCacheConfiguration(this.cacheManager)).isNotNull();
		assertThat(CustomRedisCacheManager.getInitialCacheConfigurations(this.cacheManager)).isNotNull();
		
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
	 * {@link RedisKeyGeneratorConfiguration#keyGenerator()}
	 */
	@Test
	public void keyGenerator() {
		
		assertThat(this.cachingConfigurer.keyGenerator().getClass()).isEqualTo(RedisKeyGenerator.class);
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
		public CacheManagerCustomizer<CustomRedisCacheManager> cacheManagerCustomizer() {
			
			return cacheManager -> {
				
				RedisCacheConfiguration defaultConfig = CustomRedisCacheManager
					.getDefaultCacheConfiguration(cacheManager);
				
				cacheManager.setDynamicCacheConfiguration(KEY_FOO, defaultConfig.entryTtl(TTL_FOO)
					.serializeKeysWith(KEY_SERIALIZER_FOO).serializeValuesWith(VALUE_SERIALIZER_FOO));
				
				cacheManager.setDynamicCacheConfiguration(KEY_BAR, defaultConfig.entryTtl(TTL_BAR)
					.serializeKeysWith(KEY_SERIALIZER_BAR).serializeValuesWith(VALUE_SERIALIZER_BAR));
			};
		}
	}
}
