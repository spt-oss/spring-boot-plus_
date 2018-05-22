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
package org.springframework.http.client;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * {@link ClientHttpRequestInterceptor} for logging
 */
@Accessors(fluent = true)
public class XLoggingInterceptor implements ClientHttpRequestInterceptor {
	
	/**
	 * Default log start separator
	 */
	private static final String DEFAULT_LOG_START_SEPARATOR = "-->";
	
	/**
	 * Default log end separator
	 */
	private static final String DEFAULT_LOG_END_SEPARATOR = "<--";
	
	/**
	 * Log line joiner
	 */
	private static final String LOG_LINE_JOINER = " ";
	
	/**
	 * Log header joiner
	 */
	private static final String LOG_HEADER_JOINER = ": ";
	
	/**
	 * {@link Logger}
	 */
	private Logger logger;
	
	/**
	 * Show body
	 */
	@Setter
	private boolean showBody;
	
	/**
	 * Default body charset
	 */
	@Setter
	@NonNull
	private Charset defaultBodyCharset = StandardCharsets.UTF_8;
	
	/**
	 * Log start separator
	 */
	@Setter
	@NonNull
	private String logStartSeparator = DEFAULT_LOG_START_SEPARATOR;
	
	/**
	 * Log end separator
	 */
	@Setter
	@NonNull
	private String logEndSeparator = DEFAULT_LOG_END_SEPARATOR;
	
	/**
	 * Constructor
	 */
	public XLoggingInterceptor() {
		
		this(LoggerFactory.getLogger(RestTemplate.class));
	}
	
	/**
	 * Constructor
	 * 
	 * @param logger {@link Logger}
	 */
	public XLoggingInterceptor(@NonNull Logger logger) {
		
		this.logger = logger;
	}
	
	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, @NonNull ClientHttpRequestExecution execution)
		throws IOException {
		
		if (this.isLogEnabled()) {
			
			this.logRequest(request, body);
		}
		
		ClientHttpResponse response = execution.execute(request, body);
		
		if (this.isLogEnabled()) {
			
			if (!XBodyCachingClientHttpResponse.class.isInstance(response)) {
				
				response = new XBodyCachingClientHttpResponse(response);
			}
			
			this.logResponse((XBodyCachingClientHttpResponse) response);
		}
		
		return response;
	}
	
	/**
	 * Is log enabled?
	 * 
	 * @return {@code true} if enabled
	 */
	protected boolean isLogEnabled() {
		
		/* @formatter:off */
		return this.logger.isTraceEnabled()
			|| this.logger.isDebugEnabled()
			|| this.logger.isInfoEnabled()
			|| this.logger.isWarnEnabled()
			|| this.logger.isErrorEnabled();
		/* @formatter:on */
	}
	
	/**
	 * Log request
	 * 
	 * @param request {@link HttpRequest}
	 * @param body body
	 */
	protected synchronized void logRequest(@NonNull HttpRequest request, @NonNull byte[] body) {
		
		if (StringUtils.hasText(this.logStartSeparator)) {
			
			this.logLine(this.logStartSeparator);
		}
		
		// Request Line
		this.logLine(String.join(LOG_LINE_JOINER, request.getMethod().name(), request.getURI().toString()));
		
		// Headers
		for (Entry<String, List<String>> entry : request.getHeaders().entrySet()) {
			
			for (String value : entry.getValue()) {
				
				this.logLine(String.join(LOG_HEADER_JOINER, entry.getKey(), value));
			}
		}
		
		// Message Body
		if (this.showBody && body.length > 0) {
			
			this.logLine("");
			this.logLine(new String(body, this.defaultBodyCharset));
		}
		
		if (StringUtils.hasText(this.logEndSeparator)) {
			
			this.logLine(this.logEndSeparator);
		}
	}
	
	/**
	 * Log response
	 * 
	 * @param response {@link XBodyCachingClientHttpResponse}
	 * @throws IOException if failed to get status
	 */
	protected synchronized void logResponse(@NonNull XBodyCachingClientHttpResponse response) throws IOException {
		
		if (StringUtils.hasText(this.logStartSeparator)) {
			
			this.logLine(this.logStartSeparator);
		}
		
		// Status Line
		this.logLine(response.getStatusLine());
		
		// Headers
		for (Entry<String, List<String>> entry : response.getHeaders().entrySet()) {
			
			for (String value : entry.getValue()) {
				
				this.logLine(String.join(LOG_HEADER_JOINER, entry.getKey(), value));
			}
		}
		
		// Message Body
		MediaType mediaType = response.getHeaders().getContentType();
		
		// TODO mediatype is viewable
		if (this.showBody && mediaType != null) {
			
			byte[] body = FileCopyUtils.copyToByteArray(response.getBody());
			
			if (body.length > 0) {
				
				Charset charset = mediaType.getCharset();
				
				if (charset == null) {
					
					charset = this.defaultBodyCharset;
				}
				
				this.logLine("");
				this.logLine(new String(body, charset));
			}
		}
		
		if (StringUtils.hasText(this.logEndSeparator)) {
			
			this.logLine(this.logEndSeparator);
		}
	}
	
	/**
	 * Log line
	 * 
	 * @param message message
	 */
	protected void logLine(String message) {
		
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
