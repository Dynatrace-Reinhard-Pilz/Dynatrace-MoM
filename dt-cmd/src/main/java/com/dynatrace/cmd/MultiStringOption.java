package com.dynatrace.cmd;

import java.util.ArrayList;
import java.util.Collection;

public class MultiStringOption extends Option<Collection<String>> {
	
	private Collection<String> values = null;

	public MultiStringOption(String name) {
		super(name);
	}
	
	public MultiStringOption(String name, boolean isRequired) {
		super(name, isRequired);
	}

	@Override
	public Collection<String> getValue() {
		return values;
	}

	@Override
	public boolean offer(String arg) throws InvalidOptionValueException {
		if (values == null) {
			values = new ArrayList<String>(0);
		}
		values.add(arg);
		return true;
	}

	@Override
	public boolean isValid() {
		return (values != null);
	}

}
