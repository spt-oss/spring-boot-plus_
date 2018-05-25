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

package org.springframework.boot.autoconfigure.flyway;

import org.flywaydb.core.CustomFlyway;
import org.flywaydb.core.Flyway;

/**
 * Custom {@link FlywayMigrationStrategy}
 */
public interface CustomFlywayMigrationStrategy extends FlywayMigrationStrategy {
	
	@Override
	default void migrate(Flyway flyway) {
		
		migrate((CustomFlyway) flyway);
	}
	
	/**
	 * Migrate
	 * 
	 * @param flyway {@link CustomFlyway}
	 */
	void migrate(CustomFlyway flyway);
}
