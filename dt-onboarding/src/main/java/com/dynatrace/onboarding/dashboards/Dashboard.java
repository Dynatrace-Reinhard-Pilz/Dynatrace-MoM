package com.dynatrace.onboarding.dashboards;

import java.io.IOException;

import com.dynatrace.utils.VersionedSource;

public interface Dashboard extends VersionedSource<Dashboard> {

	public static final String FILE_EXTENSION = ".dashboard.xml".intern();
	
	String getKey();
	DashboardTemplate asTemplate() throws IOException;
}
