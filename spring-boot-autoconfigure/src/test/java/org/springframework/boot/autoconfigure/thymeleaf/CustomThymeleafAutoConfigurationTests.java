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

package org.springframework.boot.autoconfigure.thymeleaf;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.thymeleaf.CustomThymeleafAutoConfiguration.ThymeleafMinifierDialectConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.extras.minify.dialect.MinifierDialect;
import org.thymeleaf.extras.minify.engine.SimpleMinifierTemplateHandler;

/**
 * {@link Test}: {@link CustomThymeleafAutoConfiguration}
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CustomThymeleafAutoConfiguration.class)
@ActiveProfiles({ "test", "test-thymeleaf" })
public class CustomThymeleafAutoConfigurationTests {
	
	/**
	 * {@link MinifierDialect}
	 */
	@Autowired
	private MinifierDialect minifierDialect;
	
	/**
	 * {@link ThymeleafMinifierDialectConfiguration#minifierDialect()}
	 */
	@Test
	public void minifierDialect() {
		
		Class<?> clazz = this.minifierDialect.getPostProcessors().iterator().next().getHandlerClass();
		
		assertThat(clazz).isEqualTo(InternalHandler.class);
	}
	
	/**
	 * {@link SimpleMinifierTemplateHandler}: Internal
	 */
	public static class InternalHandler extends SimpleMinifierTemplateHandler {
		
		/* NOP */
	}
}
