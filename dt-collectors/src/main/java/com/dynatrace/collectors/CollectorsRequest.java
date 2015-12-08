package com.dynatrace.collectors;

import com.dynatrace.http.Method;
import com.dynatrace.http.ResponseCode;
import com.dynatrace.http.request.AbstractRequest;

public class CollectorsRequest extends AbstractRequest<CollectorInfos> {

	private static final CollectorInfos RESPONSE_PROTOTYPE = new CollectorInfos();
	public String COMMAND = "/rest/management/collectors".intern();
	
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
	protected CollectorInfos getResultPrototype() {
		return RESPONSE_PROTOTYPE;
	}

}
