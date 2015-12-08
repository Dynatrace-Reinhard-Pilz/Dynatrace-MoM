package com.dynatrace.cmd;

public class UnknownOptionException extends CommandLineException {

	private static final long serialVersionUID = 1L;
	
	private final String arg;
	
	public UnknownOptionException(String arg) {
		this.arg = arg;
	}
	
	@Override
	public String getMessage() {
		return "Unknown option '" + arg + "'";
	}

}
