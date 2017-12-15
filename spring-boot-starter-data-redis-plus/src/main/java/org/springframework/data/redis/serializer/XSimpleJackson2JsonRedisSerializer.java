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

package org.springframework.data.redis.serializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;

import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Simple {@link Jackson2JsonRedisSerializer}
 */
@Accessors(chain = true)
public class XSimpleJackson2JsonRedisSerializer implements RedisSerializer<Object> {
	
	/**
	 * {@link Logger}
	 */
	private static final Logger logger = LoggerFactory.getLogger(XSimpleJackson2JsonRedisSerializer.class);
	
	/**
	 * Delegate
	 */
	private Jackson2JsonRedisSerializer<?> delegate = new Jackson2JsonRedisSerializer<>(Object.class);
	
	/**
	 * Ignore deserialization error
	 */
	@Setter
	private boolean ignoreDeserializationError = true;
	
	/**
	 * Constructor
	 */
	public XSimpleJackson2JsonRedisSerializer() {
		
		this(new ObjectMapper());
	}
	
	/**
	 * Constructor
	 * 
	 * @param sourceObjectMapper source {@link ObjectMapper}
	 */
	public XSimpleJackson2JsonRedisSerializer(@NonNull ObjectMapper sourceObjectMapper) {
		
		this.delegate.setObjectMapper(sourceObjectMapper.copy()
		/* @formatter:off */
			.enableDefaultTyping(DefaultTyping.NON_FINAL, JsonTypeInfo.As.WRAPPER_OBJECT)
			/* @formatter:on */
		);
	}
	
	@Override
	public byte[] serialize(Object data) throws SerializationException {
		
		return this.delegate.serialize(data);
	}
	
	@Override
	public Object deserialize(byte[] bytes) throws SerializationException {
		
		try {
			
			return this.delegate.deserialize(bytes);
		}
		catch (SerializationException e) {
			
			if (this.ignoreDeserializationError) {
				
				logger.warn("Failed to deserialize", e);
				
				return null;
			}
			
			throw e;
		}
	}
}
