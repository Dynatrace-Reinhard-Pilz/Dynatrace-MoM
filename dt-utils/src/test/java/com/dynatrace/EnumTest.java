package com.dynatrace;

import java.lang.reflect.Method;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit Tests derived from this class are automatically getting granted
 * additional code coverage for the {@link Enum} they are representing.<br />
 * <br />
 * Because {@link Enum}s contain byte code which is normally not getting
 * invoked, the test methods in here ensure that even those remaining parts
 * are being covered.
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 * @param <T> the {@link Enum} class the Test Case is focusing on.
 */
public abstract class EnumTest<T extends Enum<T>> extends Coverage<T> {
	
	private static final String METHOD_NAME_VALUES = "values";
	private static final String METHOD_NAME_VALUEOF = "valueOf";
	
	@Test
	public void testEnsureCoverage() throws Exception {
		Class<T> enumClass = getCoverageClass();
		Assert.assertNotNull(enumClass);
		Method mValues = enumClass.getMethod(METHOD_NAME_VALUES, NO_ARGS);
		mValues.setAccessible(true);
		Object[] values = (Object[]) mValues.invoke(NO_PARAMS);
		for (Object o : values) {
			Method mValueOf = enumClass.getMethod(
				METHOD_NAME_VALUEOF,
				String.class
			);
			mValueOf.invoke(null, o.toString());
		}
	}
	
}
