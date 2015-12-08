package com.dynatrace.cmd;

public class AnyArgument implements ArgumentConsumer {

	public boolean matches(String arg) {
		return true;
	}

	public ArgumentConsumer or(ArgumentConsumer c) {
		return null;
	}

}
