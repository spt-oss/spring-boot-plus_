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

package org.springframework.web.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

/**
 * Custom {@link RestOperations}
 */
public interface XRestOperations extends RestOperations {
	
	/**
	 * {@link #getForObject(String, Class, Object...)} with {@link ParameterizedTypeReference}
	 * 
	 * @param url URL
	 * @param responseType {@link ParameterizedTypeReference}
	 * @param uriVariables uri variables
	 * @param <T> object type
	 * @return object
	 * @throws RestClientException if failed to get
	 */
	default <T> T getForObject(String url, ParameterizedTypeReference<T> responseType, Object... uriVariables)
		throws RestClientException {
		
		return this.exchange(url, HttpMethod.GET, null, responseType, uriVariables).getBody();
	}
	
	/**
	 * {@link #getForObject(String, Class, Object...)} with {@link HttpHeaders}
	 * 
	 * @param url URL
	 * @param headers {@link HttpHeaders}
	 * @param responseType response type
	 * @param uriVariables uri variables
	 * @param <T> object type
	 * @return object
	 * @throws RestClientException if failed to post
	 */
	default <T> T getForObject(String url, HttpHeaders headers, Class<T> responseType, Object... uriVariables)
		throws RestClientException {
		
		return this.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), responseType, uriVariables).getBody();
	}
	
	/**
	 * {@link #postForObject(String, Object, Class, Object...)} with {@link ParameterizedTypeReference}
	 * 
	 * @param url URL
	 * @param requestEntity {@link HttpEntity}
	 * @param responseType {@link ParameterizedTypeReference}
	 * @param uriVariables uri variables
	 * @param <T> object type
	 * @return object
	 * @throws RestClientException if failed to post
	 */
	default <T> T postForObject(String url, HttpEntity<?> requestEntity, ParameterizedTypeReference<T> responseType,
		Object... uriVariables) throws RestClientException {
		
		return this.exchange(url, HttpMethod.POST, requestEntity, responseType, uriVariables).getBody();
	}
	
	/**
	 * {@link #postForObject(String, Object, Class, Object...)} with {@link HttpHeaders}
	 * 
	 * @param url URL
	 * @param headers {@link HttpHeaders}
	 * @param responseType response type
	 * @param uriVariables uri variables
	 * @param <T> object type
	 * @return object
	 * @throws RestClientException if failed to post
	 */
	default <T> T postForObject(String url, HttpHeaders headers, Class<T> responseType, Object... uriVariables)
		throws RestClientException {
		
		return this.exchange(url, HttpMethod.POST, new HttpEntity<>(headers), responseType, uriVariables).getBody();
	}
}
