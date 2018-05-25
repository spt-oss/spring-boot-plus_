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

import java.time.Duration;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

/**
 * {@link Test}: {@link MockRedisCacheWriter}
 */
public class MockRedisCacheWriterTests {
	
	/**
	 * Name
	 */
	private static final String NAME = "name";
	
	/**
	 * Key
	 */
	private static final byte[] KEY = "key".getBytes();
	
	/**
	 * Value
	 */
	private static final byte[] VALUE = "value".getBytes();
	
	/**
	 * TTL
	 */
	private static final Duration TTL = Duration.ofMillis(100);
	
	/**
	 * {@link MockRedisCacheWriter}
	 */
	private MockRedisCacheWriter cacheWriter;
	
	/**
	 * {@link Before}
	 */
	@Before
	public void before() {
		
		this.cacheWriter = new MockRedisCacheWriter(new LettuceConnectionFactory());
	}
	
	/**
	 * {@link MockRedisCacheWriter#get(String, byte[])}
	 */
	@Test
	public void get() {
		
		this.cacheWriter.put(NAME, KEY, VALUE, TTL);
		
		assertThat(this.cacheWriter.get(NAME, KEY)).isEqualTo(VALUE);
		
		try {
			
			Thread.sleep(TTL.toMillis() * 2);
		}
		catch (InterruptedException e) {
			
			throw new IllegalStateException(e);
		}
		
		assertThat(this.cacheWriter.get(NAME, KEY)).isNull();
	}
	
	/**
	 * {@link MockRedisCacheWriter#put(String, byte[], byte[], Duration)}
	 */
	public void put() {
		
		this.cacheWriter.put(NAME, KEY, VALUE, TTL);
		
		assertThat(this.cacheWriter.get(NAME, KEY)).isEqualTo(VALUE);
	}
	
	/**
	 * {@link MockRedisCacheWriter#putIfAbsent(String, byte[], byte[], Duration)}
	 */
	public void putIfAbsent() {
		
		byte[] temporary = "temporary".getBytes();
		
		assertThat(this.cacheWriter.putIfAbsent(NAME, KEY, VALUE, TTL)).isNull();
		assertThat(this.cacheWriter.putIfAbsent(NAME, KEY, temporary, TTL)).isEqualTo(VALUE);
		
		assertThat(this.cacheWriter.get(NAME, KEY)).isEqualTo(temporary);
	}
	
	/**
	 * {@link MockRedisCacheWriter#remove(String, byte[])}
	 */
	@Test
	public void evict() {
		
		this.cacheWriter.put(NAME, KEY, VALUE, TTL);
		this.cacheWriter.remove(NAME, KEY);
		
		assertThat(this.cacheWriter.get(NAME, KEY)).isNull();
	}
	
	/**
	 * {@link MockRedisCacheWriter#clean(String, byte[])}
	 */
	@Test
	public void clean() {
		
		this.cacheWriter.put(NAME, KEY, VALUE, TTL);
		this.cacheWriter.clean(NAME, null);
		
		assertThat(this.cacheWriter.get(NAME, KEY)).isNull();
	}
}
