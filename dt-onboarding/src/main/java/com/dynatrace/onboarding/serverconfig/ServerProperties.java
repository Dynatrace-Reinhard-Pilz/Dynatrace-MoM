package com.dynatrace.onboarding.serverconfig;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dynatrace.http.config.ServerConfig;
import com.dynatrace.mom.connector.client.ConnectorClient;
import com.dynatrace.mom.connector.model.dashboards.DashboardReference;
import com.dynatrace.mom.connector.model.profiles.SystemProfileReference;
import com.dynatrace.onboarding.dashboards.Dashboard;
import com.dynatrace.onboarding.dashboards.RemoteDashboard;
import com.dynatrace.onboarding.profiles.Profile;
import com.dynatrace.onboarding.profiles.RemoteProfile;
import com.dynatrace.utils.SizedIterable;

public class ServerProperties {
	
	private static final Logger LOGGER =
			Logger.getLogger(ServerProperties.class.getName());
	
	public ServerConfigXml serverConfigXml = ServerConfigXml.VOID;
	private final Map<String, Profile> profiles = new HashMap<>();
	private final Map<String, Dashboard> dashboards = new HashMap<>();
	
	public static ServerProperties load(ServerConfig serverConfig) {
		return load(serverConfig, true);
	}
	
	public static ServerProperties load(ServerConfig serverConfig, boolean silently) {
		ServerProperties serverProperties = new ServerProperties();
		try {
			serverProperties.fetch(serverConfig, silently);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Unable to fetch configuration files from dynaTrace Server", e);
			return null;
		}
		return serverProperties;
	}
	
	private void fetchProfiles(ServerConfig serverConfig) throws IOException {
		ConnectorClient connectorClient = new ConnectorClient(serverConfig);
		SizedIterable<SystemProfileReference> xmlProfiles = connectorClient.getProfiles();
		if (xmlProfiles == null) {
			return;
		}
		for (SystemProfileReference xmlProfile : xmlProfiles) {
			RemoteProfile metaProfile =
					new RemoteProfile(xmlProfile, serverConfig);
			profiles.put(metaProfile.getId(), metaProfile);
		}
	}
	
	private void fetchDashboards(ServerConfig serverConfig) throws IOException {
		ConnectorClient connectorClient = new ConnectorClient(serverConfig);
		SizedIterable<DashboardReference> dashboards = connectorClient.getDashboards();
		if (dashboards == null) {
			return;
		}
		for (DashboardReference dashboard : dashboards) {
			RemoteDashboard metaProfile =
					new RemoteDashboard(dashboard, serverConfig);
			this.dashboards.put(metaProfile.getId(), metaProfile);
		}
	}
	
	private void fetch(ServerConfig serverConfig, boolean silently) throws IOException {
		if (!silently) {
			LOGGER.log(Level.INFO, "Fetching System Profile, Dashboards and Server Configuration from dynaTrace Server");
		}
		
		serverConfigXml = ServerConfigXml.get(serverConfig);
		fetchProfiles(serverConfig);
		fetchDashboards(serverConfig);
	}
	
	public Profile profiles(String name) {
		if (name == null) {
			return null;
		}
		return profiles.get(name);
	}
	
	public Collection<Profile> profiles() {
		return profiles.values();
	}
	
	public Collection<Dashboard> dashboards() {
		return dashboards.values();
	}

}
