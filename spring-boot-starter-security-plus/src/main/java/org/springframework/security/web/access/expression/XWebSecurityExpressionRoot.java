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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.springframework.security.web.util.matcher.IpAddressMatcher;
import org.springframework.util.StringUtils;

/**
 * Custom {@link WebSecurityExpressionRoot}
 */
public class XWebSecurityExpressionRoot {
	
	/**
	 * X-Forwarded-For
	 */
	public static final String X_FORWARDED_FOR = "X-Forwarded-For";
	
	/**
	 * Has {@link #X_FORWARDED_FOR}?
	 * 
	 * @param request {@link HttpServletRequest}
	 * @param ipAddress IP address
	 * @return {@code true} if has
	 */
	public boolean hasXForwardedFor(HttpServletRequest request, String ipAddress) {
		
		return new IpAddressMatcher(ipAddress).matches(new ProxyHttpServletRequest(request));
	}
	
	/**
	 * Has no {@link #X_FORWARDED_FOR}?
	 * 
	 * @param request {@link HttpServletRequest}
	 * @return {@code true} if has
	 */
	public boolean hasNoXForwardedFor(HttpServletRequest request) {
		
		return !StringUtils.hasText(new ProxyHttpServletRequest(request).getRemoteAddr());
	}
	
	/**
	 * Proxy {@link HttpServletRequest}
	 */
	protected static class ProxyHttpServletRequest extends HttpServletRequestWrapper {
		
		/**
		 * Constructor
		 * 
		 * @param request {@link HttpServletRequest}
		 */
		protected ProxyHttpServletRequest(HttpServletRequest request) {
			
			super(request);
		}
		
		@Override
		public String getRemoteAddr() {
			
			return this.getHeader(X_FORWARDED_FOR);
		}
	}
}
