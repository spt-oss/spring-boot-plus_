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

package org.flywaydb.core;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

import org.flywaydb.core.api.callback.FlywayCallback;
import org.flywaydb.core.api.resolver.MigrationResolver;
import org.flywaydb.core.internal.database.Database;
import org.flywaydb.core.internal.resolver.CompositeMigrationResolver;
import org.flywaydb.core.internal.resolver.sql.SqlMigrationResolver;
import org.flywaydb.core.internal.schemahistory.SchemaHistory;
import org.flywaydb.core.internal.util.PlaceholderReplacer;
import org.flywaydb.core.internal.util.XSqlReplacer;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Custom {@link Flyway}
 */
public class XFlyway extends Flyway {
	
	/**
	 * {@link PlaceholderReplacer}
	 */
	@Getter
	@Setter
	private PlaceholderReplacer placeholderReplacer;
	
	@Override
	protected <T> T execute(Command<T> command) {
		
		return super.execute(new XCommand<>(command, this.placeholderReplacer));
	}
	
	/**
	 * Custom {@link org.flywaydb.core.Flyway.Command}
	 * 
	 * @param <T> result type
	 */
	@RequiredArgsConstructor
	protected static class XCommand<T> implements Command<T> {
		
		/**
		 * Delegate
		 */
		@NonNull
		private final Command<T> delegate;
		
		/**
		 * {@link PlaceholderReplacer}
		 */
		private final PlaceholderReplacer placeholderReplacer;
		
		@Override
		public T execute(MigrationResolver migrationResolver, SchemaHistory schemaHistory,
			@SuppressWarnings("rawtypes") Database database,
			@SuppressWarnings("rawtypes") org.flywaydb.core.internal.database.Schema[] schemas,
			List<FlywayCallback> effectiveCallbacks) {
			
			if (this.placeholderReplacer != null) {
				
				this.replacePlaceholderReplacer(migrationResolver, this.placeholderReplacer);
			}
			
			return this.delegate.execute(migrationResolver, schemaHistory, database, schemas, effectiveCallbacks);
		}
		
		/**
		 * Replace {@link PlaceholderReplacer} to {@link XSqlReplacer}
		 * 
		 * @param migrationResolver {@link MigrationResolver}
		 * @param placeholderReplacer {@link PlaceholderReplacer}
		 */
		protected void replacePlaceholderReplacer(MigrationResolver migrationResolver,
			PlaceholderReplacer placeholderReplacer) {
			
			// Extract child resolvers
			if (migrationResolver instanceof CompositeMigrationResolver) {
				
				try {
					
					Field field = CompositeMigrationResolver.class.getDeclaredField("migrationResolvers");
					field.setAccessible(true);
					
					@SuppressWarnings("unchecked")
					Collection<MigrationResolver> childResolvers = (Collection<MigrationResolver>) field
						.get(migrationResolver);
					
					// Recursive call
					for (MigrationResolver childResolver : childResolvers) {
						
						this.replacePlaceholderReplacer(childResolver, placeholderReplacer);
					}
				}
				catch (ReflectiveOperationException e) {
					
					throw new IllegalStateException("Failed to get 'MigrationResolver'", e);
				}
			}
			
			// Replace
			else if (migrationResolver instanceof SqlMigrationResolver) {
				
				try {
					
					Field field = SqlMigrationResolver.class.getDeclaredField("placeholderReplacer");
					field.setAccessible(true);
					
					field.set(migrationResolver, placeholderReplacer);
				}
				catch (ReflectiveOperationException e) {
					
					throw new IllegalStateException("Failed to set 'PlaceholderReplacer'", e);
				}
			}
		}
	}
}
