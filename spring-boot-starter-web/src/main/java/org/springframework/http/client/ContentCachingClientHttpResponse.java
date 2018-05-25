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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpRetryException;

import org.springframework.util.FileCopyUtils;

/**
 * {@link CustomClientHttpResponse}: Content caching
 */
public class ContentCachingClientHttpResponse extends CustomClientHttpResponse {
	
	/**
	 * Body
	 */
	private byte[] body;
	
	/**
	 * Constructor
	 * 
	 * @param delegate {@link ClientHttpResponse}
	 */
	public ContentCachingClientHttpResponse(ClientHttpResponse delegate) {
		
		super(delegate);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.springframework.web.client.DefaultResponseErrorHandler
	 */
	@Override
	public InputStream getBody() throws IOException {
		
		if (this.body == null) {
			
			try (InputStream stream = super.getBody()) {
				
				this.body = FileCopyUtils.copyToByteArray(stream);
			}
			catch (HttpRetryException e) {
				
				// https://jira.spring.io/browse/SPR-9999
				this.body = String.format("(Failed to get body: %s)", e.getMessage()).getBytes();
			}
		}
		
		return new ByteArrayInputStream(this.body);
	}
}
