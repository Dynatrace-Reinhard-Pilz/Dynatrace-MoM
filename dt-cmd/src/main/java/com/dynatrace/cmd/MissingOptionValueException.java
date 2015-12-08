package com.dynatrace.cmd;

public class MissingOptionValueException extends CommandLineException {

	private static final long serialVersionUID = 1L;

	private final Option<?> option;
	
	public MissingOptionValueException(Option<?> option) {
		this.option = option;
	}
	
	@Override
	public String getMessage() {
		return "Value for option '" + option.getName() + "' missing";
	}

}
