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

/**
 * Tests for {@link XRobotsTagHeaderWriter}
 */
public class XRobotsTagHeaderWriterTests {
	
	/**
	 * {@link XRobotsTagHeaderWriter#writeHeaders(HttpServletRequest, HttpServletResponse)}
	 */
	@Test
	public void writeHeaders() {
		
		{
			MockHttpServletRequest request = new MockHttpServletRequest();
			MockHttpServletResponse response = new MockHttpServletResponse();
			
			XRobotsTagHeaderWriter writer = new XRobotsTagHeaderWriter(XRobotsTagMode.NONE);
			writer.writeHeaders(request, response);
			
			assertThat(response.getHeader(XRobotsTagHeaderWriter.X_ROBOTS_TAG_HEADER))
				.isEqualTo(XRobotsTagMode.NONE.getValue());
		}
		
		{
			MockHttpServletRequest request = new MockHttpServletRequest();
			MockHttpServletResponse response = new MockHttpServletResponse();
			
			XRobotsTagHeaderWriter writer = new XRobotsTagHeaderWriter(XRobotsTagMode.NOINDEX, XRobotsTagMode.NOFOLLOW);
			writer.writeHeaders(request, response);
			
			assertThat(response.getHeader(XRobotsTagHeaderWriter.X_ROBOTS_TAG_HEADER))
				.isEqualTo(String.join(", ", XRobotsTagMode.NOINDEX.getValue(), XRobotsTagMode.NOFOLLOW.getValue()));
		}
	}
}
