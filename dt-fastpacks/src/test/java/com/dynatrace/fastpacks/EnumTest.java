package com.dynatrace.fastpacks;

import java.lang.reflect.Method;

import org.junit.Test;

public abstract class EnumTest<T extends Enum<T>> {
	
	private static final String METHOD_NAME_VALUES = "values";
	private static final String METHOD_NAME_VALUEOF = "valueOf";
	
	private static final Class<?>[] NO_ARGS = null;
	private static final Object[] NO_PARAMS = null;
	
	@Test
	public void testEnsureCoverage() throws Exception {
		Class<T> enumClass = getEnumClass();
		Method mValues = enumClass.getMethod(METHOD_NAME_VALUES, NO_ARGS);
		Object[] values = (Object[]) mValues.invoke(NO_PARAMS);
		for (Object o : values) {
			Method mValueOf = enumClass.getMethod(
				METHOD_NAME_VALUEOF,
				String.class
			);
			mValueOf.invoke(null, o.toString());
		}
	}
	
	protected abstract Class<T> getEnumClass();
}
