package com.dynatrace.http;

import com.dynatrace.http.BooleanResult;
import com.dynatrace.http.Method;
import com.dynatrace.http.ResponseCode;
import com.dynatrace.http.request.AbstractRequest;

/**
 * Orders the dynaTrace Server to restart
 * 
 * @author Reinhard Pilz
 *
 */
public class RestartServerRequest extends AbstractRequest<BooleanResult> {

	private static final BooleanResult RESULT_PROTOTYPE = new BooleanResult();
	public String COMMAND = "/rest/management/server/restart".intern();

	@Override
	protected String getPath() {
		return COMMAND;
	}

	@Override
	protected Method getMethod() {
		return Method.POST;
	}

	@Override
	protected ResponseCode getExpectedResponseCode() {
		return ResponseCode.OK;
	}

	@Override
	protected BooleanResult getResultPrototype() {
		return RESULT_PROTOTYPE;
	}
	
}
