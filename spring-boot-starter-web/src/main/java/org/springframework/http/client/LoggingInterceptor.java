/*
 * Copyright 2017-2018 the original author or authors.
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

package org.springframework.http.client;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * {@link ClientHttpRequestInterceptor}: Logging
 */
public class LoggingInterceptor implements ClientHttpRequestInterceptor {
	
	/**
	 * Default log start separator
	 */
	private static final String DEFAULT_LOG_START_SEPARATOR = "-->";
	
	/**
	 * Default log end separator
	 */
	private static final String DEFAULT_LOG_END_SEPARATOR = "<--";
	
	/**
	 * {@link InternalLogger}
	 */
	@NonNull
	private final InternalLogger logger;
	
	/**
	 * Show body
	 */
	@Setter
	@Accessors(chain = true)
	private boolean showBody;
	
	/**
	 * Default body charset
	 */
	@Setter
	@Accessors(chain = true)
	@NonNull
	private Charset defaultBodyCharset = StandardCharsets.UTF_8;
	
	/**
	 * Log start separator
	 */
	@Setter
	@Accessors(chain = true)
	@NonNull
	private String logStartSeparator = DEFAULT_LOG_START_SEPARATOR;
	
	/**
	 * Log end separator
	 */
	@Setter
	@Accessors(chain = true)
	@NonNull
	private String logEndSeparator = DEFAULT_LOG_END_SEPARATOR;
	
	/**
	 * Constructor
	 * 
	 * @param logger {@link Logger}
	 */
	public LoggingInterceptor(Logger logger) {
		
		this.logger = new InternalLogger(logger);
	}
	
	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, @NonNull ClientHttpRequestExecution execution)
		throws IOException {
		
		if (this.logger.isEnabled()) {
			
			this.logRequest(request, body);
		}
		
		ClientHttpResponse response = execution.execute(request, body);
		
		if (this.logger.isEnabled()) {
			
			if (!ContentCachingClientHttpResponse.class.isInstance(response)) {
				
				response = new ContentCachingClientHttpResponse(response);
			}
			
			this.logResponse((ContentCachingClientHttpResponse) response);
		}
		
		return response;
	}
	
	/**
	 * Log request
	 * 
	 * @param request {@link HttpRequest}
	 * @param body body
	 */
	protected synchronized void logRequest(@NonNull HttpRequest request, @NonNull byte[] body) {
		
		if (StringUtils.hasText(this.logStartSeparator)) {
			
			this.logger.log(this.logStartSeparator);
		}
		
		// Request Line
		this.logger.log(String.join(" ", request.getMethod().name(), request.getURI().toString()));
		
		// Headers
		for (Entry<String, List<String>> entry : request.getHeaders().entrySet()) {
			
			for (String value : entry.getValue()) {
				
				this.logger.log(String.join(": ", entry.getKey(), value)); // TODO @checkstyle:ignore
			}
		}
		
		// Message Body
		if (this.showBody && body.length > 0) {
			
			this.logger.log("");
			this.logger.log(new String(body, this.defaultBodyCharset));
		}
		
		if (StringUtils.hasText(this.logEndSeparator)) {
			
			this.logger.log(this.logEndSeparator);
		}
	}
	
	/**
	 * Log response
	 * 
	 * @param response {@link ContentCachingClientHttpResponse}
	 * @throws IOException if failed to get status
	 */
	protected synchronized void logResponse(@NonNull ContentCachingClientHttpResponse response) throws IOException {
		
		if (StringUtils.hasText(this.logStartSeparator)) {
			
			this.logger.log(this.logStartSeparator);
		}
		
		// Status Line
		this.logger.log(response.getStatusLine());
		
		// Headers
		for (Entry<String, List<String>> entry : response.getHeaders().entrySet()) {
			
			for (String value : entry.getValue()) {
				
				this.logger.log(String.join(": ", entry.getKey(), value));
			}
		}
		
		// Message Body
		// TODO mediatype filter
		MediaType mediaType = response.getHeaders().getContentType();
		
		if (this.showBody && mediaType != null) {
			
			byte[] body = FileCopyUtils.copyToByteArray(response.getBody());
			
			if (body.length > 0) {
				
				Charset charset = mediaType.getCharset();
				
				if (charset == null) {
					
					charset = this.defaultBodyCharset;
				}
				
				this.logger.log("");
				this.logger.log(new String(body, charset));
			}
		}
		
		if (StringUtils.hasText(this.logEndSeparator)) {
			
			this.logger.log(this.logEndSeparator);
		}
	}
	
	/**
	 * Internal {@link Logger}
	 */
	@RequiredArgsConstructor
	protected static class InternalLogger {
		
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
}
