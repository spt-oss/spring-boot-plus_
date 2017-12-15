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

import java.net.UnknownHostException;
import java.util.List;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig; // TODO @checkstyle:ignore
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.data.redis.XRedisProperties.XPool;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;

import io.lettuce.core.resource.ClientResources;
import lombok.NonNull;

/**
 * Custom {@link LettuceConnectionConfiguration}
 */
@Configuration
public class XLettuceConnectionConfiguration extends LettuceConnectionConfiguration {
	
	/**
	 * {@link XRedisProperties}
	 */
	private XRedisProperties redisProperties;
	
	/**
	 * Constructor
	 * 
	 * @param redisProperties {@link XRedisProperties}
	 * @param sentinelConfiguration {@link RedisSentinelConfiguration}
	 * @param clusterConfiguration {@link RedisClusterConfiguration}
	 * @param builderCustomizers {@link LettuceClientConfigurationBuilderCustomizer}
	 */
	public XLettuceConnectionConfiguration(@NonNull XRedisProperties redisProperties,
		ObjectProvider<RedisSentinelConfiguration> sentinelConfiguration,
		ObjectProvider<RedisClusterConfiguration> clusterConfiguration,
		ObjectProvider<List<LettuceClientConfigurationBuilderCustomizer>> builderCustomizers) {
		
		super(redisProperties, sentinelConfiguration, clusterConfiguration, builderCustomizers);
		
		this.redisProperties = redisProperties;
	}
	
	@Bean
	@Override
	public LettuceConnectionFactory redisConnectionFactory(ClientResources clientResources)
		throws UnknownHostException {
		
		LettuceConnectionFactory factory = super.redisConnectionFactory(clientResources);
		
		this.customizeClient(factory.getClientConfiguration());
		
		return factory;
	}
	
	/**
	 * Customize client
	 * 
	 * @param configuration {@link LettuceClientConfiguration}
	 */
	protected void customizeClient(@NonNull LettuceClientConfiguration configuration) {
		
		if (configuration instanceof LettucePoolingClientConfiguration) {
			
			this.customizePool(((LettucePoolingClientConfiguration) configuration).getPoolConfig(),
				this.redisProperties.getLettuce().getPool());
		}
	}
	
	/**
	 * Customize pool
	 * 
	 * @param poolConfig {@link GenericObjectPoolConfig}
	 * @param pool {@link XPool}
	 */
	protected void customizePool(@NonNull GenericObjectPoolConfig poolConfig, @NonNull XPool pool) {
		
		poolConfig.setMinEvictableIdleTimeMillis(pool.getMinEvictableIdleTime());
		poolConfig.setSoftMinEvictableIdleTimeMillis(pool.getSoftMinEvictableIdleTime());
		poolConfig.setNumTestsPerEvictionRun(pool.getNumTestsPerEvictionRun());
		poolConfig.setTestOnCreate(pool.isTestOnCreate());
		poolConfig.setTestOnBorrow(pool.isTestOnBorrow());
		poolConfig.setTestOnReturn(pool.isTestOnReturn());
		poolConfig.setTestWhileIdle(pool.isTestWhileIdle());
		poolConfig.setTimeBetweenEvictionRunsMillis(pool.getTimeBetweenEvictionRuns());
		poolConfig.setBlockWhenExhausted(pool.isBlockWhenExhausted());
	}
}
