package com.dynatrace.utils;

import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * 
 * @author Reinhard Pilz
 *
 */
public class DefaultExecutionContext extends AbstractExecutionContext {
	
	private static final Logger LOGGER =
			Logger.getLogger(DefaultExecutionContext.class.getName());
	
	private final HashMap<String, Object> attributes =
			new HashMap<String, Object>();
	
	@Override
	public <T> T getAttribute(String name) {
		Objects.requireNonNull(name);
		return Unchecked.cast(attributes.get(name));
	}

	@Override
	public void setAttribute(String name, Object attribute) {
		Objects.requireNonNull(name);
		attributes.put(name,  attribute);
	}
	
	@Override
	public void removeAttribute(String name) {
		Objects.requireNonNull(name);
		attributes.remove(name);
	}
	
	@Override
	public String getContextPath() {
		return "mom";
	}
	
	protected Logger logger() {
		return LOGGER;
	}

	@Override
	public void execute(Runnable command) {
		command.run();
	}

}
