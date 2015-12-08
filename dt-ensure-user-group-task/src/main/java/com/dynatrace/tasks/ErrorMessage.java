package com.dynatrace.tasks;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ErrorMessage implements CharSequence {
	
	private final StringWriter sw = new StringWriter();
	
	public void println(String s) {
		try (PrintWriter pw = new PrintWriter(sw)) {
			pw.println(s);
		}
	}
	
	public void println(Throwable t) {
		StackTraceElement[] stackTrace = t.getStackTrace();
		for (StackTraceElement stackTraceElement : stackTrace) {
			println(stackTraceElement.toString());
		}
	}

	@Override
	public int length() {
		return sw.getBuffer().length();
	}

	@Override
	public char charAt(int index) {
		return sw.getBuffer().charAt(index);
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		return sw.getBuffer().subSequence(start, end);
	}
	
	@Override
	public String toString() {
		return sw.getBuffer().toString();
	}
	
	public boolean isEmpty() {
		return length() == 0;
	}

}
