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
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import lombok.Getter;

/**
 * Mock {@link RedisCache}
 */
public class MockRedisCacheWriter extends DefaultRedisCacheWriter {
	
	/**
	 * Delegate
	 */
	private Map<String, ConcurrentMapCache> delegate = new ConcurrentHashMap<>();
	
	/**
	 * Constructor
	 * 
	 * @param sourceCacheWriter source {@link RedisCacheWriter}
	 */
	public MockRedisCacheWriter(RedisCacheWriter sourceCacheWriter) {
		
		this(getConnectionFactory(sourceCacheWriter));
	}
	
	/**
	 * Constructor
	 * 
	 * @param connectionFactory {@link RedisConnectionFactory}
	 */
	public MockRedisCacheWriter(RedisConnectionFactory connectionFactory) {
		
		super(connectionFactory);
	}
	
	@Override
	public byte[] get(String name, byte[] key) {
		
		ValueWrapper wrapper = this.loadCache(name).get(key);
		
		return this.toByteValue(wrapper);
	}
	
	@Override
	public void put(String name, byte[] key, byte[] value, Duration ttl) {
		
		this.loadCache(name).put(key, this.toStoreValue(value, ttl));
	}
	
	@Override
	public byte[] putIfAbsent(String name, byte[] key, byte[] value, Duration ttl) {
		
		ValueWrapper wrapper = this.loadCache(name).putIfAbsent(key, this.toStoreValue(value, ttl));
		
		return this.toByteValue(wrapper);
	}
	
	@Override
	public void remove(String name, byte[] key) {
		
		this.loadCache(name).evict(key);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * TODO: this method is not implemented
	 * </p>
	 */
	@Override
	public void clean(String name, byte[] pattern) {
		
		this.loadCache(name).clear();
	}
	
	/**
	 * Load cache
	 * 
	 * @param name name
	 * @return {@link ConcurrentMapCache}
	 */
	protected ConcurrentMapCache loadCache(String name) {
		
		if (this.delegate.containsKey(name)) {
			
			return this.delegate.get(name);
		}
		
		synchronized (this.delegate) {
			
			this.delegate.putIfAbsent(name, new ConcurrentMapCache(name));
			
			return this.delegate.get(name);
		}
	}
	
	/**
	 * To store value
	 * 
	 * @param value value
	 * @param ttl TTL
	 * @return {@link CacheStoreValue}
	 */
	protected CacheStoreValue toStoreValue(byte[] value, Duration ttl) {
		
		return new CacheStoreValue(value, ttl);
	}
	
	/**
	 * To byte value
	 * 
	 * @param wrapper {@link ValueWrapper}
	 * @return byte value or {@code null}
	 */
	protected byte[] toByteValue(ValueWrapper wrapper) {
		
		if (wrapper != null) {
			
			CacheStoreValue stored = (CacheStoreValue) wrapper.get();
			
			if (!stored.isExpired()) {
				
				return stored.getValue();
			}
		}
		
		return null;
	}
	
	/**
	 * Get {@link RedisConnectionFactory}
	 * 
	 * @param cacheWriter {@link RedisCacheWriter}
	 * @return {@link RedisConnectionFactory}
	 */
	public static RedisConnectionFactory getConnectionFactory(RedisCacheWriter cacheWriter) {
		
		Assert.isTrue(cacheWriter instanceof DefaultRedisCacheWriter, "Unsupported RedisCacheWriter");
		
		Field field = ReflectionUtils.findField(DefaultRedisCacheWriter.class, "connectionFactory");
		
		Assert.notNull(field, "Field 'connectionFactory' not found");
		
		ReflectionUtils.makeAccessible(field);
		
		return (RedisConnectionFactory) ReflectionUtils.getField(field, cacheWriter);
	}
	
	/**
	 * Cache store value
	 */
	protected static class CacheStoreValue {
		
		/**
		 * Value
		 */
		@Getter
		private byte[] value;
		
		/**
		 * Expiration
		 */
		private LocalDateTime expiration;
		
		/**
		 * Constructor
		 * 
		 * @param value value
		 * @param ttl TTL
		 */
		public CacheStoreValue(byte[] value, Duration ttl) {
			
			this.value = value;
			
			if (ttl != null && !ttl.isZero() && !ttl.isNegative()) {
				
				this.expiration = LocalDateTime.now().plus(ttl);
			}
		}
		
		/**
		 * Is expired?
		 * 
		 * @return {@code true} if expired
		 */
		public boolean isExpired() {
			
			if (this.expiration == null) {
				
				return false;
			}
			
			return this.expiration.isBefore(LocalDateTime.now());
		}
	}
}
