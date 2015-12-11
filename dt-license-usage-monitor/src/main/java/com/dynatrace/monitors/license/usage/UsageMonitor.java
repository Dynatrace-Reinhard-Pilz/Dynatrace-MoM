package com.dynatrace.monitors.license.usage;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;
import java.util.Objects;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Document;

import com.dynatrace.diagnostics.pdk.Monitor;
import com.dynatrace.diagnostics.pdk.MonitorEnvironment;
import com.dynatrace.diagnostics.pdk.MonitorMeasure;
import com.dynatrace.diagnostics.pdk.PluginEnvironment.Host;
import com.dynatrace.diagnostics.pdk.Status;
import com.dynatrace.diagnostics.pdk.Status.StatusCode;
import com.dynatrace.diagnostics.util.ExceptionHelper;
import com.dynatrace.http.Http;
import com.dynatrace.http.HttpClient;
import com.dynatrace.http.Method;
import com.dynatrace.http.Protocol;
import com.dynatrace.http.config.ConnectionConfig;
import com.dynatrace.http.config.Credentials;
import com.dynatrace.http.config.ServerConfig;
import com.dynatrace.rest.Agent;
import com.dynatrace.rest.Agents;
import com.dynatrace.utils.DomUtil;

public class UsageMonitor implements Monitor {
	
	private static final Logger LOGGER =
			Logger.getLogger(UsageMonitor.class.getName());
	
	private static final long DEFAULT_MAX_AGE = 5;
	
	private static final String CONFIGID_USER =
			"com.dynatrace.monitors.usagemonitor.config.user";
	private static final String CONFIGID_PASS =
			"com.dynatrace.monitors.usagemonitor.config.pass";
	private static final String CONFIGID_PROTOCOL =
			"com.dynatrace.monitors.usagemonitor.config.protocol";
	private static final String CONFIGID_PORT =
			"com.dynatrace.monitors.usagemonitor.config.port";
	private static final String CONFIGID_MAX_AGE =
			"com.dynatrace.monitors.usagemonitor.config.maxProfileCacheAge";
	
	
	private final String METRIC_GROUPID_LICENSEUSAGE = "License Usage";
	private final String METRICID_USED_LICENSES = "Consumed Licenses";

	
	private final String KEY_APPLICATION_ID = "-appid";
	private final String META_INFO_KEY_APPLICATION = "app";
	private final String DYN_KEY_APPLICATION = "-application-";
	private final String META_INFO_KEY_BUSINESS_UNIT = "businessunit";
	private final String DYN_KEY_BUSINESS_UNIT = "-businessunit-";
	private final String META_INFO_KEY_PROJECT = "project";
	private final String DYN_KEY_PROJECT = "-project-";
	private final String META_INFO_KEY_ENVIRONMENT = "environment";
	private final String DYN_KEY_ENVIRONMENT = "-environment-";
	private final String DYN_KEY_TYPE = "-techtype-";
	private final String OTHER = "Other";
	
	final static ProfileCache PROFILE_CACHE = new ProfileCache();

	@Override
	public Status execute(MonitorEnvironment env) throws Exception {
		try {
			Objects.requireNonNull(env);
			Host host = env.getHost();
			if (host == null) {
				LOGGER.log(Level.SEVERE, "Configuration option for dynaTrace Server Host is null");
				return new Status(StatusCode.ErrorInternalConfigurationProblem, "configured host is null");
			}
			String address = host.getAddress();
			if (address == null) {
				LOGGER.log(Level.SEVERE, "Configuration option for dynaTrace Server Host Address is null");
				return new Status(StatusCode.ErrorInternalConfigurationProblem, "configured host address is null");
			}
			String sProtocol = env.getConfigString(CONFIGID_PROTOCOL);
			if (sProtocol == null) {
				LOGGER.log(Level.SEVERE, "Configuration option for dynaTrace Server HTTP Protocol is null");
				return new Status(StatusCode.ErrorInternalConfigurationProblem, "configured protocol is null");
			}
			int port = env.getConfigLong(CONFIGID_PORT).intValue();
			String user = env.getConfigString(CONFIGID_USER);
			if (user == null) {
				LOGGER.log(Level.SEVERE, "Configured user name is null");
				return new Status(StatusCode.ErrorInternalConfigurationProblem, "configured user name is null");
			}
			String pass = env.getConfigPassword(CONFIGID_PASS);
			if (pass == null) {
				LOGGER.log(Level.SEVERE, "Configured password is null");
				return new Status(StatusCode.ErrorInternalConfigurationProblem, "configured password is null");
			}
			long maxAge = DEFAULT_MAX_AGE;
			try {
				maxAge = env.getConfigLong(CONFIGID_MAX_AGE);
			} catch (Throwable t) {
				maxAge = DEFAULT_MAX_AGE;
			}
			if (maxAge < DEFAULT_MAX_AGE) {
				maxAge = DEFAULT_MAX_AGE;
			}
			
			Credentials credentials = new Credentials(user, pass);
			
	        Protocol protocol = Protocol.fromString(sProtocol);
	        ConnectionConfig connectionConfig = new ConnectionConfig(protocol, address, port);
	        ServerConfig serverConfig = new ServerConfig(connectionConfig, credentials);
			PROFILE_CACHE.refresh(serverConfig, 1000 * 60 * maxAge);
			
			HttpClient httpClient = Http.client();
			URL url = new URL(sProtocol, address, port, "/rest/management/agents");
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int responseCode = httpClient.request(url, Method.GET, credentials, out);
			if (responseCode != HttpURLConnection.HTTP_OK) {
				LOGGER.log(Level.SEVERE, "Server responded with status code " + responseCode);
				return new Status(StatusCode.ErrorInfrastructure, "Server responded with status code " + responseCode);
			}
			byte[] bytes = out.toByteArray();
			if (bytes.length == 0) {
				LOGGER.log(Level.SEVERE, "Server did not respond with data (response code: " + responseCode + ")");
				return new Status(StatusCode.ErrorInfrastructure, "Server did not respond with data (response code: " + responseCode + ")");
			}
			String xml = new String(out.toByteArray());
			Document document = DomUtil.build(xml);
			Agents agents = new Agents(document);
			
			LicenseStatistics applications = new LicenseStatistics("applications");
			LicenseStatistics projects = new LicenseStatistics("projects");
			LicenseStatistics environments = new LicenseStatistics("environments");
			LicenseStatistics bunits = new LicenseStatistics("bunits");
			LicenseStatistics agentTypes = new LicenseStatistics("type");
			
			for (Agent agent : agents) {
				if (agent == null) {
					continue;
				}
				String agentGroupId = agent.getAgentGroup();
				String systemProfileId = agent.getSystemProfile();
				if ("dynaTrace Self-Monitoring".equals(systemProfileId)) {
					continue;
				}
				String application = PROFILE_CACHE.getMetaInfo(
					systemProfileId,
					agentGroupId,
					META_INFO_KEY_APPLICATION,
					OTHER
				);
				if ((application == null) || OTHER.equals(application)) {
					application = PROFILE_CACHE.getMetaInfo(
						systemProfileId,
						agentGroupId,
						KEY_APPLICATION_ID,
						OTHER
					);
				}
				applications.inc(application);
				
				agentTypes.inc(agent.getTechnologyType());
				
				environments.inc(PROFILE_CACHE.getMetaInfo(
					systemProfileId,
					agentGroupId,
					META_INFO_KEY_ENVIRONMENT,
					OTHER
				));
				projects.inc(PROFILE_CACHE.getMetaInfo(
					systemProfileId,
					agentGroupId,
					META_INFO_KEY_PROJECT,
					OTHER
				));
				bunits.inc(PROFILE_CACHE.getMetaInfo(
					systemProfileId,
					agentGroupId,
					META_INFO_KEY_BUSINESS_UNIT,
					OTHER
				));
			}
			
			bookMeasures(
				env,
				applications,
				DYN_KEY_APPLICATION
			);

			bookMeasures(
				env,
				projects,
				DYN_KEY_PROJECT
			);

			bookMeasures(
				env,
				bunits,
				DYN_KEY_BUSINESS_UNIT
			);

			bookMeasures(
				env,
				environments,
				DYN_KEY_ENVIRONMENT
			);
			
			bookMeasures(
				env,
				agentTypes,
				DYN_KEY_TYPE
			);
						
	        
			return new Status(StatusCode.Success);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Execution failed", e);
			LOGGER.log(Level.SEVERE, "Execution failed", ExceptionHelper.stackTraceToString(e));
			return new Status(StatusCode.ErrorInternalException, "Execution failed", "Execution Failed", e);
		} finally {
			flush(LOGGER);
		}
	}
	
	private void flush(Logger logger) {
		if (logger == null) {
			return;
		}
		flush(logger.getHandlers());
		flush(logger.getParent());
	}
	
	private void flush(Handler handler) {
		if (handler == null) {
			return;
		}
		handler.flush();
	}
	
	private void flush(Handler[] handlers) {
		if (handlers == null) {
			return;
		}
		for (Handler handler : handlers) {
			flush(handler);
		}
	}

	private void bookMeasures(MonitorEnvironment env, LicenseStatistics statistics, String dynKey) {
		bookMeasures0(env, METRICID_USED_LICENSES, statistics, dynKey);
	}
	
	private void bookMeasures0(MonitorEnvironment env, String metricId, LicenseStatistics statistics, String dynKey) {
		if (env == null) {
			return;
		}
		if (metricId == null) {
			return;
		}
		if (statistics == null) {
			return;
		}
		Collection<MonitorMeasure> measures = env.getMonitorMeasures(
			METRIC_GROUPID_LICENSEUSAGE,
			metricId
		);
		if (measures == null) {
			return;
		}
		for (MonitorMeasure measure : measures) {
			String measureName = measure.getMeasureName();
			String metricName = measure.getMetricName();
			LOGGER.log(Level.INFO, "MEASURE[name='" + measureName + "', metric='" + metricName + "'");
			resetMeasurement(measure);
			Iterable<String> keys = statistics.getKeys();
			for (String key : keys) {
				MonitorMeasure dynamicMeasure =	env.createDynamicMeasure(
					measure,
					dynKey,
					key
				);
				dynamicMeasure.setValue(statistics.get(key));
			}
			MonitorMeasure global = env.createDynamicMeasure(measure, dynKey, dynKey);
			global.setValue(Double.NaN);
		}
	}
	
	private static void resetMeasurement(MonitorMeasure measure) {
		if (measure == null) {
			return;
		}
		Class<? extends MonitorMeasure> clazz = measure.getClass();
		Field[] fields = clazz.getDeclaredFields();
		if (fields == null) {
			return;
		}
		for (Field field : fields) {
			if (field == null) {
				continue;
			}
			Class<?> fieldType = field.getType();
			if (fieldType.equals(Number.class)) {
				field.setAccessible(true);
				try {
					field.set(measure, null);
				} catch (Throwable t) {
					LOGGER.log(Level.SEVERE, "Failed miserably", t);
					LOGGER.log(Level.SEVERE, stackTraceToString(t));
				}
			}
		}
	}

	@Override
	public Status setup(MonitorEnvironment env) throws Exception {
		return new Status(StatusCode.Success);
	}

	@Override
	public void teardown(MonitorEnvironment env) throws Exception {
		PROFILE_CACHE.clear();
	}
	
	public static final String stackTraceToString(Throwable throwable) {
		if (throwable != null) {
			try {
				StringWriter stringWriter = new StringWriter();
				try {
					PrintWriter printWriter = new PrintWriter(stringWriter);
					try {
						throwable.printStackTrace(printWriter);
						printWriter.flush();
					} finally {
						printWriter.close();
					}
				} finally {
					stringWriter.close();
				}
				return stringWriter.toString();
			} catch (Exception e) {
			}
		}

		return "";
	}
	

}
