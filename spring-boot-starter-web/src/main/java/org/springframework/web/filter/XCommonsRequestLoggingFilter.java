/*
 * Copyright 2017-2019 the original author or authors.
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

package org.springframework.web.filter;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.XLoggerWrapper;

/**
 * Custom {@link CommonsRequestLoggingFilter}
 */
public class XCommonsRequestLoggingFilter extends CommonsRequestLoggingFilter {
	
	/**
	 * {@link XLoggerWrapper}
	 */
	private final XLoggerWrapper logger;
	
	/**
	 * Constructor
	 */
	public XCommonsRequestLoggingFilter() {
		
		this(LoggerFactory.getLogger(CommonsRequestLoggingFilter.class));
	}
	
	/**
	 * Constructor
	 * 
	 * @param logger {@link Logger}
	 */
	public XCommonsRequestLoggingFilter(Logger logger) {
		
		this.logger = new XLoggerWrapper(logger);
	}
	
	@Override
	protected boolean shouldLog(HttpServletRequest request) {
		
		return this.logger.isEnabled();
	}
	
	@Override
	protected void beforeRequest(HttpServletRequest request, String message) {
		
		this.logger.log(message);
	}
	
	@Override
	protected void afterRequest(HttpServletRequest request, String message) {
		
		this.logger.log(message);
	}
}
