package com.dynatrace.monitors.license.usage.api;

import java.util.Collection;

import com.dynatrace.profiles.metainfo.Metaable;

public interface Profile extends Metaable {

	String getId();
	String getHref();
	boolean isRecording();
	boolean isInteractiveLicensed();
	boolean isEnabled();
	String getDescription();
	Collection<AgentGroup> getAgentGroups();
	AgentGroup getAgentGroup(String agentGroupName);
	
}
