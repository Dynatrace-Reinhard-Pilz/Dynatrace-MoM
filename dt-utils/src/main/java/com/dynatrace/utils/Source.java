package com.dynatrace.utils;

import java.io.IOException;
import java.io.InputStream;

public interface Source<T extends Source<T>> {

	String getId();
	String getName();
	InputStream openStream() throws IOException;
	long length();
	long lastModified();
	T localize() throws IOException;
	
}
