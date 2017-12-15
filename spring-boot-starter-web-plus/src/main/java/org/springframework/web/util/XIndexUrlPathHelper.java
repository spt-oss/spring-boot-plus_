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

package org.springframework.web.util;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.Assert;

/**
 * Index {@link UrlPathHelper}
 */
public class XIndexUrlPathHelper extends UrlPathHelper {
	
	/**
	 * Index name
	 */
	private String indexName;
	
	/**
	 * Constrcutor
	 */
	public XIndexUrlPathHelper() {
		
		this("index");
	}
	
	/**
	 * Constructor
	 * 
	 * @param indexName index name
	 */
	public XIndexUrlPathHelper(String indexName) {
		
		Assert.hasText(indexName, "Index name is empty");
		
		this.indexName = indexName;
	}
	
	@Override
	public String getLookupPathForRequest(HttpServletRequest request) {
		
		String path = super.getLookupPathForRequest(request);
		
		if (path.endsWith("/")) {
			
			return path + this.indexName;
		}
		
		return path;
	}
}
