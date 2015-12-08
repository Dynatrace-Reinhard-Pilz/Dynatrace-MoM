package com.dynatrace.reporting;

import com.dynatrace.http.Method;
import com.dynatrace.http.ResponseCode;
import com.dynatrace.http.request.AbstractRequest;

/**
 * Retrieves the Build Version of a dynaTrace Server using the REST API
 * 
 * @author Reinhard Pilz
 *
 */
public class DynatraceServerHealthDashboardRequest extends AbstractRequest<DashboardReport> {

	private static final DashboardReport RESULT_PROTOTYPE = new DashboardReport();
	private String COMMAND = "/rest/management/reports/create/MoM%20dynaTrace%20Server%20Health?type=XML&format=XML+Export".intern();
	
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
	protected DashboardReport getResultPrototype() {
		return RESULT_PROTOTYPE;
	}
	
}
