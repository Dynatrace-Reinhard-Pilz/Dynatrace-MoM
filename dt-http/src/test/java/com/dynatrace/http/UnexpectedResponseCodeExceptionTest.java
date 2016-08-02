package com.dynatrace.http;

import org.junit.Test;

/**
 * Tests for class {@link UnexpectedResponseCodeException}
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 */
public class UnexpectedResponseCodeExceptionTest {

	@Test
	public void testException() {
		Exception e = new UnexpectedResponseCodeException(ResponseCode.OK, 0, null, null);
		e.getMessage();
		e = new UnexpectedResponseCodeException(ResponseCode.OK, 405, null, null);
		e.getMessage();
	}
}
