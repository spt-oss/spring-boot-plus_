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

package org.springframework.boot.autoconfigure.orm.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.MultipleJpaConfigurerAdapterTests.BarJpaConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.MultipleJpaConfigurerAdapterTests.BazJpaConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.MultipleJpaConfigurerAdapterTests.FooJpaConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.example.bar.Bar;
import org.springframework.boot.autoconfigure.orm.jpa.example.bar.BarRepository;
import org.springframework.boot.autoconfigure.orm.jpa.example.baz.Baz;
import org.springframework.boot.autoconfigure.orm.jpa.example.baz.BazRepository;
import org.springframework.boot.autoconfigure.orm.jpa.example.foo.Foo;
import org.springframework.boot.autoconfigure.orm.jpa.example.foo.FooRepository;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.EntityManagerFactoryInfo;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.PlatformTransactionManager;

import com.zaxxer.hikari.HikariDataSource;

/**
 * {@link Test}: {@link MultipleJpaConfigurer}
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
	/* @formatter:off */
	FooJpaConfiguration.class,
	BarJpaConfiguration.class,
	BazJpaConfiguration.class
	/* @formatter:on */
})
@ActiveProfiles({ "test", "test-multiple-jpa-config" })
public class MultipleJpaConfigurerAdapterTests {
	
	/**
	 * Foo
	 */
	private static final String FOO = "foo";
	
	/**
	 * Bar
	 */
	private static final String BAR = "bar";
	
	/**
	 * Baz
	 */
	private static final String BAZ = "baz";
	
	/**
	 * {@link DataSource} for {@link #FOO}
	 */
	@Autowired
	@Qualifier(FOO)
	private DataSource fooDataSource;
	
	/**
	 * {@link DataSource} for {@link #BAR}
	 */
	@Autowired
	private DataSource barDataSource;
	
	/**
	 * {@link DataSource} for {@link #BAZ}
	 */
	@Autowired
	private DataSource bazDataSource;
	
	/**
	 * {@link PlatformTransactionManager} for {@link #FOO}
	 */
	@Autowired
	@Qualifier(FOO)
	private PlatformTransactionManager fooTransactionManager;
	
	/**
	 * {@link PlatformTransactionManager} for {@link #BAR}
	 */
	@Autowired
	private PlatformTransactionManager barTransactionManager;
	
	/**
	 * {@link PlatformTransactionManager} for {@link #BAZ}
	 */
	@Autowired
	private PlatformTransactionManager bazTransactionManager;
	
	/**
	 * {@link EntityManagerFactory} for {@link #FOO}
	 */
	@Autowired
	@Qualifier("&entityManagerFactory")
	private LocalContainerEntityManagerFactoryBean fooEntityManagerFactoryBean;
	
	/**
	 * {@link EntityManagerFactory} for {@link #BAR}
	 */
	@Autowired
	@Qualifier("&" + BAR + MultipleJpaConfigurerAdapter.ENTITY_MANAGER_FACTORY_BEAN_SUFFIX)
	private LocalContainerEntityManagerFactoryBean barEntityManagerFactoryBean;
	
	/**
	 * {@link EntityManagerFactory} for {@link #BAR}
	 */
	@Autowired
	@Qualifier("&" + BAZ + MultipleJpaConfigurerAdapter.ENTITY_MANAGER_FACTORY_BEAN_SUFFIX)
	private LocalContainerEntityManagerFactoryBean bazEntityManagerFactoryBean;
	
	/**
	 * {@link EntityManagerFactory} for {@link #FOO}
	 */
	@Autowired
	@Qualifier(FOO)
	private EntityManagerFactory fooEntityManagerFactory;
	
	/**
	 * {@link EntityManagerFactory} for {@link #BAR}
	 */
	@Autowired
	private EntityManagerFactory barEntityManagerFactory;
	
	/**
	 * {@link EntityManagerFactory} for {@link #BAZ}
	 */
	@Autowired
	private EntityManagerFactory bazEntityManagerFactory;
	
	/**
	 * {@link FooRepository}
	 */
	@Autowired
	private FooRepository fooRepository;
	
	/**
	 * {@link BarRepository}
	 */
	@Autowired
	private BarRepository barRepository;
	
	/**
	 * {@link BazRepository}
	 */
	@Autowired
	private BazRepository bazRepository;
	
	/**
	 * {@link MultipleJpaConfigurerAdapter#dataSource()}
	 */
	@Test
	public void dataSource() {
		
		{
			HikariDataSource dataSource = (HikariDataSource) this.fooDataSource;
			
			assertThat(dataSource.getJdbcUrl()).startsWith("jdbc:h2:mem:foo");
			assertThat(dataSource.getPoolName()).isEqualTo(FOO);
		}
		
		{
			HikariDataSource dataSource = (HikariDataSource) this.barDataSource;
			
			assertThat(dataSource.getJdbcUrl()).startsWith("jdbc:h2:mem:bar");
			assertThat(dataSource.getPoolName()).isEqualTo(BAR);
		}
		
		{
			HikariDataSource dataSource = (HikariDataSource) this.bazDataSource;
			
			assertThat(dataSource.getJdbcUrl()).startsWith("jdbc:h2:mem:baz");
			assertThat(dataSource.getPoolName()).isEqualTo(BAZ);
		}
	}
	
	/**
	 * {@link MultipleJpaConfigurerAdapter#transactionManager()}
	 */
	@Test
	public void transactionManager() {
		
		{
			JpaTransactionManager transactionManager = (JpaTransactionManager) this.fooTransactionManager;
			
			assertThat(transactionManager.getPersistenceUnitName()).isEqualTo(FOO);
			assertThat(transactionManager.getEntityManagerFactory()).isEqualTo(this.fooEntityManagerFactory);
		}
		
		{
			JpaTransactionManager transactionManager = (JpaTransactionManager) this.barTransactionManager;
			
			assertThat(transactionManager.getPersistenceUnitName()).isEqualTo(BAR);
			assertThat(transactionManager.getEntityManagerFactory()).isEqualTo(this.barEntityManagerFactory);
		}
		
		{
			JpaTransactionManager transactionManager = (JpaTransactionManager) this.bazTransactionManager;
			
			assertThat(transactionManager.getPersistenceUnitName()).isEqualTo(BAZ);
			assertThat(transactionManager.getEntityManagerFactory()).isEqualTo(this.bazEntityManagerFactory);
		}
	}
	
	/**
	 * {@link MultipleJpaConfigurerAdapter#entityManagerFactory()}
	 */
	@Test
	public void entityManagerFactoryBean() {
		
		assertThat(this.fooEntityManagerFactoryBean.getPersistenceUnitName()).isEqualTo(FOO);
		assertThat(this.fooEntityManagerFactoryBean.getDataSource()).isEqualTo(this.fooDataSource);
		
		assertThat(this.barEntityManagerFactoryBean.getPersistenceUnitName()).isEqualTo(BAR);
		assertThat(this.barEntityManagerFactoryBean.getDataSource()).isEqualTo(this.barDataSource);
		
		assertThat(this.bazEntityManagerFactoryBean.getPersistenceUnitName()).isEqualTo(BAZ);
		assertThat(this.bazEntityManagerFactoryBean.getDataSource()).isEqualTo(this.bazDataSource);
	}
	
	/**
	 * {@link MultipleJpaConfigurerAdapter#entityManagerFactory()}
	 */
	@Test
	public void entityManagerFactory() {
		
		// Check proxy
		assertThat(this.fooEntityManagerFactory.getClass().getInterfaces()).contains(EntityManagerFactoryInfo.class);
		
		{
			EntityManagerFactoryInfo entityManagerFactory = (EntityManagerFactoryInfo) this.fooEntityManagerFactory;
			
			assertThat(entityManagerFactory.getPersistenceUnitName()).isEqualTo(FOO);
			assertThat(entityManagerFactory.getDataSource()).isEqualTo(this.fooDataSource);
		}
		
		{
			EntityManagerFactoryInfo entityManagerFactory = (EntityManagerFactoryInfo) this.barEntityManagerFactory;
			
			assertThat(entityManagerFactory.getPersistenceUnitName()).isEqualTo(BAR);
			assertThat(entityManagerFactory.getDataSource()).isEqualTo(this.barDataSource);
		}
		
		{
			EntityManagerFactoryInfo entityManagerFactory = (EntityManagerFactoryInfo) this.bazEntityManagerFactory;
			
			assertThat(entityManagerFactory.getPersistenceUnitName()).isEqualTo(BAZ);
			assertThat(entityManagerFactory.getDataSource()).isEqualTo(this.bazDataSource);
		}
	}
	
	/**
	 * {@link EnableJpaRepositories}
	 */
	@Test
	public void jpaRepository() {
		
		assertThat(this.fooRepository.findAll()).isEmpty();
		assertThat(this.barRepository.findAll()).isEmpty();
		assertThat(this.bazRepository.findAll()).isEmpty();
		
		assertThat(this.fooRepository.save(new Foo().setValue(FOO))).isEqualTo(new Foo(1L, FOO));
		assertThat(this.barRepository.save(new Bar().setValue(BAR))).isEqualTo(new Bar(1L, BAR));
		assertThat(this.bazRepository.save(new Baz().setValue(BAZ))).isEqualTo(new Baz(1L, BAZ));
		
		assertThat(this.fooRepository.findAll()).isNotEmpty();
		assertThat(this.barRepository.findAll()).isNotEmpty();
		assertThat(this.bazRepository.findAll()).isNotEmpty();
	}
	
	/**
	 * {@link MultipleJpaConfigurerAdapter} for {@link MultipleJpaConfigurerAdapterTests#FOO}
	 */
	@Configuration
	protected static class FooJpaConfiguration extends MultipleJpaConfigurerAdapter {
		
		@Bean
		@Qualifier(FOO)
		@Override
		public DataSource dataSource() {
			
			return super.dataSource();
		}
		
		@Bean
		@Qualifier(FOO)
		@Override
		public PlatformTransactionManager transactionManager() {
			
			return super.transactionManager();
		}
		
		@Bean
		@Qualifier(FOO)
		@Override
		public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
			
			return super.entityManagerFactory();
		}
		
		@Override
		protected Class<?> basePackageClass() {
			
			return Foo.class;
		}
		
		/**
		 * {@link Configuration} for {@link EnableJpaRepositories}
		 */
		@Configuration
		@EnableJpaRepositories(
		/* @formatter:off */
			basePackageClasses = FooRepository.class
			/* @formatter:on */
		)
		protected static class JpaRepositoryConfiguration {
			
			/* NOP */
		}
	}
	
	/**
	 * {@link MultipleJpaConfigurerAdapter} for {@link MultipleJpaConfigurerAdapterTests#BAR}
	 */
	@Configuration
	protected static class BarJpaConfiguration extends MultipleJpaConfigurerAdapter {
		
		@Bean(BAR + DATASOURCE_BEAN_SUFFIX)
		@Override
		public DataSource dataSource() {
			
			return super.dataSource();
		}
		
		@Bean(BAR + TRANSACTION_MANAGER_BEAN_SUFFIX)
		@Override
		public PlatformTransactionManager transactionManager() {
			
			return super.transactionManager();
		}
		
		@Bean(BAR + ENTITY_MANAGER_FACTORY_BEAN_SUFFIX)
		@Override
		public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
			
			return super.entityManagerFactory();
		}
		
		@Override
		protected Class<?> basePackageClass() {
			
			return Bar.class;
		}
		
		/**
		 * {@link Configuration} for {@link EnableJpaRepositories}
		 */
		@Configuration
		@EnableJpaRepositories(
		/* @formatter:off */
			basePackageClasses = BarRepository.class,
			entityManagerFactoryRef = BAR + ENTITY_MANAGER_FACTORY_BEAN_SUFFIX,
			transactionManagerRef = BAR + TRANSACTION_MANAGER_BEAN_SUFFIX
			/* @formatter:on */
		)
		protected static class JpaRepositoryConfiguration {
			
			/* NOP */
		}
	}
	
	/**
	 * {@link MultipleJpaConfigurerAdapter} for {@link MultipleJpaConfigurerAdapterTests#BAZ}
	 */
	@Configuration
	protected static class BazJpaConfiguration extends MultipleJpaConfigurerAdapter {
		
		@Bean(BAZ + DATASOURCE_BEAN_SUFFIX)
		@Override
		public DataSource dataSource() {
			
			return super.dataSource();
		}
		
		@Bean(BAZ + TRANSACTION_MANAGER_BEAN_SUFFIX)
		@Override
		public PlatformTransactionManager transactionManager() {
			
			return super.transactionManager();
		}
		
		@Bean(BAZ + ENTITY_MANAGER_FACTORY_BEAN_SUFFIX)
		@Override
		public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
			
			return super.entityManagerFactory();
		}
		
		@Override
		protected Class<?> basePackageClass() {
			
			return Baz.class;
		}
		
		/**
		 * {@link Configuration} for {@link EnableJpaRepositories}
		 */
		@Configuration
		@EnableJpaRepositories(
		/* @formatter:off */
			basePackageClasses = BazRepository.class,
			entityManagerFactoryRef = BAZ + ENTITY_MANAGER_FACTORY_BEAN_SUFFIX,
			transactionManagerRef = BAZ + TRANSACTION_MANAGER_BEAN_SUFFIX
			/* @formatter:on */
		)
		protected static class JpaRepositoryConfiguration {
			
			/* NOP */
		}
	}
}
