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

package org.springframework.boot.autoconfigure.claudb;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.SocketUtils;

import lombok.Data;

/**
 * ClauDB properties
 */
@ConfigurationProperties(ClauDbProperties.PREFIX)
@Data
public class ClauDbProperties {
	
	/**
	 * Prefix
	 */
	public static final String PREFIX = "spring.claudb";
	
	/**
	 * Port
	 */
	private int port;
	
	/**
	 * Determine port
	 * 
	 * @return port
	 * @throws IllegalStateException if failed to determine
	 */
	public int determinePort() throws IllegalStateException {
		
		if (this.port != 0) {
			
			return this.port;
		}
		
		return SocketUtils.findAvailableTcpPort();
	}
}
