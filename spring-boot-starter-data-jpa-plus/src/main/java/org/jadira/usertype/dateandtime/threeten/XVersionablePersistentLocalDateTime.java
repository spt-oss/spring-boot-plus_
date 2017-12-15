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

package org.jadira.usertype.dateandtime.threeten;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.jadira.usertype.dateandtime.threeten.columnmapper.TimestampColumnLocalDateTimeMapper; // TODO @checkstyle:ignore
import org.jadira.usertype.spi.shared.AbstractVersionableUserType; // TODO @checkstyle:ignore

import lombok.NonNull;

/**
 * Versionable {@link PersistentLocalDateTime}
 */
public class XVersionablePersistentLocalDateTime
	extends AbstractVersionableUserType<LocalDateTime, Timestamp, TimestampColumnLocalDateTimeMapper> {
	
	@SuppressWarnings("javadoc")
	private static final long serialVersionUID = 1L;
	
	@Override
	public int compare(@NonNull Object o1, Object o2) {
		
		return ((LocalDateTime) o1).compareTo((LocalDateTime) o2);
	}
}
