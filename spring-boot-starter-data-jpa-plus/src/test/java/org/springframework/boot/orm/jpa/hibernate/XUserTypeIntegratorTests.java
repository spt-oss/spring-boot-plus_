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

package org.springframework.boot.orm.jpa.hibernate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.hibernate.boot.Metadata; // TODO @checkstyle:ignore
import org.hibernate.boot.spi.MetadataImplementor; // TODO @checkstyle:ignore
import org.hibernate.type.TypeResolver; // TODO @checkstyle:ignore
import org.hibernate.usertype.UserType; // TODO @checkstyle:ignore
import org.jadira.usertype.dateandtime.threeten.PersistentDurationAsMillisLong; // TODO @checkstyle:ignore
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.autoconfigure.orm.jpa.XHibernateJpaConfiguration;

/**
 * Tests for {@link XHibernateJpaConfiguration}
 */
@RunWith(MockitoJUnitRunner.class)
public class XUserTypeIntegratorTests {
	
	/**
	 * {@link MetadataImplementor}
	 */
	@Mock
	private MetadataImplementor metadata;
	
	/**
	 * {@link TypeResolver}
	 */
	@Mock
	private TypeResolver typeResolver;
	
	/**
	 * {@link Before}
	 */
	@Before
	public void before() {
		
		reset(this.metadata, this.typeResolver);
	}
	
	/**
	 * {@link XUserTypeIntegrator#integrate(Metadata, org.hibernate.engine.spi.SessionFactoryImplementor, org.hibernate.service.spi.SessionFactoryServiceRegistry)}
	 */
	@Test
	public void integrate() {
		
		ArgumentCaptor<UserType> userTypeCaptor = ArgumentCaptor.forClass(UserType.class);
		
		{
			doNothing().when(this.typeResolver).registerTypeOverride(userTypeCaptor.capture(), any(String[].class));
			
			when(this.metadata.getTypeResolver()).thenReturn(this.typeResolver);
		}
		
		{
			List<UserType> userTypes = Arrays.asList(new PersistentDurationAsMillisLong());
			
			XUserTypeIntegrator integrator = new XUserTypeIntegrator(userTypes);
			
			integrator.integrate(this.metadata, null, null);
			
			assertThat(userTypeCaptor.getAllValues()).isEqualTo(userTypes);
		}
	}
}
