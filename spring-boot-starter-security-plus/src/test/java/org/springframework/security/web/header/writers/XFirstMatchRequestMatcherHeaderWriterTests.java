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

package org.springframework.security.web.header.writers;

import static org.assertj.core.api.Assertions.assertThat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.web.header.writers.XRobotsTagHeaderWriter.XRobotsTagMode;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;

/**
 * Tests for {@link XFirstMatchRequestMatcherHeaderWriter}
 */
public class XFirstMatchRequestMatcherHeaderWriterTests {
	
	/**
	 * {@link XFirstMatchRequestMatcherHeaderWriter#writeHeaders(HttpServletRequest, HttpServletResponse)}
	 */
	@Test
	public void writeHeaders() {
		
		XFirstMatchRequestMatcherHeaderWriter writer = new XFirstMatchRequestMatcherHeaderWriter(
			/* @formatter:off */
			new DelegatingRequestMatcherHeaderWriter(
				new AntPathRequestMatcher("/banner/**"),
				new XRobotsTagHeaderWriter(XRobotsTagMode.NOINDEX)
			),
			new DelegatingRequestMatcherHeaderWriter(
				AnyRequestMatcher.INSTANCE,
				new XRobotsTagHeaderWriter(XRobotsTagMode.ALL)
			)
			/* @formatter:on */
		);
		
		{
			MockHttpServletRequest request = new MockHttpServletRequest();
			request.setPathInfo("/banner");
			
			MockHttpServletResponse response = new MockHttpServletResponse();
			
			writer.writeHeaders(request, response);
			
			assertThat(response.getHeader(XRobotsTagHeaderWriter.X_ROBOTS_TAG_HEADER))
				.isEqualTo(XRobotsTagMode.NOINDEX.getValue());
		}
		
		{
			MockHttpServletRequest request = new MockHttpServletRequest();
			request.setPathInfo("/banner/");
			
			MockHttpServletResponse response = new MockHttpServletResponse();
			
			writer.writeHeaders(request, response);
			
			assertThat(response.getHeader(XRobotsTagHeaderWriter.X_ROBOTS_TAG_HEADER))
				.isEqualTo(XRobotsTagMode.NOINDEX.getValue());
		}
		
		{
			MockHttpServletRequest request = new MockHttpServletRequest();
			request.setPathInfo("/");
			
			MockHttpServletResponse response = new MockHttpServletResponse();
			
			writer.writeHeaders(request, response);
			
			assertThat(response.getHeader(XRobotsTagHeaderWriter.X_ROBOTS_TAG_HEADER))
				.isEqualTo(XRobotsTagMode.ALL.getValue());
		}
		
		{
			MockHttpServletRequest request = new MockHttpServletRequest();
			request.setPathInfo("/bannerfoo");
			
			MockHttpServletResponse response = new MockHttpServletResponse();
			
			writer.writeHeaders(request, response);
			
			assertThat(response.getHeader(XRobotsTagHeaderWriter.X_ROBOTS_TAG_HEADER))
				.isEqualTo(XRobotsTagMode.ALL.getValue());
		}
	}
}
