package com.dynatrace.onboarding.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
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
import com.dynatrace.onboarding.usergroups.DashboardPermission;
import com.dynatrace.utils.Strings;
import com.dynatrace.utils.Version;

/**
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 */
public class Config {
	
	private static final Logger LOGGER =
			Logger.getLogger(Config.class.getName());
	
	static {
		if (Debug.DEBUG) {
			Config.set(System.getProperties(), Config.PROP_DASHBOARD_TPL, "{@environment}_{@application}_TRIAGE_DASHBOARD");
			Config.set(System.getProperties(), Config.dashboardProp("JBOSS"), "{@environment}_{@application}_JBoss Monitoring");
			Config.set(System.getProperties(), Config.PROP_PROFILE_TPL, "{@application} {@environment} JBoss");
			Config.set(System.getProperties(), Config.PROP_PERMISSION_GROUP, "onboarding-test-group");
			Config.set(System.getProperties(), Config.PROP_MGMT_ROLE, "Guest");
			Config.set(System.getProperties(), Config.PROP_PROFILE_ROLE, "Administrator");
			Config.set(System.getProperties(), Config.PROP_DB_PERMISSION, "Read_Write");
			// Config.set(System.getProperties(), Config.PROP_PROFILE, "Jetty");
			// Config.set(System.getProperties(), "config.user.groups.default.dashboards.JBOSS.permission", "Read");
			
			Config.set(System.getProperties(), "variable.application", "APPLICATION-A");
			Config.set(System.getProperties(), "variable.environment", "ENVIRONMENT-A");
			Config.set(System.getProperties(), "variable.tier", "TIER-A");
			Config.set(System.getProperties(), "variable.project", "PROJECT-A");
		}
	}
	
	public static final String PROP_HOST = "config.server.host"; 
	public static final String PROP_USER = "config.server.user"; 
	public static final String PROP_PASS = "config.server.pass";
	public static final String PROP_DASHBOARD_TPL = "config.templates.dashboard";
	public static final String PROP_PROFILE_TPL = "config.templates.profile";
	public static final String PROP_PROFILE = "config.profile";
	public static final String PROP_PERMISSION_GROUP = "config.user.group";
	public static final String PROP_MGMT_ROLE = "config.user.group.management.role";
	public static final String PROP_PROFILE_ROLE = "config.user.group.profile.role";
	public static final String PROP_DB_PERMISSION = "config.user.group.dashboard.permission";
	public static final String PROP_DB_AUTOOPEN = "config.user.group.dashboard.autoopen";

	public static final String DEFAULT = "default";

	private static final String DEFAULT_MANAGEMENT_ROLE = "Guest";
	private static final String DEFAULT_PROFILE_ROLE = "Administrator";
	private static final DashboardPermission DEFAULT_DASHBOARD_PERMISSION =
			DashboardPermission.Read;
	
	private static final Boolean DEFAULT_DASHBOARD_AUTO_OPEN = Boolean.TRUE;
	
	private static String get(Properties properties, String key) {
		Objects.requireNonNull(properties);
		Objects.requireNonNull(key);
		String value = properties.getProperty(key);
		return value;
	}
	
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
		set(properties, PROP_HOST, "localhost:8021");
		set(properties, PROP_USER, "admin");
		set(properties, PROP_PASS, "admin");
		
		String value = get(properties, PROP_DASHBOARD_TPL);
		if (value != null) {
			set(properties, dashboardProp(DEFAULT), value);
		}
		
		value = get(properties, PROP_PERMISSION_GROUP);
		if (Strings.isNotEmpty(value)) {
			properties.remove(PROP_PERMISSION_GROUP);
			set(properties, groupProp(DEFAULT), value);
		}
		
		value = get(properties, PROP_PROFILE_ROLE);
		if (value != null) {
			properties.remove(PROP_PROFILE_ROLE);
			set(properties, profileRoleProp(DEFAULT), value);
		}
		
		value = get(properties, PROP_MGMT_ROLE);
		if (value != null) {
			properties.remove(PROP_MGMT_ROLE);
			set(properties, mgmtRoleProp(DEFAULT), value);
		}
		
		value = get(properties, PROP_DB_PERMISSION);
		if (value != null) {
			properties.remove(PROP_DB_PERMISSION);
			set(properties, dbPermProp(DEFAULT, DEFAULT), value);
		}
		
		value = get(properties, PROP_DB_AUTOOPEN);
		if (value != null) {
			properties.remove(PROP_DB_AUTOOPEN);
			set(properties, dbAutoOpenProp(DEFAULT, DEFAULT), value);
		}
		
		// config.user.groups.default.management.role=Guest
		set(properties, mgmtRoleProp(DEFAULT), DEFAULT_MANAGEMENT_ROLE);
		
		// config.user.groups.default.profiles.default.role=Guest
		set(properties, profileRoleProp(DEFAULT), DEFAULT_PROFILE_ROLE);
		
		// config.user.groups.default.dashboards.default.permission=Guest
		set(properties, dbPermProp(DEFAULT, DEFAULT), DEFAULT_DASHBOARD_PERMISSION.name());
		
		// config.user.groups.default.dashboards.default.autoopen=true
		set(properties, dbAutoOpenProp(DEFAULT, DEFAULT), DEFAULT_DASHBOARD_AUTO_OPEN.toString());
		
		String[] groupKeys = discoverGroupKeys(properties);
		for (String groupKey : groupKeys) {
			set(properties, mgmtRoleProp(groupKey), get(properties, mgmtRoleProp(DEFAULT)));
			set(properties, profileRoleProp(groupKey), get(properties, profileRoleProp(DEFAULT)));
			set(properties, dbPermProp(groupKey, DEFAULT), get(properties, dbPermProp(DEFAULT, DEFAULT)));
			set(properties, dbAutoOpenProp(groupKey, DEFAULT), get(properties, dbAutoOpenProp(DEFAULT, DEFAULT)));
		}
		
		String[] dashboardKeys = discoverDashboardKeys(properties);
		for (String dashboardKey : dashboardKeys) {
			for (String groupKey : groupKeys) {
				set(properties, dbPermProp(groupKey, dashboardKey), get(properties, dbPermProp(DEFAULT, DEFAULT)));
				set(properties, dbAutoOpenProp(groupKey, dashboardKey), get(properties, dbAutoOpenProp(DEFAULT, DEFAULT)));
			}
		}
	}
	
	public static String dashboardProp(String dashboardKey) {
		return "config.dashboards." + dashboardKey + ".name";
	}
	
	private static String groupProp(String userGroupKey) {
		return "config.user.groups." + userGroupKey + ".name";
	}
	
	private static String dbPermProp(String userGroupKey, String dashboardKey) {
		return "config.user.groups." + userGroupKey + ".dashboards." + dashboardKey + ".permission";
	}
	
	private static String dbAutoOpenProp(String userGroupKey, String dashboardKey) {
		return "config.user.groups." + userGroupKey + ".dashboards." + dashboardKey + ".autoopen";
	}
	
	private static String profileRoleProp(String userGroupKey) {
		return "config.user.groups." + userGroupKey + ".profile.role";
	}
	
	private static String mgmtRoleProp(String userGroupKey) {
		return "config.user.groups." + userGroupKey + ".management.role";
	}
	
	public static Boolean dashboardAutoOpen(String userGroupKey, String dashboardKey) {
		Objects.requireNonNull(userGroupKey);
		Objects.requireNonNull(dashboardKey);
		return Boolean.valueOf(
			getDashboardAutoOpen(INSTANCE.properties, userGroupKey, dashboardKey)
		);
	}
	
	public static DashboardPermission dashboardPermission(String userGroupKey, String dashboardKey) {
		Objects.requireNonNull(userGroupKey);
		Objects.requireNonNull(dashboardKey);
		return DashboardPermission.valueOf(
			getDashboardPermission(INSTANCE.properties, userGroupKey, dashboardKey)
		);
	}
	
	private static String getDashboardAutoOpen(Properties properties, String userGroupKey, String dashboardKey) {
		Objects.requireNonNull(properties);
		Objects.requireNonNull(userGroupKey);
		Objects.requireNonNull(dashboardKey);
		String value = get(properties, dbAutoOpenProp(userGroupKey, dashboardKey));
		if (value != null) {
			return value;
		}
		value = get(properties, dbAutoOpenProp(userGroupKey, DEFAULT));
		if (value != null) {
			return value;
		}
		value = get(properties, dbAutoOpenProp(DEFAULT, dashboardKey));
		if (value != null) {
			return value;
		}
		return get(properties, dbAutoOpenProp(DEFAULT, DEFAULT));
	}
	
	private static String getDashboardPermission(Properties properties, String userGroupKey, String dashboardKey) {
		Objects.requireNonNull(properties);
		Objects.requireNonNull(userGroupKey);
		Objects.requireNonNull(dashboardKey);
		String value = get(properties, dbPermProp(userGroupKey, dashboardKey));
		if (value != null) {
			return value;
		}
		value = get(properties, dbPermProp(userGroupKey, DEFAULT));
		if (value != null) {
			return value;
		}
		value = get(properties, dbPermProp(DEFAULT, dashboardKey));
		if (value != null) {
			return value;
		}
		return get(properties, dbPermProp(DEFAULT, DEFAULT));
	}
	
	public static boolean areUserGroupsConfigured() {
		return userGroupKeys().length > 0;
	}
	
	public static String userGroup(String userGroupKey) {
		Objects.requireNonNull(userGroupKey);
		return getUserGroup(INSTANCE.properties, userGroupKey);
	}
	
	private static String getUserGroup(Properties properties, String userGroupKey) {
		Objects.requireNonNull(properties);
		Objects.requireNonNull(userGroupKey);
		return get(properties, groupProp(userGroupKey));
	}
	
	public static String managementRole(String userGroupKey) {
		Objects.requireNonNull(userGroupKey);
		return getManagementRole(INSTANCE.properties, userGroupKey);
	}
	
	private static String getManagementRole(Properties properties, String userGroupKey) {
		Objects.requireNonNull(properties);
		Objects.requireNonNull(userGroupKey);
		String value = get(properties, mgmtRoleProp(userGroupKey));
		if (value != null) {
			return value;
		}
		return get(properties, mgmtRoleProp(DEFAULT));
	}
	
	public static String profileRole(String userGroupKey) {
		Objects.requireNonNull(userGroupKey);
		return getProfileRole(INSTANCE.properties, userGroupKey);
	}
	
	private static String getProfileRole(Properties properties, String userGroupKey) {
		Objects.requireNonNull(properties);
		Objects.requireNonNull(userGroupKey);
		String value = get(properties, profileRoleProp(userGroupKey));
		if (value != null) {
			return value;
		}
		return get(properties, profileRoleProp(DEFAULT));
	}

	private static Config INSTANCE = init();
	
	private final ServerConfig serverConfig;
	private final Resources resources;
	private final String profileTemplate;
	private final String profile;
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
	private Config(Version version, Properties properties, ServerConfig serverConfig, Resources resources, String profileTemplate, String profile) {
		this.properties = properties;
		this.serverConfig = serverConfig;
		this.resources = resources;
		this.profileTemplate = profileTemplate;
		this.profile = profile;
		this.version = version;
		
		Collection<String> templateNames = new ArrayList<>();
		templateNames.addAll(resources.profiles.keySet());
		for (String templateName : templateNames) {
			ProfileTemplate template = resources.profiles.get(templateName);
			if (template == null) {
				continue;
			}
			Version templateVersion = template.getVersion();
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
				resources.profiles.remove(templateName);
			}
		}
		
		templateNames = new ArrayList<>();
		templateNames.addAll(resources.dashboards.keySet());
		for (String templateName : templateNames) {
			DashboardTemplate template = resources.dashboards.get(templateName);
			if (template == null) {
				continue;
			}
			Version templateVersion = template.getVersion();
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
	
	public static String profile() {
		return INSTANCE.profile;
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
	
	public static Map<String, ProfileTemplate> profileTemplates() {
		return resources().profiles;
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
	
	public static String dashboardTemplate(String dashboardKey) {
		String name = get(INSTANCE.properties, dashboardProp(dashboardKey));
		if (name == null) {
			return null;
		}
		if (name.endsWith(".dashboard.xml")) {
			return name.substring(0, name.length() - ".dashboard.xml".length());
		}
		return name;
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
			LOGGER.severe(
				"No Server Host given (-D" + PROP_HOST + "=<hostname[:port]>)"
			);
			return null;
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
	
	public static Config init() {
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
		
		
		String profileTemplateName = System.getProperty(PROP_PROFILE_TPL);
		if (profileTemplateName == null) {
			LOGGER.severe("No Profile Template to deploy given (-D" + PROP_PROFILE_TPL + "=<profilename>)");
			LOGGER.severe("  Possible values");
			Set<String> keySet = resources.profiles.keySet();
			for (String string : keySet) {
				LOGGER.severe("    " + string);
			}
			return null;
		}
		
		String profile = System.getProperty(PROP_PROFILE);

		return new Config(serverConfigResponse.getVersion(), System.getProperties(), serverConfig, resources, profileTemplateName, profile);
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
		Protocol protocol = Protocol.HTTP;
		if (port == 8021) {
			protocol = Protocol.HTTPS;
		}
		if (port == 8020) {
			protocol = Protocol.HTTP;
		}
		if (port == 80) {
			protocol = Protocol.HTTP;
		}
		if (port == 443) {
			protocol = Protocol.HTTPS;
		}
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
