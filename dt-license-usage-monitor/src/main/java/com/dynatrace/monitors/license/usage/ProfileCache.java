package com.dynatrace.monitors.license.usage;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dynatrace.http.config.ServerConfig;
import com.dynatrace.profiles.AgentGroup;
import com.dynatrace.profiles.SystemProfile;
import com.dynatrace.sysinfo.SysInfoRefresh;
import com.dynatrace.utils.Closeables;
import com.dynatrace.utils.DefaultExecutionContext;

public class ProfileCache {
	
	private static final Logger LOGGER =
			Logger.getLogger(ProfileCache.class.getName());
	
	private final String[] FILE_TYPES = new String[] {
		"profiles",
	};

	private final Map<String, SystemProfile> profiles = new HashMap<>();
	private long tsLastRefresh = 0;
	
	public void refresh(ServerConfig serverConfig, long maxAge) {
		synchronized (profiles) {
			long now = System.currentTimeMillis();
			if (now - tsLastRefresh > maxAge) {
				update(serverConfig);
				tsLastRefresh = now;
			}
		}
	}
	
	public void clear() {
		synchronized (profiles) {
			for (SystemProfile profile : profiles.values()) {
				if (profile == null) {
					continue;
				}
				File file = profile.getLocalFile();
				if (Closeables.exists(file)) {
					Closeables.delete(file);
				}
			}
			profiles.clear();
		}
	}
	
	private void update(ServerConfig serverConfig) {
		if (serverConfig == null) {
			return;
		}
		clear();
        SysInfoRefresh sysInfoRefresh = new SysInfoRefresh(new DefaultExecutionContext(), serverConfig) {
        	@Override
        	public void onSystemProfile(SystemProfile systemProfile) {
        		add(systemProfile);
        	}
        	
        	@Override
        	protected String[] getSupportedFileTypes() {
        		return FILE_TYPES;
        	}
        };
        sysInfoRefresh.execute();
	}
	
	public void add(SystemProfile profile) {
		if (profile == null) {
			return;
		}
		String id = profile.getId();
		if (id == null) {
			return;
		}
		profiles.put(id, profile);
	}
	
	public SystemProfile get(String systemProfileId) {
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
		SystemProfile systemProfile = get(systemProfileId);
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
	
	@Override
	protected void finalize() throws Throwable {
		clear();
	}
	
}
