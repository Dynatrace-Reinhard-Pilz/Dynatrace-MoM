package com.dynatrace.monitors.license.usage.remoting;

import com.dynatrace.http.config.ServerConfig;
import com.dynatrace.http.request.AbstractRequest;
import com.dynatrace.monitors.license.usage.api.AgentGroup;
import com.dynatrace.monitors.license.usage.rest.requests.AgentGroupRequest;
import com.dynatrace.monitors.license.usage.rest.responses.XmlAgentGroup;
import com.dynatrace.monitors.license.usage.rest.responses.XmlAgentGroupRef;
import com.dynatrace.profiles.metainfo.MetaInfo;

public final class RemoteAgentGroup extends RemoteObject<XmlAgentGroupRef, XmlAgentGroup> implements AgentGroup {
	
	public RemoteAgentGroup(XmlAgentGroupRef xmlAgentGroupRef, ServerConfig serverConfig) {
		super(xmlAgentGroupRef, serverConfig);
	}
	
	@Override
	public String getName() {
		return getRef().getName();
	}

	@Override
	public String getDescription() {
		return getData().getDescription();
	}
	
	@Override
	public MetaInfo getMetaInfo() {
		String description = getDescription();
		if (description == null) {
			return null;
		}
		return MetaInfo.parse(description);
	}
	
	@Override
	public String getMetaInfo(String key) {
		MetaInfo metaInfo = getMetaInfo();
		if (metaInfo == null) {
			return null;
		}
		return metaInfo.get(key);
	}

	@Override
	protected AbstractRequest<XmlAgentGroup> createRequest(XmlAgentGroupRef ref) {
		return new AgentGroupRequest(ref);
	}
}
