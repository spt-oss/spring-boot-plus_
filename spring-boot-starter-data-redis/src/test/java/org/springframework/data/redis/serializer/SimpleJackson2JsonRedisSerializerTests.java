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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * {@link Test}: {@link SimpleJackson2JsonRedisSerializer}
 */
public class SimpleJackson2JsonRedisSerializerTests {
	
	/**
	 * Example date
	 */
	private static final String EXAMPLE_DATE = "2001-01-01T01:01:01.001Z";
	
	/**
	 * {@link SimpleJackson2JsonRedisSerializer}
	 */
	private SimpleJackson2JsonRedisSerializer serializer;
	
	/**
	 * {@link Before}
	 */
	@Before
	public void before() {
		
		SimpleJackson2JsonRedisSerializer serializer = new SimpleJackson2JsonRedisSerializer(new ObjectMapper()
		/* @formatter:off */
			.registerModules(
				new JavaTimeModule().addSerializer(new ZonedDateTimeSerializer(DateTimeFormatter.ISO_INSTANT)),
				new ParameterNamesModule()
			)
			.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
			/* @formatter:on */
		);
		
		serializer.setIgnoreDeserializationError(true);
		
		this.serializer = serializer;
	}
	
	/**
	 * {@link SimpleJackson2JsonRedisSerializer#setIgnoreDeserializationError(boolean)}
	 */
	@Test
	public void setIgnoreDeserializationError() {
		
		XExample entity = new XExample(1, ZonedDateTime.parse(EXAMPLE_DATE));
		
		// {"(class)":{"foo":XXXXX,"bar":XXXXX}} -> {"baz":{"foo":XXXXX,"bar":XXXXX}}
		String invalidJson1 = this.serialize(entity).replaceAll("^\\{\"(.*?)\":", "{\"baz\":");
		
		// {"(class)":{"foo":XXXXX,"bar":XXXXX}} -> {"foo":XXXXX,"bar":XXXXX}
		String invalidJson2 = this.serialize(entity).replaceAll("^\\{\"(.*?)\":\\{", "{").replaceAll("\\}\\}$", "}");
		
		this.serializer.setIgnoreDeserializationError(false);
		
		try {
			
			this.deserialize(invalidJson1);
			
			fail();
		}
		catch (SerializationException e) {
			
			/* NOP */
		}
		
		try {
			
			this.deserialize(invalidJson2);
			
			fail();
		}
		catch (SerializationException e) {
			
			/* NOP */
		}
		
		this.serializer.setIgnoreDeserializationError(true);
		
		assertThat(this.deserialize(invalidJson1)).isNull();
		assertThat(this.deserialize(invalidJson2)).isNull();
	}
	
	/**
	 * {@link SimpleJackson2JsonRedisSerializer#serialize(Object)}
	 */
	@Test
	public void serialize() {
		
		XExample entity = new XExample(1, ZonedDateTime.parse(EXAMPLE_DATE));
		
		String json = "{\"" + XExample.class.getName() + "\"" + ":{\"foo\":1,\"bar\":\"" + EXAMPLE_DATE + "\"}}";
		
		assertThat(this.serialize(entity)).isEqualTo(json);
		assertThat(this.serialize(Arrays.asList(entity, entity)))
			.isEqualTo("{\"java.util.Arrays$ArrayList\":[" + json + "," + json + "]}");
	}
	
	/**
	 * Serialize
	 * 
	 * @param object object
	 * @return JSON string
	 */
	private String serialize(Object object) {
		
		return new String(this.serializer.serialize(object));
	}
	
	/**
	 * {@link SimpleJackson2JsonRedisSerializer#deserialize(byte[])}
	 */
	@Test
	public void deserialize() {
		
		String string = SimpleJackson2JsonRedisSerializerTests.class.getSimpleName() + "."
			+ XExample.class.getSimpleName() + "(foo=1, bar=" + EXAMPLE_DATE + "[UTC])";
		
		XExample entity = new XExample(1, ZonedDateTime.parse(EXAMPLE_DATE));
		
		assertThat(this.deserialize(this.serialize(entity)).toString()).isEqualTo(string);
		assertThat(this.deserialize(this.serialize(Arrays.asList(entity, entity))).toString()).isEqualTo(
		/* @formatter:off */
			"[" + string + ", " + string + "]"
			/* @formatter:on */
		);
	}
	
	/**
	 * Deserialize
	 * 
	 * @param json JSON string
	 * @return object
	 */
	private Object deserialize(@NonNull String json) {
		
		return this.serializer.deserialize(json.getBytes());
	}
	
	/**
	 * Example
	 */
	@RequiredArgsConstructor
	@Data
	protected static class XExample {
		
		/**
		 * Foo
		 */
		private final int foo;
		
		/**
		 * Bar
		 */
		private final ZonedDateTime bar;
	}
}
