package com.dynatrace.variables;

/**
 * Signals that in order to resolve a {@code variable} the resolution of
 * another {@code variable} is required, but that resolution failed.
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 */
public final class UnresolvedVariableException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	private final String variableName;
	
	/**
	 * c'tor
	 * 
	 * @param variableName the {@code name} of the {@code variable} that could
	 * 		not get resolved
	 */
	public UnresolvedVariableException(String variableName) {
		this.variableName = variableName;
	}
	
	/**
	 * @return the {@code name} of the {@code variable} that could not get
	 * 		resolved.
	 */
	public String getVariable() {
		return variableName;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getMessage() {
		return "The variable {" + variableName + "} needs to get defined in order to resolve this resource";
	}
	
}
