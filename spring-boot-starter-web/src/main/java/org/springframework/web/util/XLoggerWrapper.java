
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
