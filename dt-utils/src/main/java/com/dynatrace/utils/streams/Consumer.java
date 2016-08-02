package com.dynatrace.utils.streams;

import java.io.IOException;

public interface Consumer<T> {

	void consume(T t) throws IOException;
	
}
