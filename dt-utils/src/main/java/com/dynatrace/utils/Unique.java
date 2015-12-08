package com.dynatrace.utils;

/**
 * {@link Class}es implementing this interface are required to provide a unique
 * identifier for every instance.<br />
 * <br />
 * Failing to do provide a really unique identifier as return value for 
 * {@link #getId()} will result in not being handled properly when being used
 * with {@link Iterables#asMap(Iterable)}.
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 * @param <T> the type of {@link Object} representing the unique identifier for
 * 		this implementation of {@link Unique}.
 */
public interface Unique<T> {

	/**
	 * @return a unique identifier for this object
	 */
	T getId();
	
}
