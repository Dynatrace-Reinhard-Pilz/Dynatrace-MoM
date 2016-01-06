package com.dynatrace.variables;

/**
 * Objects implementing {@link VariableResolver} are able to provide a
 * {@code value} for a {@code variable} with one specific {@code name}.
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 */
public interface VariableResolver {

	/**
	 * @return the {@code name} of the {@code variable} this
	 * 		{@link VariableResolver} is able to provide {@code value}s for
	 */
	String getVariableName();
	
	/**
	 * Provides a {@code value} for the {@code variable} with the {@code name}
	 * specified by {@link #getVariableName()}.
	 * 
	 * @param variables in case the resolution of the {@code variable} depends
	 * 		on the resolution of other {@code variable}s, this object offers
	 * 		values for them.
	 * 
	 * @return {@code null} if there is currently no value available for the
	 * 		{@code variable} with the name specified by
	 * 		{@link #getVariableName()} or a non empty {@link String}.
	 * 
	 * @throws UnresolvedVariableException if providing a {@code value} for the
	 * 		variable with the name specified by {@link #getVariableName()}
	 * 		depends on the resolution of at least one other {@code variable},
	 * 		but resolution was not possible by querying the given
	 * 		{@link Variables} object.
	 * 
	 * @see {@link Variables#get(String)}
	 */
	String resolve(Variables variables) throws UnresolvedVariableException;

}
