package com.dynatrace.cmd;

public interface ArgumentConsumer {

	boolean matches(String arg);
	ArgumentConsumer or(ArgumentConsumer c);
	
}
