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
package org.flywaydb.core.internal.util;

import java.util.regex.Pattern;

import lombok.AllArgsConstructor;

/**
 * {@link XSqlReplacer} for MySQL-SQL to H2-SQL
 */
public class XMysqlToH2SqlReplacer extends XSqlReplacer {
	
	/**
	 * $1
	 */
	protected static final String GROUP_1 = "$1";
	
	/**
	 * \n
	 */
	protected static final String LF = "\n";
	
	/**
	 * INDEX
	 */
	protected static final String INDEX = "INDEX";
	
	@Override
	public String replace(String sql) {
		
		XInternalReplacer replacer = new XInternalReplacer(sql);
		
		// Remove 'SET ...@...'
		replacer.remove("(?m)^SET(?:\\s+)(?:.*?)@(?:.*?)$");
		
		// Remove 'LOCK' and 'UNLOCK'
		replacer.remove("(?m)^LOCK(?:\\s+)TABLES(?:\\s+)(?:.*?)$");
		replacer.remove("(?m)^UNLOCK(?:\\s+)TABLES(?:.*?)$");
		
		// Replace 'UNIQUE /* CONSTRAINT */ INDEX' to 'INDEX'
		replacer.replace("UNIQUE(?:\\s*)/\\*(?:\\s*)CONSTRAINT(?:\\s*)\\*/(?:\\s*)(INDEX|KEY)", INDEX);
		
		// Replace 'DROP /* UNIQUE */ INDEX' to 'DROP CONSTRAINT'
		replacer.replace("DROP(?:\\s*)/\\*(?:\\s*)UNIQUE(?:\\s*)\\*/(?:\\s*)(INDEX|KEY)", "DROP CONSTRAINT");
		
		// Replace 'FULLTEXT' in table
		replacer.replace("FULLTEXT(?:\\s+)(?:INDEX|KEY)", INDEX);
		replacer.replace("FULLTEXT", INDEX);
		
		// Remove 'WITH PARSER' in table
		replacer.remove("WITH(?:\\s+)PARSER(?:\\s+)(?:[^;]+)");
		
		// Replace 'TEXT' to 'VARCHAR' for fulltext index
		replacer.replace("(\\s+)TEXT([\\s,]+)", "$1VARCHAR(" + Integer.MAX_VALUE + ")$2");
		
		// Remove 'AFTER' in 'CHANGE/MODIFY' column
		replacer.replace("((?:\\s*)CHANGE(?:\\s+)COLUMN(?:\\s+)(?:.*?))(?:\\s+)AFTER(?:\\s+)(?:[^;]+)", GROUP_1);
		replacer.replace("((?:\\s*)MODIFY(?:\\s+)COLUMN(?:\\s+)(?:.*?))(?:\\s+)AFTER(?:\\s+)(?:[^;]+)", GROUP_1);
		
		// Remove 'CHARACTER SET' in column
		replacer.remove("(?:\\s*)CHARACTER(?:\\s+)SET(?:\\s+)'(?:.*?)'(?:\\s+)COLLATE(?:\\s+)'(?:.*?)'");
		replacer.remove("(?:\\s*)CHARACTER(?:\\s+)SET(?:\\s+)'(?:.*?)'");
		
		// Remove 'COMMENT' in table
		replacer.remove("(?:\\s*)COMMENT(?:\\s*)=(?:\\s*)(?:[^;]+)");
		
		// Replace back-quote to double-quate
		replacer.replace("`", "\"");
		
		// Remove SQL comment
		replacer.remove("(?m)^--(?:.*?)$");
		
		// Remove newlines
		replacer.replace("\r\n", LF);
		replacer.replace("\r", LF);
		replacer.replace("\n\n\n+", LF + LF);
		replacer.replace("^\n+", LF);
		replacer.replace("\n+$", LF);
		
		return replacer.toString();
	}
	
	/**
	 * Internal replacer
	 */
	@AllArgsConstructor
	protected static class XInternalReplacer {
		
		/**
		 * SQL
		 */
		private String sql;
		
		/**
		 * Remove pattern in SQL
		 * 
		 * @param pattern pattern
		 * @return {@link XInternalReplacer}
		 */
		public XInternalReplacer remove(String pattern) {
			
			return this.replace(pattern, "");
		}
		
		/**
		 * Replace pattern in SQL
		 * 
		 * @param pattern pattern
		 * @param replacement replacement
		 * @return {@link XInternalReplacer}
		 */
		public XInternalReplacer replace(String pattern, String replacement) {
			
			this.sql = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(this.sql).replaceAll(replacement);
			
			return this;
		}
		
		@Override
		public String toString() {
			
			return this.sql;
		}
	}
}
