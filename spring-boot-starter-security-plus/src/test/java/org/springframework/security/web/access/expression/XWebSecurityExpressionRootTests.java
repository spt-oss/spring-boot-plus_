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

package org.springframework.security.web.access.expression;

import static org.assertj.core.api.Assertions.assertThat;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

/**
 * Tests for {@link XWebSecurityExpressionRoot}
 */
public class XWebSecurityExpressionRootTests {
	
	/**
	 * 10.0.0.0/8
	 */
	private static final String IP_10_0_0_0_S_8 = "10.0.0.0/8";
	
	/**
	 * {@link XWebSecurityExpressionRoot}
	 */
	private XWebSecurityExpressionRoot expression = new XWebSecurityExpressionRoot();
	
	/**
	 * {@link XWebSecurityExpressionRoot#hasXForwardedFor(HttpServletRequest, String)}
	 */
	@Test
	public void hasXForwardedFor() {
		
		{
			MockHttpServletRequest request = new MockHttpServletRequest();
			
			assertThat(this.expression.hasXForwardedFor(request, IP_10_0_0_0_S_8)).isEqualTo(false);
		}
		
		{
			MockHttpServletRequest request = new MockHttpServletRequest();
			request.addHeader(XWebSecurityExpressionRoot.X_FORWARDED_FOR, "10.1.2.4");
			
			assertThat(this.expression.hasXForwardedFor(request, IP_10_0_0_0_S_8)).isEqualTo(true);
			assertThat(this.expression.hasXForwardedFor(request, "10.1.2.0/24")).isEqualTo(true);
			assertThat(this.expression.hasXForwardedFor(request, "10.1.2.0/30")).isEqualTo(false);
		}
	}
	
	/**
	 * {@link XWebSecurityExpressionRoot#hasNoXForwardedFor(HttpServletRequest)}
	 */
	@Test
	public void hasNoXForwardedFor() {
		
		{
			MockHttpServletRequest request = new MockHttpServletRequest();
			
			assertThat(this.expression.hasNoXForwardedFor(request)).isEqualTo(true);
		}
		
		{
			MockHttpServletRequest request = new MockHttpServletRequest();
			request.addHeader(XWebSecurityExpressionRoot.X_FORWARDED_FOR, "foo");
			
			assertThat(this.expression.hasNoXForwardedFor(request)).isEqualTo(false);
		}
	}
}
