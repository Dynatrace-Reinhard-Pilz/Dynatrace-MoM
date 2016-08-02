package com.dynatrace.utils;

import java.util.concurrent.TimeUnit;

public final class StopWatch {

	private long start;
	private final TimeUnit unit;

	public StopWatch() {
		this(TimeUnit.MILLISECONDS);
	}
	
	public StopWatch(TimeUnit unit) {
		start();
		this.unit = unit;
	}
	
	public void start() {
		this.start = System.nanoTime();
	}
	
	public long stop() {
		return unit.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS);
	}
}
