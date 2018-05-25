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

package org.springframework.boot.autoconfigure.h2;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.SocketUtils;
import org.springframework.util.StringUtils;

import lombok.Data;

/**
 * H2 server properties
 */
@ConfigurationProperties(H2ServerProperties.PREFIX)
@Data
public class H2ServerProperties {
	
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
	
	/**
	 * Create arguments
	 * 
	 * @return arguments
	 */
	public String[] createArguments() {
		
		List<String> args = new ArrayList<>();
		args.add("-tcp");
		args.add("-tcpPort");
		args.add(String.valueOf(this.determinePort()));
		args.add("-baseDir");
		args.add(this.determineBaseDir());
		
		if (this.allowOthers) {
			
			args.add("-tcpAllowOthers");
		}
		
		if (this.daemon) {
			
			args.add("-tcpDaemon");
		}
		
		return args.toArray(new String[0]);
	}
	
	/**
	 * Determine port
	 * 
	 * @return port
	 * @throws IllegalStateException if failed to determine
	 */
	protected int determinePort() throws IllegalStateException {
		
		if (this.port != 0) {
			
			return this.port;
		}
		
		return SocketUtils.findAvailableTcpPort();
	}
	
	/**
	 * Determine base directory
	 * 
	 * @return base directory
	 * @throws IllegalStateException if failed to determine
	 */
	protected String determineBaseDir() throws IllegalStateException {
		
		if (StringUtils.hasText(this.baseDir)) {
			
			return this.baseDir;
		}
		
		try {
			
			return Files.createTempDirectory("H2-").toString();
		}
		catch (IOException e) {
			
			throw new IllegalStateException("Failed to determine base directory", e);
		}
	}
}
