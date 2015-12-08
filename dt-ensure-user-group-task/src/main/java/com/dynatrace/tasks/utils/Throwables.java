package com.dynatrace.tasks.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Utility class for dealing with {@link Exception}s and {@link Throwable}s in
 * general.
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 */
public final class Throwables {

	/**
	 * Returns the Stacktrace of the given {@link Throwable} or an empty
	 * {@link String} if it is {@code null}.
	 *  
	 * @param thrown the {@link Throwable} to get the Stacktrace for
	 * 
	 * @return the Stacktrace as String for the given {@link Throwable} or
	 * 		an empty {@link String} if it is {@code null}.
	 */
	public static String toString(Throwable thrown) {
		if (thrown == null) {
			return Strings.EMPTY;
		}
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		try {
			thrown.printStackTrace(pw);
		} catch (Throwable t) {
			Closeables.closeQuietly(sw);
			Closeables.closeQuietly(pw);
			return t.getClass().getName();
		}
		Closeables.closeQuietly(sw);
		Closeables.closeQuietly(pw);
		return sw.getBuffer().toString();
	}
	
}
