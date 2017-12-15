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

package org.springframework.boot.autoconfigure.h2;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * H2 server properties
 */
@ConfigurationProperties(XH2ServerProperties.PREFIX)
@Data
public class XH2ServerProperties {
	
	/**
	 * Prefix
	 */
	public static final String PREFIX = "spring.h2.server";
	
	/**
	 * Port
	 */
	private int port;
	
	/**
	 * Base directory
	 */
	private String baseDir;
	
	/**
	 * Allow others
	 */
	private boolean allowOthers;
	
	/**
	 * Daemon
	 */
	private boolean daemon = true;
}
