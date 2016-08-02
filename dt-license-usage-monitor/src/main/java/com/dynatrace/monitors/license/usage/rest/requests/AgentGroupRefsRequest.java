package com.dynatrace.monitors.license.usage.rest.requests;

import java.net.MalformedURLException;
import java.net.URL;

import com.dynatrace.http.Method;
import com.dynatrace.http.ResponseCode;
import com.dynatrace.http.request.AbstractRequest;
import com.dynatrace.monitors.license.usage.rest.responses.XmlAgentGroupRefs;
import com.dynatrace.monitors.license.usage.rest.responses.XmlAgentGroupsRef;

public final class AgentGroupRefsRequest extends AbstractRequest<XmlAgentGroupRefs> {

	private final XmlAgentGroupsRef agentGroupsRef;
	
	public AgentGroupRefsRequest(XmlAgentGroupsRef agentGroupsRef) {
		this.agentGroupsRef = agentGroupsRef;
	}
	
	@Override
	protected String getPath() {
		try {
			return new URL(agentGroupsRef.getHref()).getPath();
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
	protected XmlAgentGroupRefs getResultPrototype() {
		return new XmlAgentGroupRefs();
	}

}
