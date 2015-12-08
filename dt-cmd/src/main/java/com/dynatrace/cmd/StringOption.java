package com.dynatrace.cmd;


public class StringOption extends Option<String> {
	
	private String value = null;

	public StringOption(String name) {
		super(name, true);
	}
	
	public StringOption(String name, boolean isRequired) {
		super(name, isRequired);
	}
	
	public StringOption(String name, String defaultValue) {
		super(name, false);
		this.value = defaultValue;
	}
	
	@Override
	public final String getValue() {
		return value;
	}
	
	@Override
	public boolean offer(String value) throws InvalidOptionValueException {
		if (this.value != null) {
			return false;
		}
		this.value = value;
		return true;
	}

	@Override
	public boolean isValid() {
		return (value != null);
	}

}
