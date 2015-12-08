package com.dynatrace.collectors;

public enum RestartStatus {

	NONE,
	REQUIRED,
	SCHEDULED,
	INPROGRESS;
	
	public static boolean isRestarting(RestartStatus status) {
		return (status == SCHEDULED) || (status == INPROGRESS);
	}
}
