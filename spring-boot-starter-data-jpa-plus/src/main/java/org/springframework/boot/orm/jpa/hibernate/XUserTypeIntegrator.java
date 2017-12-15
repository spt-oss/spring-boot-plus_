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

import java.util.Arrays;
import java.util.List;

import org.hibernate.boot.Metadata; // TODO @checkstyle:ignore
import org.hibernate.boot.spi.MetadataImplementor; // TODO @checkstyle:ignore
import org.hibernate.engine.spi.SessionFactoryImplementor; // TODO @checkstyle:ignore
import org.hibernate.integrator.spi.Integrator; // TODO @checkstyle:ignore
import org.hibernate.service.spi.SessionFactoryServiceRegistry; // TODO @checkstyle:ignore
import org.hibernate.type.TypeResolver; // TODO @checkstyle:ignore
import org.hibernate.usertype.UserType; // TODO @checkstyle:ignore

import lombok.NonNull;

/**
 * {@link Integrator} for {@link UserType}
 * 
 * @see org.jadira.usertype.spi.shared.AbstractUserTypeHibernateIntegrator
 */
public class XUserTypeIntegrator implements Integrator {
	
	/**
	 * {@link Integrator}
	 */
	private List<UserType> userTypes;
	
	/**
	 * Constructor
	 * 
	 * @param userTypes {@link UserType}
	 */
	public XUserTypeIntegrator(UserType... userTypes) {
		
		this(Arrays.asList(userTypes));
	}
	
	/**
	 * Constructor
	 * 
	 * @param userTypes {@link UserType}
	 */
	public XUserTypeIntegrator(@NonNull List<UserType> userTypes) {
		
		this.userTypes = userTypes;
	}
	
	@Override
	public void integrate(@NonNull Metadata metadata, SessionFactoryImplementor sessionFactory,
		SessionFactoryServiceRegistry serviceRegistry) {
		
		TypeResolver resolver = ((MetadataImplementor) metadata).getTypeResolver();
		
		for (UserType userType : this.userTypes) {
			
			resolver.registerTypeOverride(userType, new String[] { userType.returnedClass().getName() });
		}
	}
	
	@Override
	public void disintegrate(SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
		
		/* NOP */
	}
}
