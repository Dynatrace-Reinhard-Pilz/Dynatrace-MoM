package com.dynatrace.monitors.license.usage;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
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
import com.dynatrace.profiles.SystemProfile;
import com.dynatrace.profiles.metainfo.MetaInfo;
import com.dynatrace.rest.Agent;
import com.dynatrace.rest.Agents;
import com.dynatrace.utils.DomUtil;
import com.dynatrace.utils.Strings;

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
			
			StatisticsMap statisticsMap = new StatisticsMap();
			
			for (Agent agent : agents) {
				if (agent == null) {
					continue;
				}
				if (agent.isMasterAgent()) {
					continue;
				}
				String agentGroupId = agent.getAgentGroup();
				String systemProfileId = agent.getSystemProfile();
				
				if ("dynaTrace Self-Monitoring".equals(systemProfileId)) {
					continue;
				}
//				LOGGER.log(Level.INFO, "agentGroupId: " + agentGroupId);
//				LOGGER.log(Level.INFO, "systemProfileId: " + systemProfileId);
				SystemProfile systemProfile = PROFILE_CACHE.get(systemProfileId);
				MetaInfo metaInfo = systemProfile.getMetaInfo();
				Iterable<String> metaInfoKeys = metaInfo.keys();
				for (String metaInfoKey : metaInfoKeys) {
					if (Strings.isNullOrEmpty(metaInfoKey)) {
						continue;
					}
//					LOGGER.log(Level.INFO, "    metaInfoKey: " + metaInfoKey);
					LicenseStatistics statistics =
						statisticsMap.get(metaInfoKey);
					String metaInfoValue = PROFILE_CACHE.getMetaInfo(
						systemProfileId,
						agentGroupId,
						metaInfoKey,
						OTHER
					);
					if (Strings.isNullOrEmpty(metaInfoValue)) {
						metaInfoValue = OTHER;
					}
					statistics.inc(metaInfoValue);
				}
				LicenseStatistics techTypeStatistics =
						statisticsMap.get("techType");
				String techType = agent.getTechnologyType();
				if (Strings.isNullOrEmpty(techType)) {
					techType = OTHER;
				}
				techTypeStatistics.inc(techType);
			}
			
			Iterable<LicenseStatistics> statisticsList = statisticsMap.getStatistics();
			for (LicenseStatistics statistics : statisticsList) {
				if (statistics == null) {
					continue;
				}
				bookMeasures(env, statistics, statistics.getId());
			}
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
//			String measureName = measure.getMeasureName();
//			String metricName = measure.getMetricName();
//			LOGGER.log(Level.INFO, "MEASURE[name='" + measureName + "', metric='" + metricName + "'");
			Iterable<String> keys = statistics.getKeys();
			for (String key : keys) {
//				LOGGER.log(Level.INFO, "env.createDynamicMeasure(" + measure + ", \"" + dynKey + "\", \"" + key + "\")");
				MonitorMeasure dynamicMeasure =	env.createDynamicMeasure(
					measure,
					dynKey,
					key
				);
//				LOGGER.log(Level.INFO, "dynamicMeasure.setValue(" + statistics.get(key) + ")");
				dynamicMeasure.setValue(statistics.get(key));
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
