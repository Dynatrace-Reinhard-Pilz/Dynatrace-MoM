package com.dynatrace.agents;

import com.dynatrace.utils.Labelled;
import com.dynatrace.utils.Unique;

public class AgentRecord implements Unique<AgentInfo>, Labelled {
	
	private AgentInfo agentInfo = null;
	private final Labelled parent = null;
	
	public AgentRecord(AgentInfo agentInfo) {
		this.agentInfo = agentInfo;
	}

	@Override
	public AgentInfo getId() {
		return agentInfo;
	}
	
	public void setAgentInfo(AgentInfo agentInfo) {
		this.agentInfo = agentInfo;
	}

	@Override
	public String name() {
		return agentInfo.getAgentInstanceName();
	}
	
	public Labelled getParent() {
		return parent;
	}

}
