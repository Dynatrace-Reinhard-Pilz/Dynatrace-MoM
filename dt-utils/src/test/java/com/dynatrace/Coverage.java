package com.dynatrace;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Assert;
import org.junit.Test;

import com.dynatrace.utils.Throwables;
import com.dynatrace.utils.Unchecked;

/**
 * Base class for unit tests in order to achieve 100% code coverage
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 * @param <T> the {@link Class} the unit test derived from this abstract
 * 		class is trying to reach 100% code coverage for.
 */
public abstract class Coverage<T> {
	
	/**
	 * Indicating that a method does not require any arguments
	 */
	protected static final Class<?>[] NO_ARGS = null;
	
	/**
	 * Indicating that no arguments are getting passed to a method
	 */
	protected static final Object[] NO_PARAMS = null;

	/**
	 * Invokes the Default Constructor of the {@link Class} returned by
	 * {@link #getCoverageClass()} if it exists.<br />
	 * <br />
	 * This test is not supposed to fail. Instead it is simply trying to
	 * instantiate the class in order to reach 100% code coverage. 
	 */
	@Test
	public void testConstructor() {
		Class<T> clazz = getCoverageClass();
		if (clazz == null) {
			return;
		}
		try {
			Constructor<?> ctor = clazz.getDeclaredConstructor(NO_ARGS);
			ctor.setAccessible(true);
			ctor.newInstance(NO_PARAMS);
		} catch (Error | Exception e) {
			Assert.fail();
		}
	}
	
	protected abstract Class<T> getCoverageClass();
	
	/**
	 * Invokes a method on a given object using Reflection, assuming that such a
	 * method indeed exists and it does not require any arguments.<br />
	 * <br />
	 * In case no method with the given name can be found an
	 * {@link IllegalArgumentException} will be thrown, saving the caller the
	 * hassle to catch the usual {@link NoSuchMethodException} and
	 * {@link SecurityException}.
	 * 
	 * @param o the object to invoke the method on or {@code null} if it is
	 * 		a static method.
	 * @param method the name of the method to invoke.
	 * 
	 * @return the return value of the invocation
	 * 
	 * @throws InvocationTargetException wraps any exception thrown by the
	 * 		invoked method
	 */
	public static Object invoke(Object o, String method)
		throws InvocationTargetException
	{
		return invoke(o, method, NO_ARGS, NO_PARAMS);
	}
	
	/**
	 * Invokes a method on a given object using Reflection, assuming that such a
	 * method indeed exists and fits the signature described by the given
	 * argument types.<br />
	 * <br />
	 * In case the signature is not correct or no method with the given name
	 * can be found an {@link IllegalArgumentException} will be thrown, saving
	 * the caller the hassle to catch the usual {@link NoSuchMethodException}
	 * and {@link SecurityException}.
	 * 
	 * @param o the object to invoke the method on or {@code null} if it is
	 * 		a static method.
	 * @param method the name of the method to invoke.
	 * @param argTypes the argument types defining the method signature or
	 * 		{@code null} if the method does not require any arguments.
	 * @param args the arguments to pass when invoking the method or
	 * 		{@code null} if the method does not require any arguments.
	 * 
	 * @return the return value of the invocation
	 * 
	 * @throws InvocationTargetException wraps any exception thrown by the
	 * 		invoked method
	 */
	public static Object invoke(
			Object o, String method, Class<?>[] argTypes, Object... args
	)
		throws InvocationTargetException
	{
		if (o == null) {
			throw new NullPointerException("object must not be null");
		}
		return invoke(o.getClass(), o, method, argTypes, args);
	}
	
	/**
	 * Invokes a static method on the given {@link Class} using Reflection,
	 * assuming that such a method indeed exists and does not require any
	 * arguments.<br />
	 * <br />
	 * In case no method with the given name can be found an
	 * {@link IllegalArgumentException} will be thrown, saving the caller the
	 * hassle to catch the usual {@link NoSuchMethodException} and
	 * {@link SecurityException}.
	 * 
	 * @param clazz the class to invoke the method on
	 * @param method the name of the method to invoke
	 * 
	 * @return the return value of the invocation
	 * 
	 * @throws InvocationTargetException wraps any exception thrown by the
	 * 		invoked method
	 */
	public static Object invoke(Class<?> clazz, String method)
		throws InvocationTargetException
	{
		return invoke(clazz, null, NO_ARGS, NO_PARAMS);
	}
	
	/**
	 * Invokes a static method on the given {@link Class} using Reflection,
	 * assuming that such a method indeed exists and fits the signature
	 * described by the given argument types.<br />
	 * <br />
	 * In case the signature is not correct or no method with the given name
	 * can be found an {@link IllegalArgumentException} will be thrown, saving
	 * the caller the hassle to catch the usual {@link NoSuchMethodException}
	 * and {@link SecurityException}.
	 * 
	 * @param clazz the class to invoke the method on
	 * @param method the name of the method to invoke
	 * @param argTypes the argument types defining the method signature or
	 * 		{@code null} if the method does not require any arguments.
	 * @param args the arguments to pass when invoking the method or
	 * 		{@code null} if the method does not require any arguments.
	 * 
	 * @return the return value of the invocation
	 * 
	 * @throws InvocationTargetException wraps any exception thrown by the
	 * 		invoked method
	 */
	public static Object invoke(
		Class<?> clazz,
		String method,
		Class<?>[] argTypes,
		Object... args
	)
		throws InvocationTargetException
	{
		return invoke(clazz, null, method, argTypes, args);
	}

	/**
	 * Invokes a method on a given object using Reflection, assuming that such a
	 * method indeed exists and fits the signature described by the given
	 * argument types.<br />
	 * <br />
	 * In case the signature is not correct or no method with the given name
	 * can be found an {@link IllegalArgumentException} will be thrown, saving
	 * the caller the hassle to catch the usual {@link NoSuchMethodException}
	 * and {@link SecurityException}.
	 * 
	 * @param clazz the {@link Class} defining the method.
	 * @param o the object to invoke the method on or {@code null} if it is
	 * 		a static method.
	 * @param method the name of the method to invoke.
	 * @param argTypes the argument types defining the method signature or
	 * 		{@code null} if the method does not require any arguments.
	 * @param args the arguments to pass when invoking the method or
	 * 		{@code null} if the method does not require any arguments.
	 * 
	 * @return the return value of the invocation
	 * 
	 * @throws InvocationTargetException wraps any exception thrown by the
	 * 		invoked method
	 */
	public static <T> Object invoke(
		Class<? extends T> clazz,
		T o, String method,
		Class<?>[] argTypes,
		Object... args
	)
		throws InvocationTargetException
	{
		if (method == null) {
			throw new NullPointerException("method name must not be null");
		}
		Method m;
		try {
			m = clazz.getDeclaredMethod(method, argTypes);
		} catch (NoSuchMethodException | SecurityException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
		m.setAccessible(true);
		try {
			return m.invoke(o, args);
		} catch (IllegalAccessException | IllegalArgumentException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}
	
	/**
	 * Convenience method to create an array of {@link Class} objects.
	 * 
	 * @param classes the classes to have as an array
	 * 
	 * @return an array containing all the classes
	 */
	public static Class<?>[] sig(Class<?>... classes) {
		return classes;
	}
	
	/**
	 * Queries for the {@link Throwable} the given
	 * {@link InvocationTargetException} is wrapping. If it is either an
	 * {@link Error} or a {@link RuntimeException} it will immediately throw
	 * it. Otherwise it returns the wrapped {@link Exception}.
	 * 
	 * @param e the {@link InvocationTargetException} to check for the
	 * 		{@link Throwable} it is wrapping.
	 * 
	 * @return the wrapped {@link Throwable} unless it is either a 
	 * 		{@link RuntimeException} or an {@link Error}.
	 */
	public static Throwable throwRuntime(InvocationTargetException e) {
		if (e == null) {
			return null;
		}
		Throwable targetException = e.getTargetException();
		if (targetException instanceof Error) {
			throw (Error) targetException;
		}
		if (targetException instanceof RuntimeException) {
			throw (RuntimeException) targetException;
		}
		return targetException;
	}
	
	/**
	 * Queries for the value of the field with the given name within the given
	 * {@link Object}.<br />
	 * <br />
	 * In case the field does not exist or the field is not of the expected type
	 * this method immediately causes the test case to fail.
	 * 
	 * @param o the {@link Object} to query for the field value for
	 * @param name the name of the field
	 * 
	 * @return the value of the field
	 */
	public static <K> K getField(Object o, String name) {
		Assert.assertNotNull(o);
		Assert.assertNotNull(name);
		Class<? extends Object> clazz = o.getClass();
		Assert.assertNotNull(clazz);
		try {
			Field field = clazz.getDeclaredField(name);
			field.setAccessible(true);
			return Unchecked.cast(field.get(o));
		} catch (ClassCastException | IllegalAccessException| IllegalArgumentException | NoSuchFieldException | SecurityException e) {
			Assert.fail(Throwables.toString(e));
		}
		return null;
	}
	
}
