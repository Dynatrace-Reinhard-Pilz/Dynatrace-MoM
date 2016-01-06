package com.dynatrace.utils;

import java.util.Iterator;

/**
 * An {@link Iterable} that is able to provide information about the number
 * of elements to expect when iterating over it without actually having to
 * call {@link Iterable#iterator()} and afterwards {@link Iterator#hasNext()}.
 * 
 * @author reinhard.pilz@dynatrace.com
 * 
 * @param <T> the type of elements returned by the iterator
 */
public interface SizedIterable<T> extends Iterable<T> {

	int size();
	
}
