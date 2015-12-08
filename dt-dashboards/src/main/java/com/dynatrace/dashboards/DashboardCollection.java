package com.dynatrace.dashboards;

import com.dynatrace.utils.SizedIterable;

public interface DashboardCollection extends SizedIterable<Dashboard> {

	Dashboard get(String dashboardName);
	void addAll(Iterable<Dashboard> dashboards);
	void add(Dashboard dashboard);
	
}
