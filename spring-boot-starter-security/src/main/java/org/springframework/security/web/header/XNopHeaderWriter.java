
package org.springframework.security.web.header;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * NOP {@link HeaderWriter}
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class XNopHeaderWriter implements HeaderWriter {
	
	/**
	 * Instance
	 */
	public static final XNopHeaderWriter INSTANCE = new XNopHeaderWriter();
	
	@Override
	public void writeHeaders(HttpServletRequest request, HttpServletResponse response) {
		
		/* NOP */
	}
}
