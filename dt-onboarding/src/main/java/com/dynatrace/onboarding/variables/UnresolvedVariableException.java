package com.dynatrace.onboarding.variables;

public class UnresolvedVariableException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	private final String variableName;
	
	public UnresolvedVariableException(String variableName) {
		this.variableName = variableName;
	}
	
	@Override
	public String getMessage() {
		return "The variable {" + variableName + "} needs to get defined in order to resolve this resource";
	}
	
	public String getVariable() {
		return variableName;
	}
}
