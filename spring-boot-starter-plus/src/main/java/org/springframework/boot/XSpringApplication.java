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

import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner.Mode;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.SimpleCommandLinePropertySource;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import lombok.NonNull;

/**
 * Custom {@link SpringApplication}
 */
public class XSpringApplication extends SpringApplication {
	
	/**
	 * {@link Logger}
	 */
	private static final Logger logger = LoggerFactory.getLogger(SpringApplication.class);
	
	/**
	 * Property name for shutdown URL
	 */
	public static final String SHUTDOWN_URL_PROPERTY_NAME = "management.endpoint.shutdown.url";
	
	/**
	 * Constructor
	 * 
	 * @param primarySources primary sources
	 */
	public XSpringApplication(Class<?>... primarySources) {
		
		super(primarySources);
		
		this.setBannerMode(Mode.LOG);
	}
	
	/**
	 * Get additional profiles
	 * 
	 * @return additional profiles
	 */
	protected Set<String> getAdditionalProfiles() {
		
		Field field = ReflectionUtils.findField(SpringApplication.class, "additionalProfiles");
		
		Assert.notNull(field, "Field 'additionalProfiles' not found");
		
		ReflectionUtils.makeAccessible(field);
		
		@SuppressWarnings("unchecked")
		Set<String> profiles = (Set<String>) ReflectionUtils.getField(field, this);
		
		return profiles;
	}
	
	@Override
	public ConfigurableApplicationContext run(String... args) throws IllegalStateException {
		
		String profile = new SimpleCommandLinePropertySource(args)
			.getProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME);
		
		if (!StringUtils.hasText(profile) && this.getAdditionalProfiles().isEmpty()) {
			
			throw new IllegalStateException("Active profiles not found");
		}
		
		return super.run(args);
	}
	
	/**
	 * {@link SpringApplication#run(Class, String...)}
	 * 
	 * @param primarySource primary source
	 * @param args arguments
	 * @return {@link ConfigurableApplicationContext}
	 */
	public static ConfigurableApplicationContext run(Class<?> primarySource, String... args) {
		
		return run(new Class<?>[] { primarySource }, args);
	}
	
	/**
	 * {@link SpringApplication#run(Class[], String[])}
	 * 
	 * @param primarySources primary sources
	 * @param args arguments
	 * @return {@link ConfigurableApplicationContext}
	 */
	public static ConfigurableApplicationContext run(Class<?>[] primarySources, String[] args) {
		
		return new XSpringApplication(primarySources).run(args);
	}
	
	/**
	 * Shutdown active application
	 * 
	 * @param args args
	 * @return {@code true} if shutdowned
	 * @throws IllegalStateException if failed to shutdown
	 */
	public static boolean shutdown(String... args) throws IllegalStateException {
		
		// Find URL
		String url = new SimpleCommandLinePropertySource(args).getProperty(SHUTDOWN_URL_PROPERTY_NAME);
		
		if (!StringUtils.hasText(url)) {
			
			return false;
		}
		
		// Execute request
		URL request = null;
		
		try {
			
			request = new URL(url);
		}
		catch (MalformedURLException e) {
			
			throw new IllegalStateException(String.format("Invalid shutdown endpoint URL: %s", url), e);
		}
		
		HttpURLConnection connection = null;
		
		try {
			
			connection = (HttpURLConnection) request.openConnection();
			connection.setRequestMethod("POST");
			
			int status = connection.getResponseCode();
			
			if (status == 200) {
				
				logger.debug("Active application stopped");
				
				return true;
			}
			
			throw new IllegalStateException(String.format("Shutdown endpoint did not return OK: %s", status));
		}
		catch (IOException e) {
			
			logger.debug("Application is not running");
			
			return false;
		}
		finally {
			
			if (connection != null) {
				
				connection.disconnect();
			}
		}
	}
	
	/**
	 * Set default {@link TimeZone}
	 * 
	 * @param zone {@link TimeZone}
	 */
	public static void setDefaultTimeZone(TimeZone zone) {
		
		TimeZone.setDefault(zone);
	}
	
	/**
	 * Set default {@link Locale}
	 * 
	 * @param locale {@link Locale}
	 */
	public static void setDefaultLocale(Locale locale) {
		
		Locale.setDefault(locale);
	}
	
	/**
	 * Set default {@link Charset}
	 * 
	 * @param charset {@link Charset}
	 * @throws IllegalStateException if failed to set
	 * @see "http://stackoverflow.com/questions/361975/setting-the-default-java-character-encoding"
	 */
	public static void setDefaultCharset(@NonNull Charset charset) throws IllegalStateException {
		
		System.setProperty("file.encoding", charset.name());
		
		// Reset charset
		try {
			
			Field defaultCharset = Charset.class.getDeclaredField("defaultCharset");
			defaultCharset.setAccessible(true);
			defaultCharset.set(null, null);
		}
		catch (ReflectiveOperationException e) {
			
			throw new IllegalStateException("Failed to set default charset", e);
		}
	}
	
	/**
	 * Set default {@link UncaughtExceptionHandler}
	 * 
	 * @param exceptionHandler {@link UncaughtExceptionHandler}
	 */
	public static void setDefaultUncaughtExceptionHandler(UncaughtExceptionHandler exceptionHandler) {
		
		Thread.setDefaultUncaughtExceptionHandler(exceptionHandler);
	}
}
