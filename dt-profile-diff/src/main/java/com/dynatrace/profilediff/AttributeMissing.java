package com.dynatrace.profilediff;

public class AttributeMissing extends Difference {

	private final String name;
	
	public AttributeMissing(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + getName() + "]";
	}
}
