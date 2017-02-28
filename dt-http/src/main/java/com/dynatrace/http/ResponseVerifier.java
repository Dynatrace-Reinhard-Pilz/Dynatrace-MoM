package com.dynatrace.http;

import java.net.URL;

public interface ResponseVerifier {

	public void verifyResponseHeader(URL url, String name, String value)
			throws VerificationException;
	
}
