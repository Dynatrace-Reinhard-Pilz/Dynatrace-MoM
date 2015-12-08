package com.dynatrace.agents;

public interface AgentAware {

	void setAgents(Iterable<AgentInfo> agentInfos);
	
}
