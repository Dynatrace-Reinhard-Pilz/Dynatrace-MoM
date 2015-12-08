package com.dynatrace.collectors;

import java.text.MessageFormat;
import java.util.Objects;

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
public class RestartCollectorRequest extends AbstractRequest<BooleanResult> {

	private static final BooleanResult RESULT_PROTOTYPE = BooleanResult.TRUE;
	public static String COMMAND =
			"/rest/management/collector/{0}/restart".intern();
	
	private final String collectorName;
	
	public RestartCollectorRequest(String collectorName) {
		Objects.requireNonNull(collectorName);
		this.collectorName = collectorName;
	}

	@Override
	protected String getPath() {
		return MessageFormat.format(COMMAND, this.collectorName);
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
