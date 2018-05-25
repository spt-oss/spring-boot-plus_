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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.github.tonivade.resp.RespServer;

import redis.clients.jedis.Jedis;

/**
 * {@link Test}: {@link ClauDbAutoConfiguration}
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ClauDbAutoConfiguration.class)
@ActiveProfiles({ "test", "test-claudb" })
public class ClauDbAutoConfigurationTests {
	
	/**
	 * {@link ClauDbProperties}
	 */
	@Autowired
	private ClauDbProperties clauDbProperties;
	
	/**
	 * {@link RespServer}
	 */
	@SuppressWarnings("unused")
	@Autowired
	private RespServer respServer;
	
	/**
	 * Annotation
	 */
	@Test
	public void annotation() {
		
		ConditionalOnProperty property = AnnotationUtils.findAnnotation(ClauDbAutoConfiguration.class,
			ConditionalOnProperty.class);
		
		assertThat(property.prefix()).isEqualTo(ClauDbProperties.PREFIX);
		assertThat(property.name()).isEqualTo(new String[] { "enabled" });
		assertThat(property.havingValue()).isEqualTo("true");
		assertThat(property.matchIfMissing()).isEqualTo(false);
	}
	
	/**
	 * {@link ClauDbProperties}
	 */
	@Test
	public void clauDbProperties() {
		
		{
			ClauDbProperties properties = new ClauDbProperties();
			
			assertThat(properties.getPort()).isEqualTo(0);
			assertThat(properties.determinePort()).isNotEqualTo(0);
		}
		
		{
			assertThat(this.clauDbProperties.determinePort()).isEqualTo(45678);
		}
	}
	
	/**
	 * {@link Jedis}
	 */
	@Test
	public void jedis() {
		
		byte[] key = "key".getBytes();
		byte[] value = "value".getBytes();
		
		try (Jedis jedis = new Jedis("localhost", this.clauDbProperties.determinePort())) {
			
			jedis.set(key, value);
			
			assertThat(jedis.get(key)).isEqualTo(value);
		}
	}
}
