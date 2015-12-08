package com.dynatrace.profiles;


import com.dynatrace.http.Method;
import com.dynatrace.http.ResponseCode;
import com.dynatrace.http.request.AbstractRequest;

/**
 * @author reinhard.pilz@dynatrace.com
 */
public class ProfilesRequest extends AbstractRequest<SystemProfiles> {
	
	private static final SystemProfiles RESPONSE_PROTOTYPE =
			new SystemProfiles();
	public final String COMMAND = "/rest/management/profiles".intern();

	@Override
	protected String getPath() {
		return COMMAND;
	}

	@Override
	protected Method getMethod() {
		return Method.GET;
	}

	@Override
	protected ResponseCode getExpectedResponseCode() {
		return ResponseCode.OK;
	}

	@Override
	protected SystemProfiles getResultPrototype() {
		return RESPONSE_PROTOTYPE;
	}

}
