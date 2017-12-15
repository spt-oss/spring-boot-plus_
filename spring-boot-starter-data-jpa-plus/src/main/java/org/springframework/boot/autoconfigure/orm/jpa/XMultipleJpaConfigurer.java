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

package org.springframework.boot.autoconfigure.orm.jpa;

import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * JPA configurer for multiple
 */
public interface XMultipleJpaConfigurer {
	
	/**
	 * {@link PlatformTransactionManager}
	 * 
	 * @return {@link PlatformTransactionManager}
	 * @see JpaBaseConfiguration#transactionManager()
	 */
	PlatformTransactionManager transactionManager();
	
	/**
	 * {@link LocalContainerEntityManagerFactoryBean}
	 * 
	 * @return {@link LocalContainerEntityManagerFactoryBean}
	 * @see JpaBaseConfiguration#entityManagerFactory(EntityManagerFactoryBuilder)
	 */
	LocalContainerEntityManagerFactoryBean entityManagerFactory();
}
