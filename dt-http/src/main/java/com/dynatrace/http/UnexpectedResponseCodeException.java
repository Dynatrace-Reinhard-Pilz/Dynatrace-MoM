package com.dynatrace.http;

import java.io.IOException;
import java.text.MessageFormat;

public class UnexpectedResponseCodeException extends IOException {

	private static final long serialVersionUID = 1L;
	
	static final String ERR_MSG_PATTERN =
			"Unexpected HTTP response code {0} received (expected: {1}) - URL: {2}"
			.intern();
	
	private final String serverResponse;
	
	public UnexpectedResponseCodeException(ResponseCode expected, int actual, String serverResponse, String path) {
		super(formatMessage(expected, actual, path));
		this.serverResponse = serverResponse;
	}
	
	public String getServerResponse() {
		return serverResponse;
	}
	
	private static String formatMessage(ResponseCode expected, int actual, String path) {
		ResponseCode actualCode = ResponseCode.fromCode(actual);
		if (actualCode == null) {
			return MessageFormat.format(ERR_MSG_PATTERN, actual, expected, path);			
		}
		return MessageFormat.format(ERR_MSG_PATTERN, actualCode, expected, path);
	}

}
