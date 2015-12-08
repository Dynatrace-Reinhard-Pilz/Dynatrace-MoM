package com.dynatrace.cmd;

import java.util.Objects;


public class Options {

	private final Option<?>[] options;
	
	public Options(Option<?>... options) {
		this.options = options;
	}

	public void consume(String[] args) throws CommandLineException {
		Objects.requireNonNull(args);
		Option<?> currentOption = null;
		for (String arg : args) {
			if (arg == null) {
				throw new IllegalArgumentException(
					"none of the argument values is allowed to be null"
				);
			}
			Option<?> option = match(options, arg);
			if (option == null) {
				if (currentOption == null) {
					throw new UnknownOptionException(arg);
				} else if (!currentOption.offer(arg)) {
					throw new UnknownOptionException(arg);
				}
			}
			if ((currentOption != null) && !currentOption.isValid()) {
				throw new MissingOptionValueException(currentOption);
			}
			currentOption = option;
		}
		for (Option<?> option : options) {
			if (option.isRequired() && !option.isValid()) {
				throw new MissingOptionException(option);
			}
		}
	}
	
	public static Option<?> match(Option<?>[] options, String arg) {
		if (arg == null) {
			return null;
		}
		if (options == null) {
			return null;
		}
		for (Option<?> argument : options) {
			if (Option.matches(argument, arg)) {
				return argument;
			}
		}
		return null;
	}
	
	
}
