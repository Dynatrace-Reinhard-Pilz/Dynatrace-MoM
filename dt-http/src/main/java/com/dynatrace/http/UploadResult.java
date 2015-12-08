package com.dynatrace.http;

import java.util.List;
import java.util.Map;

public class UploadResult {
	
	public final int status;
	public final List<String> response;
	public final Map<String, String> headers;
	
	public UploadResult(
		int status,
		List<String> response,
		Map<String, String> headers
	) {
		this.status = status;
		this.response = response;
		this.headers = headers;
	}
}
