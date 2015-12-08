package com.dynatrace.cmd;

public class InvalidOptionValueException extends CommandLineException {

	private static final long serialVersionUID = 1L;
	
	private final Option<?> option;
	private final String value;
	
	public InvalidOptionValueException(Option<?> option, String value) {
		this.option = option;
		this.value = value;
	}
	
	@Override
	public String getMessage() {
		return "Invalid value '"
					+ value + "' for option '" + option.getName() + "'";
	}

}
