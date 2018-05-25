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

package org.springframework.boot.autoconfigure.data.redis;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Custom {@link RedisProperties}
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CustomRedisProperties extends RedisProperties {
	
	/**
	 * {@link XLettuce}
	 */
	private XLettuce lettuce = new XLettuce();
	
	/**
	 * Custom {@link org.springframework.boot.autoconfigure.data.redis.RedisProperties.Pool}
	 */
	@Data
	@EqualsAndHashCode(callSuper = true)
	@ToString(callSuper = true)
	public static class XPool extends Pool {
		
		/**
		 * Minimum evictable idle time
		 */
		private long minEvictableIdleTime = 1000L * 60L * 30L;
		
		/**
		 * Soft minimum evictable idle time
		 */
		private long softMinEvictableIdleTime = -1;
		
		/**
		 * Number of tests per eviction run
		 */
		private int numTestsPerEvictionRun = 3;
		
		/**
		 * Test on create
		 */
		private boolean testOnCreate;
		
		/**
		 * Test on borrow
		 */
		private boolean testOnBorrow;
		
		/**
		 * Test on return
		 */
		private boolean testOnReturn;
		
		/**
		 * Test while idle
		 */
		private boolean testWhileIdle;
		
		/**
		 * Time between eviction runs
		 */
		private long timeBetweenEvictionRuns = -1;
		
		/**
		 * Block when exhausted
		 */
		private boolean blockWhenExhausted = true;
	}
	
	/**
	 * Custom {@link org.springframework.boot.autoconfigure.data.redis.RedisProperties.Lettuce}
	 */
	@Data
	@EqualsAndHashCode(callSuper = true)
	@ToString(callSuper = true)
	public static class XLettuce extends Lettuce {
		
		/**
		 * {@link XPool}
		 */
		private XPool pool;
	}
}
