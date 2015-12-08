package com.dynatrace.utils;

import java.io.Closeable;
import java.util.Objects;

/**
 * Utility class to temporarily change a System Property and restore it later on
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 */
public final class SysProp implements Closeable {

	/**
	 * The key of the System Property
	 */
	private final String key;
	
	/**
	 * The value of the System Property at the time of the creation of this
	 * object.
	 */
	private final String value;
	
	/**
	 * c'tor
	 * 
	 * @param key the key of the System Property
	 * 
	 * @throws NullPointerException if the given key is {@code null}
	 */
	public SysProp(String key) {
		Objects.requireNonNull(key);
		this.key = key;
		value = System.getProperty(key);
	}
	
	/**
	 * Restores to value of the System Property to the value it was set to at
	 * the time this object has been created or removes the System Property
	 * if it did not exist before this object has been created.
	 */
	public void restore() {
		if (this.value == null) {
			System.clearProperty(key);
		} else {
			System.setProperty(key, value);
		}
	}
	
	/**
	 * Changes the value of the System Property
	 * 
	 * @param value the new value of the System Property
	 */
	public void set(String value) {
		if (value == null) {
			System.clearProperty(key);
		} else {
			System.setProperty(key, value);
		}
	}

	/**
	 * Restores to value of the System Property to the value it was set to at
	 * the time this object has been created or removes the System Property
	 * if it did not exist before this object has been created.
	 */
	@Override
	public void close() {
		restore();
	}
}
