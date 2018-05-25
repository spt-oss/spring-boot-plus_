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
public class SimpleJackson2JsonRedisSerializer implements RedisSerializer<Object> {
	
	/**
	 * {@link Logger}
	 */
	private static final Logger logger = LoggerFactory.getLogger(SimpleJackson2JsonRedisSerializer.class);
	
	/**
	 * Delegate
	 */
	private final Jackson2JsonRedisSerializer<?> delegate;
	
	/**
	 * Ignore deserialization error
	 */
	@Setter
	@Accessors(chain = true)
	private boolean ignoreDeserializationError = true;
	
	/**
	 * Constructor
	 */
	public SimpleJackson2JsonRedisSerializer() {
		
		this(new ObjectMapper());
	}
	
	/**
	 * Constructor
	 * 
	 * @param source source {@link ObjectMapper}
	 */
	public SimpleJackson2JsonRedisSerializer(@NonNull ObjectMapper source) {
		
		Jackson2JsonRedisSerializer<?> delegate = new Jackson2JsonRedisSerializer<>(Object.class);
		
		delegate.setObjectMapper(
			source.copy().enableDefaultTyping(DefaultTyping.NON_FINAL, JsonTypeInfo.As.WRAPPER_OBJECT));
		
		this.delegate = delegate;
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
