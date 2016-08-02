package com.dynatrace.onboarding.profiles;

public class InvalidProfileNameException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public InvalidProfileNameException(String message) {
		super(message);
	}

}
