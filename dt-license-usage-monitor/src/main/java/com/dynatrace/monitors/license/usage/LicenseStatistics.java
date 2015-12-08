package com.dynatrace.monitors.license.usage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

public final class LicenseStatistics {
	
	@SuppressWarnings("unused")
	private static final Logger LOGGER =
			Logger.getLogger(LicenseStatistics.class.getName());

	private final Map<String,Integer> usageMap = new HashMap<>();
	
	@SuppressWarnings("unused")
	private final String id;
	
	public LicenseStatistics(String id) {
		this.id = id;
	}
	
	public int getCount() {
		int count = 0;
		Collection<Integer> values = usageMap.values();
		for (Integer value : values) {
			count += value.intValue();
		}
		return count;
	}
	
	public Iterable<String> getKeys() {
		Collection<String> keys = new ArrayList<>();
		keys.addAll(usageMap.keySet());
		return keys;
	}
	
	public int get(String key) {
		Objects.requireNonNull(key);
		Integer iCount = usageMap.get(key);
		if (iCount == null) {
			iCount = new Integer(0);
			usageMap.put(key, iCount);
		}
		return iCount.intValue();
	}
	
	private void put(String key, int count) {
		Objects.requireNonNull(key);
		usageMap.put(key, new Integer(count));
	}
	
	public void inc(String key) {
		inc(key, 1);
	}
	
	public void inc(String key, int count) {
		Objects.requireNonNull(key);
		put(key, get(key) + count);
	}
}
