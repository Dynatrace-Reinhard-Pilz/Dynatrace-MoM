package com.dynatrace.profiles;

import com.dynatrace.utils.Batch;

public interface ProfileCollection extends Batch<SystemProfile> {

	SystemProfile get(String profileName);
	SystemProfile getSelfMonitoringProfile();
	void addAll(Iterable<SystemProfile> systemProfiles);
	void add(SystemProfile systemProfile);
	
}
