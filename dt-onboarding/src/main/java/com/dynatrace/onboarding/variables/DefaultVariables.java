package com.dynatrace.onboarding.variables;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dynatrace.onboarding.config.Config;
import com.dynatrace.pluggability.PluginManager;
import com.dynatrace.utils.Strings;
import com.dynatrace.variables.UnresolvedVariableException;
import com.dynatrace.variables.VariableResolver;
import com.dynatrace.variables.Variables;

public class DefaultVariables implements Variables {
	
	private static final Logger LOGGER =
		Logger.getLogger(DefaultVariables.class.getName());

	private final Map<String, Object> variables = new HashMap<>();
	
	private final Properties properties;
	private final static PluginManager PLUGINMGR = PluginManager.get(
		DefaultVariables.class
	);
	private final static Iterable<VariableResolver> RESOLVERS = findResolvers();
	
	private static Iterable<VariableResolver> findResolvers() {
		Class<? extends VariableResolver>[] classes =
				PLUGINMGR.getImplementors(VariableResolver.class);
		Collection<VariableResolver> resolvers = new ArrayList<>();
		for (Class<? extends VariableResolver> clazz : classes) {
			VariableResolver resolver = createResolver(clazz);
			if (resolver != null) {
				LOGGER.log(
					Level.INFO,
					"Registering " + resolver.getClass().getSimpleName() +
					" for variable '" +	resolver.getVariableName() + "'"
				);
				resolvers.add(resolver);
			}
		}
		return resolvers;
	}
	
	private static VariableResolver createResolver(
		Class<? extends VariableResolver> clazz
	) {
		if (clazz == null) {
			return null;
		}
		int modifiers = clazz.getModifiers();
		if (Modifier.isAbstract(modifiers)) {
			return null;
		}
		if (Modifier.isInterface(modifiers)) {
			return null;
		}
		try {
			Constructor<? extends VariableResolver> ctor =
					clazz.getDeclaredConstructor(new Class<?>[0]);
			ctor.setAccessible(true);
			VariableResolver resolver = ctor.newInstance();
			String variableName = resolver.getVariableName();
			if (Strings.isNullOrEmpty(variableName)) {
				LOGGER.log(
					Level.WARNING,
					clazz.getName() + " is not a valid " +
					VariableResolver.class.getSimpleName() +
					". Invalid variable name published"
				);
				return null;
			}
			return resolver;
		} catch (Throwable t) {
			LOGGER.log(
				Level.WARNING,
				"Unable to create instance of " + clazz.getName()
			);
			return null;
		}
		
	}
	
	public DefaultVariables(Properties properties) {
		this.properties = properties;
	}
	
	public DefaultVariables() {
		this((String) null);
	}
	
	public DefaultVariables(String profileName) {
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
		for (VariableResolver resolver : RESOLVERS) {
			if (resolver == null) {
				continue;
			}
			String variableName = resolver.getVariableName();
			if (Strings.isNullOrEmpty(variableName)) {
				continue;
			}
			put(variableName, resolver);
		}
	}
	
	@Override
	public String get(String name) throws UnresolvedVariableException {
		if (name == null) {
			return null;
		}
		Object value = null;
		synchronized (variables) {
			value = variables.get(name);
			if (value == null) {
				variables.put(name, name);
			}
		}
		if (name.equals(value)) {
			value = null;
		}
		if (value instanceof VariableResolver) {
			VariableResolver resolver = (VariableResolver) value;
			Thread currentThread = Thread.currentThread();
			ClassLoader ccl = currentThread.getContextClassLoader();
			ClassLoader resolverClassLoader =
				resolver.getClass().getClassLoader();
			if (resolverClassLoader != null) {
				currentThread.setContextClassLoader(resolverClassLoader);
			}
			try {
				return resolver.resolve(this);
			} catch (Throwable t) {
				LOGGER.log(
					Level.WARNING,
					"Variable Resolver '" + resolver.getClass().getName() +
						"' for variable '" + resolver.getVariableName() +
						"' failed.",
					t);
				return null;
			} finally {
				currentThread.setContextClassLoader(ccl);
			}
		}
		if (value == null) {
			return null;
		}
		return value.toString();
	}
	
	public void put(String name, String value) {
		synchronized (variables) {
			variables.put(name, value);
		}
	}
	
	private void put(String name, VariableResolver resolver) {
		synchronized (variables) {
			variables.put(name, resolver);
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
				Object value = variables.get(name);
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
				Object value = variables.get(name);
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
		Collection<String> dashVariables = getDashVariables(s);
		if (dashVariables != null) {
			variables.addAll(dashVariables);
		}
		return variables;
	}
	
	private static boolean isDash(String text, int idx) {
		if (text == null) {
			return false;
		}
		if (idx < 0) {
			return false;
		}
		if (idx == text.length()) {
			return false;
		}
		if (text.charAt(idx) == '-') {
			return true;
		}
		return false;
	}
	
	private static Collection<String> getDashVariables(String s) {
		if (s == null) {
			return null;
		}
		Collection<String> variables = new HashSet<>();
		String text = s;
		int startIdx = text.indexOf("---");
		while (startIdx >= 0) {
			while (isDash(text, startIdx + 3)) {
				startIdx++;
			}
			int endIdx = text.indexOf("---", startIdx + 1);
			if (endIdx < 0) {
				return variables;
			}
			String token = text.substring(startIdx + 3, endIdx).trim();
			variables.add(token);
			String before = text.substring(0, startIdx);
			String after = text.substring(endIdx + 3);
			text = before + token + after;
			startIdx = text.indexOf("---");
		}
		return variables;
	}
	
	public String resolve(String s) throws UnresolvedVariableException {
		return resolveDash(resolveClassic(s));
	}
	
	private String resolveClassic(String s) throws UnresolvedVariableException {
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
	
	private String resolveDash(String s) throws UnresolvedVariableException {
		if (s == null) {
			return null;
		}
		String text = s;
		int startIdx = text.indexOf("---");
		while (startIdx >= 0) {
			while (isDash(text, startIdx + 3)) {
				startIdx++;
			}
			int endIdx = text.indexOf("---", startIdx + 1);
			if (endIdx < 0) {
				return text;
			}
			String token = text.substring(startIdx + 3, endIdx);
			String resolvedToken = get(token);
			if (resolvedToken == null) {
				throw new UnresolvedVariableException(token);
			}
			String before = text.substring(0, startIdx);
			String after = text.substring(endIdx + 3);
			text = before + resolvedToken + after;
			startIdx = text.indexOf("---");
		}
		return text;
	}
}
