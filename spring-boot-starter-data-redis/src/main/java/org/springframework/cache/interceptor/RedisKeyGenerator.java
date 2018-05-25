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

package org.springframework.cache.interceptor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.cache.annotation.CacheParam;
import org.springframework.core.MethodParameter;

import lombok.NonNull;

/**
 * Redis {@link KeyGenerator}
 */
public class RedisKeyGenerator implements KeyGenerator {
	
	@Override
	public Object generate(Object target, Method method, @NonNull Object... params) {
		
		// Merge parameter name and value
		Map<String, Object> entries = new LinkedHashMap<>();
		
		for (int index = 0; index < method.getParameterCount(); index++) {
			
			CacheParam cacheParam = new MethodParameter(method, index).getParameterAnnotation(CacheParam.class);
			
			if (cacheParam != null && !cacheParam.required()) {
				
				continue;
			}
			
			entries.put(method.getParameters()[index].getName(), params[index]);
		}
		
		// Generate key
		List<String> elements = new ArrayList<>();
		
		for (Entry<String, Object> entry : entries.entrySet()) {
			
			elements.add(entry.getKey());
			elements.add(String.valueOf(entry.getValue()));
		}
		
		return String.join(":", elements);
	}
}
