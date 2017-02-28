package com.dynatrace.onboarding.serverconfig;

import java.io.IOException;
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
import com.dynatrace.utils.Batch;
import com.dynatrace.utils.Iterables;

public class ServerProperties {
	
	private static final String EXM_CANNOT_FETCH =
			"Unable to fetch configuration files from dynaTrace Server";
	private static final String MSG_FETCHING =
			"Fetching System Profile, Dashboards and Server Configuration from dynaTrace Server";

	private static final Logger LOGGER =
			Logger.getLogger(ServerProperties.class.getName());
	
	public ServerConfigXml serverConfigXml = ServerConfigXml.VOID;
	private final Map<String, Profile> profiles = new HashMap<>();
	private final Map<String, Dashboard> dashboards = new HashMap<>();
	
	public static ServerProperties load(ServerConfig srvConf) {
		return load(srvConf, true);
	}
	
	public static ServerProperties load(ServerConfig srvConf, boolean silent) {
		ServerProperties srvProps = new ServerProperties();
		try {
			srvProps.fetch(srvConf, silent);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, EXM_CANNOT_FETCH, e);
			return null;
		}
		return srvProps;
	}
	
	private void fetchProfiles(ServerConfig srvConf) throws IOException {
		ConnectorClient client = new ConnectorClient(srvConf);
		Batch<SystemProfileReference> xmlProfiles =	client.getProfiles();
		if (Iterables.isNullOrEmpty(xmlProfiles)) {
			return;
		}
		for (SystemProfileReference xmlProfile : xmlProfiles) {
			put(xmlProfile, srvConf);
		}
	}
	
	private void put(SystemProfileReference xmlProfile, ServerConfig srvConf) {
		if (xmlProfile == null) {
			return;
		}
		RemoteProfile metaProfile =	new RemoteProfile(xmlProfile, srvConf);
		profiles.put(metaProfile.id(), metaProfile);
	}
	
	private void fetchDashboards(ServerConfig srvConf) throws IOException {
		ConnectorClient client = new ConnectorClient(srvConf);
		Batch<DashboardReference> dashboards = client.getDashboards();
		if (Iterables.isNullOrEmpty(dashboards)) {
			return;
		}
		for (DashboardReference dashboard : dashboards) {
			put(dashboard, srvConf);
		}
	}
	
	private void put(DashboardReference dashboard, ServerConfig srvConf) {
		if (dashboard == null) {
			return;
		}
		RemoteDashboard metaProfile = new RemoteDashboard(dashboard, srvConf);
		this.dashboards.put(metaProfile.id(), metaProfile);
	}
	
	private void fetch(ServerConfig srvConf, boolean silent)
			throws IOException
	{
		if (!silent) {
			LOGGER.log(Level.INFO, MSG_FETCHING);
		}
		
		serverConfigXml = ServerConfigXml.get(srvConf);
		fetchProfiles(srvConf);
		fetchDashboards(srvConf);
	}
	
	public Profile profiles(String name) {
		if (name == null) {
			return null;
		}
		return profiles.get(name);
	}
	
	public Profile[] profiles() {
		if (profiles == null) {
			return new Profile[0];
		}
		return profiles.values().toArray(new Profile[profiles.size()]);  
	}
	
	public Dashboard[] dashboards() {
		if (dashboards == null) {
			return new Dashboard[0];
		}
		return dashboards.values().toArray(new Dashboard[dashboards.size()]);  
	}

}
