package com.dynatrace.monitors.license.usage.remoting;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.dynatrace.http.config.ServerConfig;
import com.dynatrace.http.request.AbstractRequest;
import com.dynatrace.monitors.license.usage.api.AgentGroup;
import com.dynatrace.monitors.license.usage.api.Profile;
import com.dynatrace.monitors.license.usage.rest.requests.AgentGroupRefsRequest;
import com.dynatrace.monitors.license.usage.rest.requests.ProfileRequest;
import com.dynatrace.monitors.license.usage.rest.responses.XmlAgentGroupRef;
import com.dynatrace.monitors.license.usage.rest.responses.XmlAgentGroupRefs;
import com.dynatrace.monitors.license.usage.rest.responses.XmlProfile;
import com.dynatrace.monitors.license.usage.rest.responses.XmlProfileRef;
import com.dynatrace.profiles.metainfo.MetaInfo;
import com.dynatrace.utils.Strings;

public final class RemoteProfile extends RemoteObject<XmlProfileRef, XmlProfile> implements Profile {
	
	private Map<String, AgentGroup> agentGroups = null;

	public RemoteProfile(XmlProfileRef xmlProfileRef, ServerConfig serverConfig) {
		super(xmlProfileRef, serverConfig);
	}
	
	@Override
	public String getId() {
		return getRef().getId();
	}
	
	@Override
	public String getHref() {
		return getData().getHref();
	}
	
	@Override
	public boolean isRecording() {
		return getData().isRecording();
	}
	
	@Override
	public boolean isInteractiveLicensed() {
		return getData().isInteractiveLicensed();
	}
	
	@Override
	public boolean isEnabled() {
		return getData().isEnabled();
	}
	
	@Override
	public String getDescription() {
		return getData().getDescription();
	}	

	@Override
	public Collection<AgentGroup> getAgentGroups() {
		if (agentGroups != null) {
			return agentGroups.values();
		}
		agentGroups = new HashMap<String, AgentGroup>();
		AgentGroupRefsRequest request =
				new AgentGroupRefsRequest(getData().getAgenttGroupsRef());
		XmlAgentGroupRefs xmlAgentGroupRefs = request.execute(getServerConfig()).getData();
		Collection<XmlAgentGroupRef> agentGroupRefs = xmlAgentGroupRefs.getAgentGroupRefs();
		for (XmlAgentGroupRef xmlAgentGroupRef : agentGroupRefs) {
			agentGroups.put(
				xmlAgentGroupRef.getName(),
				new RemoteAgentGroup(xmlAgentGroupRef, getServerConfig())
			);
		}
		return agentGroups.values();
	}
	
	@Override
	public AgentGroup getAgentGroup(String agentGroupName) {
		if (agentGroupName == null) {
			return null;
		}
		getAgentGroups();
		return agentGroups.get(agentGroupName);
	}
	
	@Override
	public MetaInfo getMetaInfo() {
		String description = getDescription();
		if (Strings.isNullOrEmpty(description)) {
			return MetaInfo.parse(Strings.EMPTY);
		}
		MetaInfo metaInfo = MetaInfo.parse(description);
		return metaInfo;
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
	protected AbstractRequest<XmlProfile> createRequest(XmlProfileRef ref) {
		return new ProfileRequest(ref);
	}
	
}
