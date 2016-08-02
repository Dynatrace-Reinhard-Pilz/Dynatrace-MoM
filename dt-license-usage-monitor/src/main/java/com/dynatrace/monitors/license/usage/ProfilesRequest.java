package com.dynatrace.monitors.license.usage;


import java.util.logging.Level;

import com.dynatrace.http.Method;
import com.dynatrace.http.ResponseCode;
import com.dynatrace.http.request.AbstractRequest;

/**
 * @author reinhard.pilz@dynatrace.com
 */
public class ProfilesRequest extends AbstractRequest<MetaSystemProfiles> {
	
	private static final MetaSystemProfiles RESPONSE_PROTOTYPE =
			new MetaSystemProfiles();
	public final String COMMAND = "/rest/management/profiles".intern();
	
	@Override
	protected Level level() {
		return Level.FINE;
	}

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
	protected MetaSystemProfiles getResultPrototype() {
		return RESPONSE_PROTOTYPE;
	}

}
