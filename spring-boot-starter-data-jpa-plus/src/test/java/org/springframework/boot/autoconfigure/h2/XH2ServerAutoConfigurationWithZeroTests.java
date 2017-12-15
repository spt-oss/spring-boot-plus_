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

import static org.assertj.core.api.Assertions.assertThat;

import org.h2.server.Service;
import org.h2.tools.Server;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Tests for {@link XH2ServerAutoConfiguration} with zero
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = XH2ServerAutoConfiguration.class)
@ActiveProfiles({ "test", "test-h2-config-with-zero" })
public class XH2ServerAutoConfigurationWithZeroTests {
	
	/**
	 * {@link XH2ServerProperties}
	 */
	@Autowired
	private XH2ServerProperties h2ServerProperties;
	
	/**
	 * {@link Server}
	 */
	@Autowired
	private Server h2Server;
	
	/**
	 * {@link XH2ServerProperties}
	 */
	@Test
	public void h2ServerProperties() {
		
		assertThat(this.h2ServerProperties.getPort()).isEqualTo(0);
		assertThat(this.h2ServerProperties.getBaseDir()).isNull();
		assertThat(this.h2ServerProperties.isAllowOthers()).isEqualTo(false);
		assertThat(this.h2ServerProperties.isDaemon()).isEqualTo(true);
	}
	
	/**
	 * {@link Server}
	 */
	@Test
	public void h2Server() {
		
		assertThat(this.h2Server.isRunning(false)).isEqualTo(true);
		
		Service service = this.h2Server.getService();
		
		assertThat(service.getPort()).isGreaterThan(0);
		assertThat(service.getAllowOthers()).isEqualTo(this.h2ServerProperties.isAllowOthers());
		assertThat(service.isDaemon()).isEqualTo(this.h2ServerProperties.isDaemon());
	}
}
