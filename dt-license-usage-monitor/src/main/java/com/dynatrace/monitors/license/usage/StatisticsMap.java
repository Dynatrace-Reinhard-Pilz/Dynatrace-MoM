package com.dynatrace.monitors.license.usage;

import java.util.HashMap;
import java.util.Map;

public final class StatisticsMap {

	private final Map<String, LicenseStatistics> statistics = new HashMap<>();
	
	public LicenseStatistics get(String key) {
		synchronized (statistics) {
			LicenseStatistics licenseStatistics = statistics.get(key);
			if (licenseStatistics == null) {
				licenseStatistics = new LicenseStatistics(key);
				statistics.put(key, licenseStatistics);
			}
			return licenseStatistics;
		}
	}
	
	public Iterable<LicenseStatistics> getStatistics() {
		synchronized (statistics) {
			return statistics.values();
		}
	}
}
