package com.dynatrace.monitors.license.usage.rest.requests;

import com.dynatrace.http.Method;
import com.dynatrace.http.ResponseCode;
import com.dynatrace.http.request.AbstractRequest;
import com.dynatrace.monitors.license.usage.rest.responses.XmlProfileRefs;

public final class ProfileRefsRequest extends AbstractRequest<XmlProfileRefs> {
	
	@Override
	protected String getPath() {
		return "/rest/management/profiles";
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
	protected XmlProfileRefs getResultPrototype() {
		return new XmlProfileRefs();
	}

}
