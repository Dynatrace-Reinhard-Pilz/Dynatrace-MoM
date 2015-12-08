package com.dynatrace.dashboards;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class DefaultDashboardCollection implements DashboardCollection {

	private final HashMap<String, Dashboard> dashboards =
			new HashMap<String, Dashboard>();

	@Override
	public Dashboard get(String dashboardName) {
		synchronized (this.dashboards) {
			return this.dashboards.get(dashboardName);
		}
	}
	
	public void onDashboardRemoved(Dashboard dashboard) {
		// subclasses may override
	}
	
	public void onDashboardAdded(Dashboard dashboard) {
		// subclasses may override
	}
	
	public void onDashboadUpdated(Dashboard dashboard) {
		// subclasses may override
	}
	
	private void removeMissing(Iterable<Dashboard> dashboards) {
		Iterator<String> it = null;
		for (it = this.dashboards.keySet().iterator(); it.hasNext(); ) {
			String dashboardName = it.next();
			boolean found = false;
			for (Dashboard dashboard : dashboards) {
				if (dashboardName.equals(dashboard.getId())) {
					found = true;
				}
			}
			if (!found) {
				Dashboard dashboard = get(dashboardName);
				it.remove();
				onDashboardRemoved(dashboard);
			}
		}
	}
	
	@Override
	public void addAll(Iterable<Dashboard> dashboards) {
		if (dashboards == null) {
			return;
		}
		synchronized (this.dashboards) {
			removeMissing(dashboards);
			for (Dashboard dashboard : dashboards) {
				add(dashboard);
			}
		}
	}
	
	@Override
	public void add(Dashboard dashboard) {
		if (dashboard == null) {
			return;
		}
		synchronized (this.dashboards) {
			String dashboardName = dashboard.getId();
			if (!this.dashboards.containsKey(dashboardName)) {
				onDashboardAdded(dashboard);
			} else {
				onDashboadUpdated(dashboard);
			}
			this.dashboards.put(dashboardName, dashboard);
		}
	}
	
	@Override
	public Iterator<Dashboard> iterator() {
		synchronized (this.dashboards) {
			return new ArrayList<Dashboard>(dashboards.values()).iterator();
		}
	}

	@Override
	public int size() {
		synchronized (this.dashboards) {
			return this.dashboards.size();
		}
	}

}
