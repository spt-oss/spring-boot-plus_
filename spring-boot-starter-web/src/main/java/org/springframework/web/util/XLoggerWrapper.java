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

package org.springframework.web.util;

import org.slf4j.Logger;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * {@link Logger} wrapper
 */
@RequiredArgsConstructor
public class XLoggerWrapper {
	
	/**
	 * {@link Logger}
	 */
	@NonNull
	private final Logger logger;
	
	/**
	 * Is enabled?
	 * 
	 * @return {@code true} if enabled
	 */
	public boolean isEnabled() {
		
		/* @formatter:off */
		return this.logger.isTraceEnabled()
			|| this.logger.isDebugEnabled()
			|| this.logger.isInfoEnabled()
			|| this.logger.isWarnEnabled()
			|| this.logger.isErrorEnabled();
		/* @formatter:on */
	}
	
	/**
	 * Log
	 * 
	 * @param message message
	 */
	public void log(String message) {
		
		if (this.logger.isTraceEnabled()) {
			
			this.logger.trace(message);
		}
		else if (this.logger.isDebugEnabled()) {
			
			this.logger.debug(message);
		}
		else if (this.logger.isInfoEnabled()) {
			
			this.logger.info(message);
		}
		else if (this.logger.isWarnEnabled()) {
			
			this.logger.warn(message);
		}
		else if (this.logger.isErrorEnabled()) {
			
			this.logger.error(message);
		}
	}
}
