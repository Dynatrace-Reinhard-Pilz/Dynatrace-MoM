package com.dynatrace.profiles;

import com.dynatrace.utils.SizedIterable;

public interface ProfileCollection extends SizedIterable<SystemProfile> {

	SystemProfile get(String profileName);
	SystemProfile getSelfMonitoringProfile();
	void addAll(Iterable<SystemProfile> systemProfiles);
	void add(SystemProfile systemProfile);
	
}
