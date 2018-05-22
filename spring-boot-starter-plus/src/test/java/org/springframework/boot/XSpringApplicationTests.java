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
package org.springframework.boot;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request; // TODO @checkstyle:ignore
import org.eclipse.jetty.server.Server; // TODO @checkstyle:ignore
import org.eclipse.jetty.server.handler.AbstractHandler; // TODO @checkstyle:ignore
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.SocketUtils;

import lombok.NonNull;

/**
 * Tests for {@link XSpringApplication}
 */
public class XSpringApplicationTests {
	
	/**
	 * Property prefix
	 */
	private static final String PROPERTY_PREFIX = "--" + XSpringApplication.SHUTDOWN_URL_PROPERTY_NAME + "=";
	
	/**
	 * {@link Server}
	 */
	private Server server;
	
	/**
	 * URL
	 */
	private String url;
	
	/**
	 * {@link Before}
	 * 
	 * @throws Exception if failed to start server
	 */
	@Before
	public void before() throws Exception {
		
		int port = SocketUtils.findAvailableTcpPort();
		
		Server server = new Server(port);
		server.setHandler(new TestHandler());
		server.start();
		
		this.server = server;
		this.url = "http://localhost:" + port;
	}
	
	/**
	 * {@link After}
	 * 
	 * @throws Exception if failed to stop server
	 */
	@After
	public void after() throws Exception {
		
		this.server.stop();
	}
	
	/**
	 * {@link XSpringApplication#shutdown(String...)}
	 */
	@Test
	public void shutdown() {
		
		// No arguments
		assertThat(XSpringApplication.shutdown("")).isEqualTo(false);
		
		// Invalid URL
		try {
			
			XSpringApplication.shutdown(PROPERTY_PREFIX + "-----");
			
			fail();
		}
		catch (IllegalStateException e) {
			
			assertThat(e.getMessage()).contains("Invalid");
		}
		
		// OK
		assertThat(XSpringApplication.shutdown(PROPERTY_PREFIX + this.url + "/200")).isEqualTo(true);
		
		// Not OK
		try {
			
			XSpringApplication.shutdown(PROPERTY_PREFIX + this.url);
			
			fail();
		}
		catch (IllegalStateException e) {
			
			assertThat(e.getMessage()).contains("not return OK");
		}
		
		// Connection error
		assertThat(XSpringApplication.shutdown(PROPERTY_PREFIX + "http://foo.bar.baz.qux/")).isEqualTo(false);
	}
	
	/**
	 * Test handler
	 */
	protected static class TestHandler extends AbstractHandler {
		
		@Override
		public void handle(@NonNull String target, @NonNull Request baseRequest, HttpServletRequest request,
			@NonNull HttpServletResponse response) throws IOException, ServletException {
			
			if (target.contains(String.valueOf(HttpServletResponse.SC_OK))) {
				
				response.setStatus(HttpServletResponse.SC_OK);
			}
			else {
				
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
			
			baseRequest.setHandled(true);
		}
	}
}
