package com.dynatrace.collectors.web;

import com.dynatrace.collectors.CollectorRecord;
import com.dynatrace.web.base.ModelBase;

public interface CollectorsModel extends ModelBase {

	Iterable<CollectorRecord> getCollectors();
	int getCollectorCount();
	boolean isServerColumnRequired();
	
}
