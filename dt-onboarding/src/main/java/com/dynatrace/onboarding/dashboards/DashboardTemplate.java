package com.dynatrace.onboarding.dashboards;

import java.io.IOException;

import com.dynatrace.onboarding.variables.DefaultVariables;
import com.dynatrace.utils.VersionedSource;
import com.dynatrace.variables.UnresolvedVariableException;

public interface DashboardTemplate extends VersionedSource<DashboardTemplate> {

	String getKey();
	Dashboard resolve(DefaultVariables variables) throws IOException, UnresolvedVariableException;
	
}
