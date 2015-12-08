package com.dynatrace.reporting;

public interface HealthDashboardAware {

	void setHealthDashboardAvailability(Availability availability);
	Availability getHealthDashboardAvailability();
	
}
