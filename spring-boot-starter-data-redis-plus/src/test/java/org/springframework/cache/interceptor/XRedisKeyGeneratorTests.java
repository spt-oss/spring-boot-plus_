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

package org.springframework.cache.interceptor;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;

import org.junit.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.XCacheParam;

/**
 * Tests for {@link XRedisKeyGenerator}
 */
public class XRedisKeyGeneratorTests {
	
	/**
	 * {@link XRedisKeyGenerator}
	 */
	private XRedisKeyGenerator keyGenerator = new XRedisKeyGenerator();
	
	/**
	 * {@link XRedisKeyGenerator#generate(Object, Method, Object...)}
	 */
	@Test
	public void generate() {
		
		TestService service = new TestService();
		
		{
			Method method = BeanUtils.findDeclaredMethodWithMinimalParameters(TestService.class, "withoutParameters");
			
			assertThat(this.keyGenerator.generate(service, method)).isEqualTo("");
		}
		
		{
			Method method = BeanUtils.findDeclaredMethodWithMinimalParameters(TestService.class, "withAnnotation");
			
			assertThat(this.keyGenerator.generate(service, method, "foo", 1, true)).isEqualTo("first:foo:third:true");
		}
		
		{
			Method method = BeanUtils.findDeclaredMethodWithMinimalParameters(TestService.class, "withoutAnnotation");
			
			assertThat(this.keyGenerator.generate(service, method, "bar", 1, true))
				.isEqualTo("first:bar:second:1:third:true");
		}
	}
	
	/**
	 * Test service
	 */
	protected static class TestService {
		
		/**
		 * Without parameters
		 * 
		 * @return {@link Object}
		 */
		public Object withoutParameters() {
			
			return new Object();
		}
		
		/**
		 * With annotation
		 * 
		 * @param first first
		 * @param second second
		 * @param third third
		 * @return {@link Object}
		 */
		public Object withAnnotation(
		/* @formatter:off */
			String first,
			@XCacheParam(required = false) int second,
			boolean third) {
			/* @formatter:on */
			
			return new Object();
		}
		
		/**
		 * Without annotation
		 * 
		 * @param first first
		 * @param second second
		 * @param third third
		 * @return {@link Object}
		 */
		public Object withoutAnnotation(
		/* @formatter:off */
			String first,
			int second,
			boolean third) {
			/* @formatter:on */
			
			return new Object();
		}
	}
}
