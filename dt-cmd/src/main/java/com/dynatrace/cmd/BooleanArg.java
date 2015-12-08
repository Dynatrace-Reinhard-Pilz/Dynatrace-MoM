package com.dynatrace.cmd;


public class BooleanArg extends Option<Boolean> {
	
	private Boolean value = null;

	public BooleanArg(String name) {
		super(name);
	}

	@Override
	public Boolean getValue() {
		return value;
	}

	@Override
	public boolean offer(String arg) throws InvalidOptionValueException {
		if (value != null) {
			return false;
		}
		value = Boolean.parseBoolean(arg);
		return true;
	}

	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub
		return false;
	}


}
