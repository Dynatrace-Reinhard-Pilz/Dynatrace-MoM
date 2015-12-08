package com.dynatrace.onboarding.variables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dynatrace.onboarding.config.Config;

public class Variables {
	
	private static final Logger LOGGER =
			Logger.getLogger(Variables.class.getName());

	private final Map<String, String> variables = new HashMap<>();
	
	private final Properties properties;
	
	public Variables(Properties properties) {
		this.properties = properties;
	}
	
	public Variables() {
		this((String) null);
	}
	
	public Variables(String profileName) {
		this(Config.properties());
		variables.put("sessionid", profileName);
		for (Object key : properties.keySet()) {
			String name = key.toString();
			if (!name.startsWith("variable.")) {
				continue;
			}
			String value = properties.getProperty(name);
			String variable = name.substring("variable.".length());
			LOGGER.log(Level.FINEST, "put(" + variable + ", " + value + ")");
			variables.put(variable, value);
		}
	}
	
	public String get(String name) {
		if (name == null) {
			return null;
		}
		synchronized (variables) {
			String value = variables.get(name);
			if (value == null) {
				variables.put(name, name);
			}
			if (name.equals(value)) {
				return null;
			}
			return value;
		}
	}
	
	public void put(String name, String value) {
		synchronized (variables) {
			variables.put(name, value);
		}
	}
	
	public void remove(String name) {
		synchronized (variables) {
			variables.remove(name);
		}
	}
	
	public boolean isResolved() {
		synchronized (variables) {
			for (String name : variables.keySet()) {
				String value = variables.get(name);
				if ((value == null) || value.equals(name)) {
					return false;
				}
			}
		}
		return true;
	}
	
	public Iterable<String> getUnresolved() {
		Collection<String> unresolved = new ArrayList<>();
		synchronized (variables) {
			for (String name : variables.keySet()) {
				String value = variables.get(name);
				if ((value == null) || value.equals(name)) {
					unresolved.add(name);
				}
			}
		}
		return unresolved;
	}
	
	public static Iterable<String> getVariables(String s) {
		if (s == null) {
			return null;
		}
		Collection<String> variables = new HashSet<>();
		String text = s;
		int startIdx = text.indexOf("{@");
		while (startIdx >= 0) {
			int endIdx = text.indexOf('}', startIdx);
			if (endIdx < 0) {
				return variables;
			}
			String token = text.substring(startIdx + 2, endIdx).trim();
			variables.add(token);
			String before = text.substring(0, startIdx);
			String after = text.substring(endIdx + 1);
			text = before + token + after;
			startIdx = text.indexOf("{@");
		}
		return variables;
	}
	
	public String resolve(String s) throws UnresolvedVariableException {
		if (s == null) {
			return null;
		}
		String text = s;
		int startIdx = text.indexOf("{@");
		while (startIdx >= 0) {
			int endIdx = text.indexOf('}', startIdx);
			if (endIdx < 0) {
				return text;
			}
			String token = text.substring(startIdx + 2, endIdx);
			String resolvedToken = get(token);
			if (resolvedToken == null) {
				throw new UnresolvedVariableException(token);
			}
			String before = text.substring(0, startIdx);
			String after = text.substring(endIdx + 1);
			text = before + resolvedToken + after;
			startIdx = text.indexOf("{@");
		}
		return text;
	}
}
