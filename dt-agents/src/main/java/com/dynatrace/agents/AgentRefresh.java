package com.dynatrace.agents;

import java.util.logging.Logger;

import com.dynatrace.http.ServerOperation;
import com.dynatrace.http.config.ServerConfig;
import com.dynatrace.http.request.Request;
import com.dynatrace.utils.ExecutionContext;

public class AgentRefresh extends ServerOperation<AgentInfos> {
	
	private static final Logger LOGGER =
			Logger.getLogger(AgentRefresh.class.getName());
	
	public AgentRefresh(ExecutionContext ctx, ServerConfig scfg) {
		super(ctx, scfg);
	}

	@Override
	protected void handleResult(AgentInfos agentInfos) {
		getAttribute(AgentAware.class).setAgents(agentInfos);
	}

	@Override
	public Request<AgentInfos> createRequest() {
		return new AgentsRequest();
	}

	@Override
	protected Logger logger() {
		return LOGGER;
	}
}
