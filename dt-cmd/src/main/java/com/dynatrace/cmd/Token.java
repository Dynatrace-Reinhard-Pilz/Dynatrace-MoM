package com.dynatrace.cmd;

public interface Token {

	boolean equals(String arg);
	
	boolean matches(String arg);

}
