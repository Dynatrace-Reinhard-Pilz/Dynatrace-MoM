package com.dynatrace.http;

import java.io.IOException;

public class VerificationException extends IOException {

	private static final long serialVersionUID = 1L;
	
	public VerificationException(String message) {
		super(message);
	}
	
	public VerificationException(Throwable throwable) {
		super(throwable);
	}
	
	public VerificationException(String message, Throwable throwable) {
		super(message, throwable);
	}

}
