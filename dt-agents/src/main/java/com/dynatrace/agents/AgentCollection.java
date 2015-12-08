package com.dynatrace.agents;

import java.util.Collection;

public interface AgentCollection extends Iterable<AgentInfo> {
	
	void addAll(Iterable<AgentInfo> agentInfos);
	void add(AgentInfo agentInfos);
	Collection<AgentInfo> values();
	AgentInfo get(String agentId);
	void remove(String agentId);
	int size();
}
