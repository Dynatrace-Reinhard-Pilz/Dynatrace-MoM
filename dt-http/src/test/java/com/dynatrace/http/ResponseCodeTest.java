package com.dynatrace.http;

import com.dynatrace.EnumTest;

/**
 * Tests for class {@link ResponseCode}
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 */
public class ResponseCodeTest extends EnumTest<ResponseCode> {

	@Override
	protected Class<ResponseCode> getCoverageClass() {
		return ResponseCode.class;
	}
	
}
