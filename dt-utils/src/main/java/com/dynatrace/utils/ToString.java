package com.dynatrace.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public final class ToString {

	private final StringBuilder sb = new StringBuilder();
	private final boolean ignoreNull;
	private boolean isAwaitingFirstValue = true;
	
	public static String toString(Throwable t) {
		try (
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
		) {
			t.printStackTrace(pw);
			pw.flush();
			sw.flush();
			return sw.getBuffer().toString();
		} catch (IOException e) {
			// ignore
			return t.getClass().getName();
		}
	}
	
	public ToString(final Object o) {
		this(o, true);
	}
	
	public ToString(final Object o, final boolean ignoreNull) {
		this.ignoreNull = ignoreNull;
		if (o == null) {
			sb.append("null");
		}
	}
	
	public final ToString append(final Object value) {
		if (ignoreNull && (value == null)) {
			return this;
		}
		if (isAwaitingFirstValue) {
			sb.append("[");
			isAwaitingFirstValue = false;
		} else {
			sb.append(", ");
		}
		appendValue(value);
		return this;
	}
	
	public final ToString append(final String name, final Object value) {
		if (name == null) {
			return this;
		}
		if (ignoreNull && (value == null)) {
			return this;
		}
		if (isAwaitingFirstValue) {
			sb.append("[");
			isAwaitingFirstValue = false;
		} else {
			sb.append(", ");
		}
		sb.append(name).append("=");
		appendValue(value);
		return this;
	}
	
	private final void appendValue(final Object value) {
		if (value instanceof CharSequence) {
			sb.append('"').append(value).append('"');
		} else if (value == null) {
			sb.append("null");
		} else {
			sb.append(value.toString());
		}
	}
	
	/**
	 * @return the currently appended properties to include into the
	 * 		toString-representation of the object to stringify
	 */
	@Override
	public final String toString() {
		if (isAwaitingFirstValue) {
			return sb.toString();
		}
		return new StringBuilder(sb.toString()).append("]").toString();
	}
}
