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

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import lombok.NonNull;

/**
 * Custom {@link RedisCacheManager}
 */
public class CustomRedisCacheManager extends RedisCacheManager {
	
	/**
	 * Name for {@link #getCacheWriter(RedisCacheManager)}
	 */
	private static final String CACHE_WRITER_NAME = "cacheWriter";
	
	/**
	 * Name for {@link #getDefaultCacheConfiguration(RedisCacheManager)}
	 */
	private static final String DEFAULT_CACHE_CONFIGURATION_NAME = "defaultCacheConfig";
	
	/**
	 * Name for {@link #getInitialCacheConfigurations(RedisCacheManager)}
	 */
	private static final String INITIAL_CACHE_CONFIGURATIONS_NAME = "initialCacheConfiguration";
	
	/**
	 * Dynamic {@link RedisCacheConfiguration}
	 */
	private Map<String, RedisCacheConfiguration> dynamicCacheConfigs = new ConcurrentHashMap<>();
	
	/**
	 * Constructor
	 * 
	 * @param sourceCacheManager source {@link RedisCacheManager}
	 */
	public CustomRedisCacheManager(@NonNull RedisCacheManager sourceCacheManager) {
		
		this(sourceCacheManager, getCacheWriter(sourceCacheManager));
	}
	
	/**
	 * Constructor
	 * 
	 * @param sourceCacheManager source {@link RedisCacheManager}
	 * @param cacheWriter {@link RedisCacheWriter}
	 */
	public CustomRedisCacheManager(@NonNull RedisCacheManager sourceCacheManager, RedisCacheWriter cacheWriter) {
		
		super(
		/* @formatter:off */
			cacheWriter,
			getDefaultCacheConfiguration(sourceCacheManager),
			getInitialCacheConfigurations(sourceCacheManager)
			/* @formatter:on */
		);
		
		this.setTransactionAware(sourceCacheManager.isTransactionAware());
	}
	
	/**
	 * Get dynamic {@link RedisCacheConfiguration}
	 * 
	 * @return dynamic {@link RedisCacheConfiguration}
	 */
	public Map<String, RedisCacheConfiguration> getDynamicCacheConfigurations() {
		
		return Collections.unmodifiableMap(this.dynamicCacheConfigs);
	}
	
	/**
	 * Set dynamic {@link RedisCacheConfiguration}
	 * 
	 * @param name name
	 * @param cacheConfig {@link RedisCacheConfiguration}
	 * @return {@link CustomRedisCacheManager}
	 */
	public CustomRedisCacheManager setDynamicCacheConfiguration(String name, RedisCacheConfiguration cacheConfig) {
		
		this.dynamicCacheConfigs.put(name, cacheConfig);
		
		return this;
	}
	
	@Override
	protected RedisCache getMissingCache(String name) {
		
		if (this.dynamicCacheConfigs.containsKey(name)) {
			
			return this.createRedisCache(name, this.dynamicCacheConfigs.get(name));
		}
		
		return super.getMissingCache(name);
	}
	
	/**
	 * Get {@link RedisCacheWriter}
	 * 
	 * @param cacheManager {@link RedisCacheManager}
	 * @return {@link RedisCacheWriter}
	 */
	public static RedisCacheWriter getCacheWriter(RedisCacheManager cacheManager) {
		
		Field field = getAccessibleField(CACHE_WRITER_NAME);
		
		return (RedisCacheWriter) ReflectionUtils.getField(field, cacheManager);
	}
	
	/**
	 * Get default {@link RedisCacheConfiguration}
	 * 
	 * @param cacheManager {@link RedisCacheManager}
	 * @return default {@link RedisCacheConfiguration}
	 */
	public static RedisCacheConfiguration getDefaultCacheConfiguration(RedisCacheManager cacheManager) {
		
		Field field = getAccessibleField(DEFAULT_CACHE_CONFIGURATION_NAME);
		
		return (RedisCacheConfiguration) ReflectionUtils.getField(field, cacheManager);
	}
	
	/**
	 * Set default {@link RedisCacheConfiguration}
	 * 
	 * @param cacheManager {@link RedisCacheManager}
	 * @param cacheConfiguration {@link RedisCacheConfiguration}
	 */
	public static void setDefaultCacheConfiguration(RedisCacheManager cacheManager,
		RedisCacheConfiguration cacheConfiguration) {
		
		Field field = getAccessibleField(DEFAULT_CACHE_CONFIGURATION_NAME);
		
		ReflectionUtils.setField(field, cacheManager, cacheConfiguration);
	}
	
	/**
	 * Get initial {@link RedisCacheConfiguration}
	 * 
	 * @param cacheManager {@link RedisCacheManager}
	 * @return initial {@link RedisCacheConfiguration}
	 */
	public static Map<String, RedisCacheConfiguration> getInitialCacheConfigurations(RedisCacheManager cacheManager) {
		
		Field field = getAccessibleField(INITIAL_CACHE_CONFIGURATIONS_NAME);
		
		@SuppressWarnings("unchecked")
		Map<String, RedisCacheConfiguration> configuration = (Map<String, RedisCacheConfiguration>) ReflectionUtils
			.getField(field, cacheManager);
		
		return configuration;
	}
	
	/**
	 * Get accessible {@link Field}
	 * 
	 * @param name {@link Field}
	 * @return {@link Field}
	 */
	protected static Field getAccessibleField(String name) {
		
		Field field = ReflectionUtils.findField(RedisCacheManager.class, name);
		
		Assert.notNull(field, String.format("Field '%s' not found", name));
		
		ReflectionUtils.makeAccessible(field);
		
		return field;
	}
}
