package com.dynatrace.cmd;

import java.util.Objects;

import com.dynatrace.utils.Strings;

public abstract class Option<T> {

	private final String name;
	private final boolean isRequired;
	
	public Option(String name) {
		this(name, false);
	}
	
	public Option(String name, boolean isRequired) {
		Objects.requireNonNull(name);
		this.name = name;
		this.isRequired = isRequired;
	}
	
	public final String getName() {
		return name;
	}
	
	public abstract T getValue();
	
	public abstract boolean offer(String arg)
		throws InvalidOptionValueException;
	
	public abstract boolean isValid();
	
	public final boolean isRequired() {
		return isRequired;
	}
	
	public boolean matches(String name) {
		return Strings.equals(this.name, name);
	}
	
	public static boolean matches(Option<?> argument, String name) {
		if (argument == null) {
			return false;
		}
		return argument.matches(name);
	}
	
	@Override
	public final String toString() {
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Option<?> other = (Option<?>) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
}
