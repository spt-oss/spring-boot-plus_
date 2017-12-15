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

package org.springframework.security.config.annotation.web;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.WebSecurityConfigurerTests.ExampleConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.expression.XWebSecurityExpressionRoot;
import org.springframework.security.web.header.writers.DelegatingRequestMatcherHeaderWriter;
import org.springframework.security.web.header.writers.XFirstMatchRequestMatcherHeaderWriter;
import org.springframework.security.web.header.writers.XRobotsTagHeaderWriter;
import org.springframework.security.web.header.writers.XRobotsTagHeaderWriter.XRobotsTagMode;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;
import org.springframework.test.context.junit4.SpringRunner;

import lombok.NonNull;

/**
 * Tests for {@link WebSecurityConfigurer}
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ExampleConfiguration.class)
public class WebSecurityConfigurerTests {
	
	/**
	 * {@link ExampleConfiguration}
	 */
	@Autowired
	private ExampleConfiguration configuration;
	
	/**
	 * {@link ExampleConfiguration}
	 */
	@Test
	public void testConfiguration() {
		
		// TODO
		assertThat(this.configuration).isNotNull();
	}
	
	/**
	 * Example configuration
	 */
	@Configuration
	@EnableWebSecurity
	protected static class ExampleConfiguration extends WebSecurityConfigurerAdapter {
		
		@Override
		protected void configure(@NonNull HttpSecurity http) throws Exception {
			
			/* @formatter:off */
			http
				.authorizeRequests()
					.antMatchers("/foo/**")
						.access(
							  "(hasIpAddress('::1') and @webSecurityExpression.hasNoXForwardedFor(request))"
							+ " or "
							+ "@webSecurityExpression.hasXForwardedFor(request, '10.0.0.0/8')"
						)
					.anyRequest()
						.permitAll()
					.and()
				.headers()
					.addHeaderWriter(
						new XFirstMatchRequestMatcherHeaderWriter(
							new DelegatingRequestMatcherHeaderWriter(
								new AntPathRequestMatcher("/banner/**"),
								new XRobotsTagHeaderWriter(XRobotsTagMode.NOINDEX, XRobotsTagMode.NOFOLLOW)
							),
							new DelegatingRequestMatcherHeaderWriter(
								AnyRequestMatcher.INSTANCE,
								new XRobotsTagHeaderWriter(XRobotsTagMode.NOARCHIVE, XRobotsTagMode.NOTRANSLATE)
							)
						)
					);
			/* @formatter:on */
		}
		
		/**
		 * {@link XWebSecurityExpressionRoot}
		 * 
		 * @return {@link XWebSecurityExpressionRoot}
		 */
		@Bean
		public XWebSecurityExpressionRoot webSecurityExpression() {
			
			return new XWebSecurityExpressionRoot();
		}
	}
}
