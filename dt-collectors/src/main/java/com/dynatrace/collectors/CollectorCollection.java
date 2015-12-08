package com.dynatrace.collectors;

import java.util.Collection;

import com.dynatrace.utils.SizedIterable;

public interface CollectorCollection extends SizedIterable<CollectorRecord> {
	
	void addAll(Iterable<CollectorInfo> collectorInfos);
	void add(CollectorInfo collectorInfo);
	Collection<CollectorRecord> values();
	CollectorRecord get(CollectorInfo collectorInfo);
	void remove(CollectorInfo collectorInfo);
	CollectorCollection filter(Filter<CollectorRecord> filter);
}
