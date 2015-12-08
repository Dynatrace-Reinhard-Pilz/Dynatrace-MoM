package com.dynatrace.license;

import com.dynatrace.http.Method;
import com.dynatrace.http.ResponseCode;
import com.dynatrace.http.request.AbstractRequest;
import com.dynatrace.license.LicenseInfo;

/**
 * Retrieves the Build Version of a dynaTrace Server using the REST API
 * 
 * @author Reinhard Pilz
 *
 */
public class LicenseRequest extends AbstractRequest<LicenseInfo> {

	private static final LicenseInfo RESULT_PROTOTYPE = new LicenseInfo();
	private String COMMAND = "/rest/management/server/license/information".intern();

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
	protected LicenseInfo getResultPrototype() {
		return RESULT_PROTOTYPE;
	}
}
