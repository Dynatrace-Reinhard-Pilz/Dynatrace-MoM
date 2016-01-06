package com.dynatrace.variables;

/**
 * An object implementing {@link Variables} are able to provide a value for
 * a given {@code variable}.
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 */
public interface Variables {

	/**
	 * Provides a value for a {@code variable} with the given {@code name}.
	 * <br />
	 * <br />
	 * If there is simply no value available for a {@code variable} with the
	 * given {@code name}, the method is bound to return {@code null}.<br />
	 * <br />
	 * In case another {@code variable} needs to be resolved (but cannot) in
	 * order to resolve the {@code variable} with the given {@code name}, a
	 * {@link UnresolvedVariableException} will get thrown. This is however only
	 * the case for {@code variable}s whichs {@code name}s DIFFER from the
	 * {@code variable} that is supposed to get resolved by this call.
	 *  
	 * @param name the {@code name} of the {@code variable} for which to resolve
	 * 		a {@code value}.
	 * 
	 * @return a {@code value} for the {@code variable} to resolve or
	 * 		{@code null} if no value can be resolved for it
	 * 
	 * @throws UnresolvedVariableException if the resolution of the
	 * 		{@code variable} with the given {@code name} requires the resolution
	 * 		of another {@code variable} which cannot get resolved
	 */
	String get(String name) throws UnresolvedVariableException;
}
