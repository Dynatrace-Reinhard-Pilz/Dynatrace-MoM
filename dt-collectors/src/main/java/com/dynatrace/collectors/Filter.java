package com.dynatrace.collectors;

public interface Filter<T> {

	boolean accept(T t);
}
