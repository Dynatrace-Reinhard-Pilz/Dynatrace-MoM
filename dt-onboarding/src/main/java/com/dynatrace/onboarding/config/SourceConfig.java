package com.dynatrace.onboarding.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dynatrace.http.HttpResponse;
import com.dynatrace.http.Protocol;
import com.dynatrace.http.UnexpectedResponseCodeException;
import com.dynatrace.http.VersionRequest;
import com.dynatrace.http.config.ConnectionConfig;
import com.dynatrace.http.config.Credentials;
import com.dynatrace.http.config.ServerConfig;
import com.dynatrace.http.permissions.Unauthorized;
import com.dynatrace.onboarding.dashboards.DashboardTemplate;
import com.dynatrace.onboarding.profiles.ProfileTemplate;
import com.dynatrace.utils.Strings;
import com.dynatrace.utils.Version;

/**
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 */
public class SourceConfig {
	
	private static final Logger LOGGER =
			Logger.getLogger(SourceConfig.class.getName());
	
	public static final String PROP_HOST = "config.source.server.host"; 
	public static final String PROP_USER = "config.source.server.user"; 
	public static final String PROP_PASS = "config.source.server.pass";

	public static final String DEFAULT = "default";
	
	public static void set(Properties properties, String key, String value) {
		Objects.requireNonNull(properties);
		Objects.requireNonNull(key);
		Objects.requireNonNull(value);
		
		if (properties.get(key) != null) {
			return;
		}
		properties.setProperty(key, value);
	}
	
	private static String extractGroupKey(String propertyName) {
		StringTokenizer strTok = new StringTokenizer(propertyName, ".");
		if (!strTok.hasMoreTokens()) {
			return null;
		}
		String token = strTok.nextToken();
		if (!"config".equals(token)) {
			return null;
		}
		
		if (!strTok.hasMoreTokens()) {
			return null;
		}
		token = strTok.nextToken();
		if (!"user".equals(token)) {
			return null;
		}
		
		if (!strTok.hasMoreTokens()) {
			return null;
		}
		token = strTok.nextToken();
		if (!"groups".equals(token)) {
			return null;
		}
		
		if (!strTok.hasMoreTokens()) {
			return null;
		}
		String groupKey = strTok.nextToken().trim();
		if (groupKey.isEmpty()) {
			return null;
		}
		
		if (!strTok.hasMoreTokens()) {
			return null;
		}
		token = strTok.nextToken();
		if (!"name".equals(token)) {
			return null;
		}
		
		if (strTok.hasMoreTokens()) {
			return null;
		}
		return groupKey;
	}
	
	public static String[] discoverGroupKeys(Properties properties) {
		Collection<String> groupKeys = new ArrayList<>();
		for (Object name : properties.keySet()) {
			if (name == null) {
				continue;
			}
			String groupKey = extractGroupKey(name.toString());
			if (groupKey != null) {
				groupKeys.add(groupKey);
			}
		}
		return groupKeys.toArray(new String[groupKeys.size()]);
	}
	
	private static String extractDashboardKey(String propertyName) {
		StringTokenizer strTok = new StringTokenizer(propertyName, ".");
		if (!strTok.hasMoreTokens()) {
			return null;
		}
		String token = strTok.nextToken();
		if (!"config".equals(token)) {
			return null;
		}
		
		if (!strTok.hasMoreTokens()) {
			return null;
		}
		token = strTok.nextToken();
		if (!"dashboards".equals(token)) {
			return null;
		}
		
		if (!strTok.hasMoreTokens()) {
			return null;
		}
		String dashboardKey = strTok.nextToken().trim();
		if (dashboardKey.isEmpty()) {
			return null;
		}
		
		if (!strTok.hasMoreTokens()) {
			return null;
		}
		token = strTok.nextToken();
		if (!"name".equals(token)) {
			return null;
		}
		
		if (strTok.hasMoreTokens()) {
			return null;
		}
		return dashboardKey;
	}
	
	public static String[] discoverDashboardKeys(Properties properties) {
		Collection<String> dashboardKeys = new ArrayList<>();
		for (Object name : properties.keySet()) {
			if (name == null) {
				continue;
			}
			String dashboardKey = extractDashboardKey(name.toString());
			if (dashboardKey != null) {
				dashboardKeys.add(dashboardKey);
			}
		}
		return dashboardKeys.toArray(new String[dashboardKeys.size()]);
	}
	
	public static void setDefaultValues(Properties properties) {
	}
	
	public static boolean areUserGroupsConfigured() {
		return userGroupKeys().length > 0;
	}
	
	private static SourceConfig INSTANCE = init();
	
	private final ServerConfig serverConfig;
	private final Resources resources;
	private final String profileTemplate;
	private final Properties properties;
	private final Version version;
	
	/**
	 * private c'tor
	 * 
	 * @param serverConfig
	 * @param resources
	 * @param profileTemplate
	 * @param dashboardTemplate
	 * @param userGroup
	 * @param mgmntRole
	 * @param profileRole
	 * @param dashboardPermission
	 */
	private SourceConfig(Version version, Properties properties, ServerConfig serverConfig, Resources resources, String profileTemplate) {
		this.properties = properties;
		this.serverConfig = serverConfig;
		this.resources = resources;
		this.profileTemplate = profileTemplate;
		this.version = version;
		
		Collection<String> templateNames = new ArrayList<>();
		templateNames.addAll(resources.profileTemplates.keySet());
		for (String templateName : templateNames) {
			ProfileTemplate template = resources.profileTemplates.get(templateName);
			if (template == null) {
				continue;
			}
			Version templateVersion = template.version();
			if (!Version.isValid(templateVersion)) {
				LOGGER.log(Level.WARNING, "The System Profile Template '" + templateName + "' embedded within dt-onboarding.jar does not have a valid dynaTrace Version - this Template will be ignored");
			}
			boolean isCompatible = true;
			if (templateVersion.getMajor() > version.getMajor()) {
				isCompatible = false;
			}
			if (templateVersion.getMinor() > version.getMinor()) {
				isCompatible = false;
			}
			if (!isCompatible) {
				LOGGER.log(Level.WARNING, "The System Profile Template '" + templateName + "' (Version " + templateVersion + ") embedded within dt-onboarding.jar cannot be used on this dynaTrace Server (Version: " + version + ") - this Template will be ignored");
				resources.profileTemplates.remove(templateName);
			}
		}
		
		templateNames = new ArrayList<>();
		templateNames.addAll(resources.dashboards.keySet());
		for (String templateName : templateNames) {
			DashboardTemplate template = resources.dashboards.get(templateName);
			if (template == null) {
				continue;
			}
			Version templateVersion = template.version();
			if (!Version.isValid(templateVersion)) {
				LOGGER.log(Level.WARNING, "The Dashboard Template '" + templateName + "' embedded within dt-onboarding.jar does not have a valid dynaTrace Version - this Template will be ignored");
			}
			boolean isCompatible = true;
			if (templateVersion.getMajor() > version.getMajor()) {
				isCompatible = false;
			}
			if (templateVersion.getMinor() > version.getMinor()) {
				isCompatible = false;
			}
			if (!isCompatible) {
				LOGGER.log(Level.WARNING, "The Dashboard Template '" + templateName + "' (Version " + templateVersion + ") embedded within dt-onboarding.jar cannot be used on this dynaTrace Server (Version: " + version + ") - this Template will be ignored");
				resources.dashboards.remove(templateName);
			}
		}
	}
	
	public static Version getVersion() {
		return INSTANCE.version;
	}
	
	/**
	 * @return {@code true} if all required System Properties were present
	 * 		and were valid, {@code false} otherwise
	 */
	public static boolean isValid() {
		return INSTANCE != null;
	}
	
	public static String profileTemplate() {
		return INSTANCE.profileTemplate;
	}
	
	public static ServerConfig serverConfig() {
		return INSTANCE.serverConfig;
	}
	
	public static Resources resources() {
		return INSTANCE.resources;
	}
	
	public static Map<String,ProfileTemplate> profileTemplates() {
		return resources().profileTemplates;
	}
	
	public static ProfileTemplate profileTemplates(String name) {
		if (name == null) {
			return null;
		}
		if (name.endsWith(".profile.xml")) {
			return profileTemplates().get(name.substring(0, name.length() - ".profile.xml".length()));
		}
		return profileTemplates().get(name);
	}

	public static Map<String, DashboardTemplate> dashboardTemplates() {
		return resources().dashboards;
	}
	
	public static DashboardTemplate dashboardTemplates(String name) {
		return dashboardTemplates().get(name);
	}
	
	public static File temp() {
		return resources().temp;
	}
	
	private static class ConnectivityResponse {
		private final int status;
		private final Version version;
		
		public ConnectivityResponse(int status, Version version) {
			this.status = status;
			this.version = version;
		}
		
		public int getStatus() {
			return status;
		}
		
		public Version getVersion() {
			return version;
		}
	}
	
	private static ConnectivityResponse checkConnectivity(ServerConfig serverConfig) {
		VersionRequest versionRequest = new VersionRequest();
		HttpResponse<Version> response = versionRequest.execute(serverConfig);
		Version version = response.getData();
		if (version == null) {
			Throwable exception = response.getException();
			if (exception instanceof UnexpectedResponseCodeException) {
				UnexpectedResponseCodeException urce = (UnexpectedResponseCodeException) exception;
				String serverResponse = urce.getServerResponse();
				String missingPermission = Unauthorized.getMissingPermission(serverResponse);
				LOGGER.log(Level.INFO, "Missing Permission: " + missingPermission);
			}
			return new ConnectivityResponse(response.getStatus(), null);
		}
		LOGGER.log(Level.INFO, "dynaTrace Server Version: " + version);
		return new ConnectivityResponse(response.getStatus(), version);
	}
	
	private static class ServerConfigResponse {
		private final ServerConfig serverConfig;
		private final Version version;
		
		public ServerConfigResponse(ServerConfig serverConfig, Version version) {
			this.serverConfig = serverConfig;
			this.version = version;
		}
		
		public ServerConfig getServerConfig() {
			return serverConfig;
		}
		
		public Version getVersion() {
			return version;
		}
	}
	
	private static ServerConfigResponse resolveServerConfig() {
		String host = System.getProperty(PROP_HOST);
		if (host == null) {
			if (!Config.isValid()) {
				return null;
			}
			return new ServerConfigResponse(Config.serverConfig(), Config.getVersion());
		}
		String sPort = null;;
		int colonIdx = host.indexOf(':');
		if (colonIdx >= 0) {
			sPort = host.substring(colonIdx + 1);
			host = host.substring(0, colonIdx);
		}
		
		if (!Strings.isNotEmpty(sPort)) {
			LOGGER.log(Level.INFO, "No connection port to dynaTrace Server specified - assuming 8021");
			sPort = "8021";
		}
		
		int port = 0;
		try {
			port = Integer.parseInt(sPort);
		} catch (IllegalArgumentException e) {
			LOGGER.severe("The connection port to the dynaTrace Server specified (-D" + PROP_HOST + "=" + host + ") is not a valid port number");
			return null;
		}


		String user = System.getProperty(PROP_USER);
		if (user == null) {
			LOGGER.severe(
				"No dynaTrace Server user given (-D" + PROP_USER + "=<username>)"
			);
			return null;
		}
		
		String pass = System.getProperty(PROP_PASS);
		if (pass == null) {
			LOGGER.severe(
				"No dynaTrace Server user given (-D" + PROP_PASS + "=<password>)"
			);
			return null;
		}		
		
		ServerConfig serverConfig = createServerConfig(host, port, user, pass);
		
		ConnectivityResponse response = checkConnectivity(serverConfig);
		int responseCode = response.getStatus();
		switch (responseCode) {
			case 200:
				break;
			case 401:
				LOGGER.severe("Invalid user credentials for The dynaTrace Server");
				return null;
			case Integer.MIN_VALUE:
				if (serverConfig.getConnectionConfig().getProtocol() == Protocol.HTTP) {
					LOGGER.log(Level.INFO, "dynaTrace Server is not listening on plain HTTP - trying HTTPS");
					serverConfig.getConnectionConfig().setProtocol(Protocol.HTTPS);
				} else {
					LOGGER.log(Level.INFO, "dynaTrace Server is not listening on HTTPS - trying plain HTTP");
					serverConfig.getConnectionConfig().setProtocol(Protocol.HTTP);
				}
				response = checkConnectivity(serverConfig);
				responseCode = response.getStatus();
				switch (responseCode) {
				case 200:
					break;
				case 401:
					LOGGER.severe("The dynaTrace Server does not accept the specified user credentials");
					return null;
				default:
					LOGGER.severe("Unexpected HTTP response from dynaTrace Server (" + responseCode + ")");
					return null;
				}
		}
		return new ServerConfigResponse(serverConfig, response.getVersion());
	}
	
	public static String[] userGroupKeys() {
		return discoverGroupKeys(INSTANCE.properties);
	}
	
	public static String[] dashboardKeys() {
		return discoverDashboardKeys(INSTANCE.properties);
	}
	
	public static SourceConfig init() {
		setDefaultValues(System.getProperties());
		ServerConfigResponse serverConfigResponse = resolveServerConfig();
		if (serverConfigResponse == null) {
			return null;
		}
		ServerConfig serverConfig = serverConfigResponse.getServerConfig();
		if (serverConfig == null) {
			return null;
		}
		
		Resources resources = new Resources();
		if (!Config.isValid()) {
			return null;
		}
		return new SourceConfig(serverConfigResponse.getVersion(), System.getProperties(), serverConfig, resources, Config.profileTemplate());
	}
	
	private static Protocol guessProtocol(int port) {
		switch (port) {
		case 8021:
		case 443:
			return Protocol.HTTPS;
		case 8020:
		case 80:
			return Protocol.HTTP;
		default:
			return Protocol.HTTP;
		}
	}
	
	/**
	 * 
	 * @param host
	 * @param port
	 * @param user
	 * @param pass
	 * @return
	 */
	private static ServerConfig createServerConfig(String host, int port, String user, String pass) {
		Protocol protocol = guessProtocol(port);
		ConnectionConfig connectionConfig = new ConnectionConfig(
				protocol,
				host,
				port
			);
		Credentials credentials = new Credentials(user, pass);
		ServerConfig serverConfig = new ServerConfig(
			connectionConfig,
			credentials
		);
		return serverConfig;
	}
	
	public static Properties properties() {
		if (INSTANCE == null) {
			return System.getProperties();
		}
		return INSTANCE.properties;
	}
}
