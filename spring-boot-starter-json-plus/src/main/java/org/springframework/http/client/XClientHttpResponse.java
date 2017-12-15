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
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse; // TODO @checkstyle:ignore
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StreamUtils;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Custom {@link ClientHttpResponse}
 */
@RequiredArgsConstructor
public class XClientHttpResponse implements ClientHttpResponse {
	
	/**
	 * Delegate
	 */
	@NonNull
	private ClientHttpResponse delegate;
	
	/**
	 * Get status line
	 * 
	 * @return status line
	 * @throws IOException if failed to get
	 */
	public String getStatusLine() throws IOException {
		
		// Simple
		if (this.delegate instanceof SimpleClientHttpResponse) {
			
			Field field = ReflectionUtils.findField(SimpleClientHttpResponse.class, "connection");
			
			Assert.notNull(field, "Field 'connection' not found");
			
			ReflectionUtils.makeAccessible(field);
			
			return ((HttpURLConnection) ReflectionUtils.getField(field, this.delegate)).getHeaderField(0);
		}
		
		// Apache HttpComponents
		else if (this.delegate instanceof HttpComponentsClientHttpResponse) {
			
			Field field = ReflectionUtils.findField(HttpComponentsClientHttpResponse.class, "httpResponse");
			
			Assert.notNull(field, "Field 'httpResponse' not found");
			
			ReflectionUtils.makeAccessible(field);
			
			return ((HttpResponse) ReflectionUtils.getField(field, this.delegate)).getStatusLine().toString();
		}
		
		throw new IOException("Unsupported client: " + this.delegate.getClass());
	}
	
	/**
	 * Get HTTP version
	 * 
	 * @return HTTP version
	 * @throws IOException if failed to get
	 */
	public String getHttpVersion() throws IOException {
		
		String statusLine = this.getStatusLine();
		
		Matcher matcher = Pattern.compile("(HTTP/[0-9.]+)").matcher(statusLine);
		
		if (matcher.find()) {
			
			return matcher.group(1);
		}
		
		throw new IOException("HTTP version not found: " + statusLine);
	}
	
	@Override
	public HttpStatus getStatusCode() throws IOException {
		
		return this.delegate.getStatusCode();
	}
	
	@Override
	public int getRawStatusCode() throws IOException {
		
		return this.delegate.getRawStatusCode();
	}
	
	@Override
	public String getStatusText() throws IOException {
		
		return this.delegate.getStatusText();
	}
	
	@Override
	public HttpHeaders getHeaders() {
		
		return this.delegate.getHeaders();
	}
	
	@Override
	public InputStream getBody() throws IOException {
		
		return this.delegate.getBody();
	}
	
	/**
	 * {@link ClientHttpResponse#getBody()} as string
	 * 
	 * @return body
	 * @throws IOException if failed to get
	 */
	public String getBodyAsString() throws IOException {
		
		return this.getBodyAsString(StandardCharsets.UTF_8);
	}
	
	/**
	 * {@link ClientHttpResponse#getBody()} as string
	 * 
	 * @param charset {@link Charset}
	 * @return body
	 * @throws IOException if failed to get
	 */
	public String getBodyAsString(Charset charset) throws IOException {
		
		return StreamUtils.copyToString(this.getBody(), charset);
	}
	
	@Override
	public void close() {
		
		this.delegate.close();
	}
}
