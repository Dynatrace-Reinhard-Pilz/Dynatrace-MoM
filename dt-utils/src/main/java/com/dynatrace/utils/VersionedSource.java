package com.dynatrace.utils;

public interface VersionedSource<T extends VersionedSource<T>> extends Source<T> {

	Version getVersion();
	
}
