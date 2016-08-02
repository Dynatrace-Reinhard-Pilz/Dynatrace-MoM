package com.dynatrace.agents;

import java.util.logging.Level;

import com.dynatrace.agents.AgentInfos;
import com.dynatrace.http.Method;
import com.dynatrace.http.ResponseCode;
import com.dynatrace.http.request.AbstractRequest;

public class AgentsRequest extends AbstractRequest<AgentInfos> {
	
	private static final AgentInfos RESPONSE_PROTOTYPE = new AgentInfos();
	public String COMMAND = "/rest/management/agents".intern();
	
	@Override
	protected Level level() {
		return Level.FINE;
	}
	
	@Override
	protected String getPath() {
		return COMMAND;
	}

	@Override
	protected Method getMethod() {
		return Method.GET;
	}

	@Override
	protected ResponseCode getExpectedResponseCode() {
		return ResponseCode.OK;
	}

	@Override
	protected AgentInfos getResultPrototype() {
		return RESPONSE_PROTOTYPE;
	}

}
