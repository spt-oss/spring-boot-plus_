
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
