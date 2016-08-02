package com.dynatrace.monitors.license.usage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dynatrace.http.HttpResponse;
import com.dynatrace.http.config.ServerConfig;
import com.dynatrace.monitors.license.usage.api.AgentGroup;
import com.dynatrace.monitors.license.usage.api.Profile;
import com.dynatrace.monitors.license.usage.remoting.RemoteProfile;
import com.dynatrace.monitors.license.usage.rest.requests.ProfileRefsRequest;
import com.dynatrace.monitors.license.usage.rest.responses.XmlProfileRef;
import com.dynatrace.monitors.license.usage.rest.responses.XmlProfileRefs;

public class ProfileCache extends Thread {
	
	private static final Logger LOGGER =
			Logger.getLogger(ProfileCache.class.getName());
	
	private final Map<String, Profile> profiles = new HashMap<>();
	
	public ProfileCache(ServerConfig serverConfig) {
		ProfileRefsRequest request = new ProfileRefsRequest();
		HttpResponse<XmlProfileRefs> response = request.execute(serverConfig);
		XmlProfileRefs xmlProfileRefs = response.getData();
		Collection<XmlProfileRef> profileRefs = xmlProfileRefs.getProfileRefs();
		for (XmlProfileRef xmlProfileRef : profileRefs) {
			if (xmlProfileRef == null) {
				continue;
			}
			RemoteProfile profile = new RemoteProfile(xmlProfileRef, serverConfig);
			profiles.put(profile.getId(), profile);
		}
	}
	
	public Profile get(String systemProfileId) {
		if (systemProfileId == null) {
			return null;
		}
		return profiles.get(systemProfileId);
	}
	
	public String getMetaInfo(String systemProfileId, String agentGroupId, String key, String def) {
		if (systemProfileId == null) {
			LOGGER.log(Level.WARNING, "systemProfileId == null");
			return def;
		}
		Profile systemProfile = get(systemProfileId);
		if (systemProfile == null) {
			LOGGER.log(Level.WARNING, "systemProfile == null");
			return def;
		}
		AgentGroup agentGroup = systemProfile.getAgentGroup(agentGroupId);
		if (agentGroup == null) {
			LOGGER.log(Level.WARNING, "agentGroup == null");
			return def;
		}
		String metaInfo = agentGroup.getMetaInfo(key);
		if (metaInfo != null) {
			return metaInfo;
		}
		metaInfo = systemProfile.getMetaInfo(key);
		if (metaInfo == null) {
			return def;
		}
		return metaInfo;
	}
	
}
