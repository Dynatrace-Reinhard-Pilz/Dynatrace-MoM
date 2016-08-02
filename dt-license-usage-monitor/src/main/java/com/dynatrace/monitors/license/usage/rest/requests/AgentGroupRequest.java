package com.dynatrace.monitors.license.usage.rest.requests;

import java.net.MalformedURLException;
import java.net.URL;

import com.dynatrace.http.Method;
import com.dynatrace.http.ResponseCode;
import com.dynatrace.http.request.AbstractRequest;
import com.dynatrace.monitors.license.usage.rest.responses.XmlAgentGroup;
import com.dynatrace.monitors.license.usage.rest.responses.XmlAgentGroupRef;

public final class AgentGroupRequest extends AbstractRequest<XmlAgentGroup> {
	
	private final XmlAgentGroupRef agentGroupRef;
	
	public AgentGroupRequest(XmlAgentGroupRef agentGroupRef) {
		this.agentGroupRef = agentGroupRef;
	}

	@Override
	protected String getPath() {
		try {
			return new URL(agentGroupRef.getHref()).getPath();
		} catch (MalformedURLException e) {
			return null;
		}
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
	protected XmlAgentGroup getResultPrototype() {
		return new XmlAgentGroup();
	}

}
