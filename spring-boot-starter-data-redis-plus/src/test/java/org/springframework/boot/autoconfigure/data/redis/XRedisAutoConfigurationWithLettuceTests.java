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

package org.springframework.boot.autoconfigure.data.redis;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig; // TODO @checkstyle:ignore
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Tests for {@link XRedisAutoConfiguration} with Lettuce
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = XRedisAutoConfiguration.class)
@ActiveProfiles({ "test", "test-redis-config-with-lettuce" })
public class XRedisAutoConfigurationWithLettuceTests {
	
	/**
	 * {@link LettuceConnectionFactory}
	 */
	@Autowired
	private LettuceConnectionFactory redisConnectionFactory;
	
	/**
	 * {@link XLettuceConnectionConfiguration#redisConnectionFactory(io.lettuce.core.resource.ClientResources)}
	 */
	@Test
	public void redisConnectionFactory() {
		
		GenericObjectPoolConfig pool = ((LettucePoolingClientConfiguration) this.redisConnectionFactory
			.getClientConfiguration()).getPoolConfig();
		
		assertThat(pool.getMinEvictableIdleTimeMillis()).isEqualTo(30000L);
		assertThat(pool.getSoftMinEvictableIdleTimeMillis()).isEqualTo(0L);
		assertThat(pool.getNumTestsPerEvictionRun()).isEqualTo(4);
		assertThat(pool.getTestOnCreate()).isEqualTo(true);
		assertThat(pool.getTestOnBorrow()).isEqualTo(true);
		assertThat(pool.getTestOnReturn()).isEqualTo(true);
		assertThat(pool.getTestWhileIdle()).isEqualTo(true);
		assertThat(pool.getTimeBetweenEvictionRunsMillis()).isEqualTo(0L);
		assertThat(pool.getBlockWhenExhausted()).isEqualTo(false);
	}
}
