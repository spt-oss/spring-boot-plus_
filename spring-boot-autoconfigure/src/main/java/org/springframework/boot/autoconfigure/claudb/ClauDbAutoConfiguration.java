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

package org.springframework.boot.autoconfigure.claudb;

import org.springframework.boot.autoconfigure.AbstractDependsOnBeanFactoryPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import com.github.tonivade.claudb.ClauDB;
import com.github.tonivade.resp.RespServer;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * ClauDB auto configuration
 */
@Configuration
@ConditionalOnClass(RespServer.class)
@ConditionalOnProperty(prefix = ClauDbProperties.PREFIX, name = "enabled", havingValue = "true")
@EnableConfigurationProperties(ClauDbProperties.class)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class ClauDbAutoConfiguration {
	
	/**
	 * {@link ClauDbProperties}
	 */
	@NonNull
	private final ClauDbProperties properties;
	
	/**
	 * {@link Bean}: {@link RespServer}
	 * 
	 * @return {@link RespServer}
	 */
	@Bean(initMethod = "start", destroyMethod = "stop")
	public RespServer respServer() {
		
		return ClauDB.builder().port(this.properties.determinePort()).build();
	}
	
	/**
	 * {@link AbstractDependsOnBeanFactoryPostProcessor}: {@link RedisConnectionFactory}
	 */
	@Configuration
	@ConditionalOnClass(RedisConnectionFactory.class)
	public static class DataSourceDependsOnBeanFactoryPostProcessor extends AbstractDependsOnBeanFactoryPostProcessor {
		
		/**
		 * Constructor
		 */
		public DataSourceDependsOnBeanFactoryPostProcessor() {
			
			super(RedisConnectionFactory.class, "clauDbServer");
		}
	}
}
