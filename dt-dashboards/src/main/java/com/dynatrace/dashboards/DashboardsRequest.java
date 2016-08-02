package com.dynatrace.dashboards;

import java.util.logging.Level;

import com.dynatrace.http.Method;
import com.dynatrace.http.ResponseCode;
import com.dynatrace.http.request.AbstractRequest;

public class DashboardsRequest extends AbstractRequest<Dashboards> {
	
	private static final Dashboards RESPONSE_PROTOTYPE = new Dashboards();
	public String COMMAND = "/rest/management/dashboards".intern();
	
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
	protected Dashboards getResultPrototype() {
		return RESPONSE_PROTOTYPE;
	}

}
