package com.dynatrace.cmd;

public class MissingOptionException extends CommandLineException {

	private static final long serialVersionUID = 1L;
	
	private final Option<?> option;
	
	public MissingOptionException(Option<?> option) {
		this.option = option;
	}
	
	@Override
	public String getMessage() {
		return "Missing required option '" + option.getName() + "'";
	}

}
