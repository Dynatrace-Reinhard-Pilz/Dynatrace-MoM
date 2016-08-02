package com.dynatrace.utils;

public final class Ref<T> {

	private T value = null;
	
	public void set(T value) {
		synchronized (this) {
			this.value = value;
		}
	}
	
	public T get() {
		synchronized (this) {
			return value;
		}
	}
}
