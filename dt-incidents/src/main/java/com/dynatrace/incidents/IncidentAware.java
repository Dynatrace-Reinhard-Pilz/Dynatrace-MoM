package com.dynatrace.incidents;

public interface IncidentAware {

	IncidentRule getIncidentRule(String name);
	void refreshIncidentReferences();
}
