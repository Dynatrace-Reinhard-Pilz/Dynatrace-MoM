package com.dynatrace.agents;

import java.util.logging.Logger;

import com.dynatrace.collectors.CollectorInfo;
import com.dynatrace.collectors.CollectorRecord;
import com.dynatrace.mom.runtime.components.ServerRecord;
import com.dynatrace.utils.Version;

public class AgentUtils {
	
	@SuppressWarnings("unused")
	private static final Logger LOGGER =
			Logger.getLogger(AgentUtils.class.getName());
	
	public static boolean checkRestartRequired(AgentInfo agentInformation, ServerRecord serverRecord) {
		if (agentInformation == null) {
			return false;
		}
		if (serverRecord == null) {
			return false;
		}
		Version agentVersion = getAgentVersion(agentInformation);
		if (!Version.isValid(agentVersion)) {
			return false;
		}
		CollectorRecord collector = getCollector(agentInformation, serverRecord);
		if (collector == null) {
			return false;
		}
		Version collectorVersion = collector.getVersion();
		if (!Version.isValid(collectorVersion)) {
			return false;
		}
		return collectorVersion.compareTo(agentVersion) > 0;
	}

	public static Version getAgentVersion(AgentInfo agentInformation) {
		if (agentInformation == null) {
			return Version.UNDEFINED;
		}
		AgentProperties agentProperties = agentInformation.getAgentProperties();
		if (agentProperties == null) {
			return Version.UNDEFINED;
		}
		Version version = Version.parse(agentProperties.getAgentVersion(), false);
		if (version == null) {
			return Version.UNDEFINED;
		}
		return version;
	}
	
	public static CollectorRecord getCollector(AgentInfo agentInformation, ServerRecord serverRecord) {
		if (agentInformation == null) {
			return null;
		}
		if (serverRecord == null) {
			return null;
		}
		CollectorInfo collectorInfo = agentInformation.getCollectorInformation();
		if (collectorInfo == null) {
			return null;
		}
		String collectorHost = collectorInfo.getHost();
		if (collectorHost == null) {
			return null;
		}
		String collectorName = collectorInfo.getName();
		if (collectorName == null) {
			return null;
		}
		return serverRecord.getCollector(collectorName, collectorHost);
	}
}
