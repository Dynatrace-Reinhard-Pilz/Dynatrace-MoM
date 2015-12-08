package com.dynatrace.utils;

public interface SizedIterable<T> extends Iterable<T> {

	int size();
	
}
