package com.dynatrace.mom.runtime;

import java.util.Collection;

import com.dynatrace.mom.runtime.components.ServerRecord;


public interface BaseServerRepository {
	
	void setServerRecords(Collection<ServerRecord> serverRecords);
	Collection<ServerRecord> getServerRecords();
	
}
