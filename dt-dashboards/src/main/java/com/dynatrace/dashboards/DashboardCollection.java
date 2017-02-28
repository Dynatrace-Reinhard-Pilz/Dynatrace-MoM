package com.dynatrace.dashboards;

import com.dynatrace.utils.Batch;

public interface DashboardCollection extends Batch<Dashboard> {

	Dashboard get(String dashboardName);
	void addAll(Iterable<Dashboard> dashboards);
	void add(Dashboard dashboard);
	
}
