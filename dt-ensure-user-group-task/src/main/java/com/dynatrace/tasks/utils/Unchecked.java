package com.dynatrace.tasks.utils;

/**
 * Utility for performing unchecked casts. Use with caution.
 *  
 * @author reinhard.pilz@dynatrace.com
 *
 */
public final class Unchecked {

	/**
	 * Performs an unchecked cast to any object you want avoiding a compiler
	 * warning.<br />
	 * <br />
	 * Only use in situations where it is perfectly clear that this cast may
	 * be performed unchecked.
	 * 
	 * @param o the {@link Object} to cast
	 * 
	 * @return the given {@link Object} but cast to the class the result of
	 * 		this invocation expects.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T cast(Object o) {
		if (o == null) {
			return null;
		}
        return (T) o;		
	}

}
