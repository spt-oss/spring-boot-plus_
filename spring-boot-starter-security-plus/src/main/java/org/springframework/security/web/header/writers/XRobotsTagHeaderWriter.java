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

package org.springframework.security.web.header.writers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.web.header.HeaderWriter;

import lombok.NonNull;

/**
 * X-Robots-Tag {@link HeaderWriter}
 */
public class XRobotsTagHeaderWriter implements HeaderWriter {
	
	/**
	 * X-Robots-Tag header name
	 */
	public static final String X_ROBOTS_TAG_HEADER = "X-Robots-Tag";
	
	/**
	 * {@link XRobotsTagMode}
	 */
	private List<XRobotsTagMode> modes;
	
	/**
	 * Constructor
	 * 
	 * @param modes {@link XRobotsTagMode}
	 */
	public XRobotsTagHeaderWriter(XRobotsTagMode... modes) {
		
		this.modes = Arrays.asList(modes);
	}
	
	@Override
	public void writeHeaders(HttpServletRequest request, @NonNull HttpServletResponse response) {
		
		List<String> values = new ArrayList<>();
		
		for (XRobotsTagMode mode : this.modes) {
			
			values.add(mode.getValue());
		}
		
		response.addHeader(X_ROBOTS_TAG_HEADER, String.join(", ", values));
	}
	
	/**
	 * X-Robots-Tag mode
	 *
	 * @see "https://developers.google.com/webmasters/control-crawl-index/docs/robots_meta_tag"
	 */
	public enum XRobotsTagMode {
		
		/**
		 * all
		 */
		ALL,
		
		/**
		 * noindex
		 */
		NOINDEX,
		
		/**
		 * nofollow
		 */
		NOFOLLOW,
		
		/**
		 * none
		 */
		NONE,
		
		/**
		 * noarchive
		 */
		NOARCHIVE,
		
		/**
		 * nosnippet
		 */
		NOSNIPPET,
		
		/**
		 * noodp
		 */
		NOODP,
		
		/**
		 * notranslate
		 */
		NOTRANSLATE,
		
		/**
		 * noimageindex
		 */
		NOIMAGEINDEX;
		
		/**
		 * Get value
		 * 
		 * @return value
		 */
		protected String getValue() {
			
			return this.name().toLowerCase();
		}
	}
}
