package com.dynatrace.http;

public interface ResponseVerifier {

	public void verifyResponseHeader(String name, String value)
			throws VerificationException;
	
}
