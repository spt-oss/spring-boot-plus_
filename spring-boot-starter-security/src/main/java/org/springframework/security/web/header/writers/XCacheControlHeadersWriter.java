
package org.springframework.security.web.header.writers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.CacheControl;
import org.springframework.security.web.header.HeaderWriter;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Custom {@link CacheControlHeadersWriter}
 */
@RequiredArgsConstructor
public class XCacheControlHeadersWriter implements HeaderWriter {
	
	/**
	 * Cache-Control header name
	 */
	public static final String CACHE_CONTROL = "Cache-Control";
	
	/**
	 * CacheControl
	 */
	@NonNull
	private final CacheControl cacheControl;
	
	@Override
	public void writeHeaders(HttpServletRequest request, @NonNull HttpServletResponse response) {
		
		response.setHeader(CACHE_CONTROL, this.cacheControl.getHeaderValue());
	}
}
