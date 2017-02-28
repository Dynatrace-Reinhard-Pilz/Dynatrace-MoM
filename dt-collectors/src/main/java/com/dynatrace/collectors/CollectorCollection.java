package com.dynatrace.collectors;

import java.util.Collection;

import com.dynatrace.utils.Batch;

public interface CollectorCollection extends Batch<CollectorRecord> {
	
	void addAll(Iterable<CollectorInfo> collectorInfos);
	void add(CollectorInfo collectorInfo);
	Collection<CollectorRecord> values();
	CollectorRecord get(CollectorInfo collectorInfo);
	void remove(CollectorInfo collectorInfo);
	CollectorCollection filter(Filter<CollectorRecord> filter);
}
