package com.dynatrace.mom.runtime.components;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.dynatrace.agents.AgentAware;
import com.dynatrace.agents.AgentRefresh;
import com.dynatrace.collectors.CollectorCollection;
import com.dynatrace.collectors.CollectorRecord;
import com.dynatrace.collectors.CollectorRefresh;
import com.dynatrace.collectors.CollectorRestart;
import com.dynatrace.collectors.RestartStatus;
import com.dynatrace.dashboards.DashboardCollection;
import com.dynatrace.dashboards.DashboardRefresh;
import com.dynatrace.fastpacks.FastPackUpload;
import com.dynatrace.fixpacks.FixPack;
import com.dynatrace.fixpacks.FixPackAware;
import com.dynatrace.fixpacks.FixPackInstallStatus;
import com.dynatrace.fixpacks.FixPackStatus;
import com.dynatrace.fixpacks.FixPackUpload;
import com.dynatrace.http.ConnectionAware;
import com.dynatrace.http.ConnectionStatus;
import com.dynatrace.http.Protocol;
import com.dynatrace.http.Restartable;
import com.dynatrace.http.ServerRestart;
import com.dynatrace.http.VersionRefresh;
import com.dynatrace.http.config.ConnectionConfig;
import com.dynatrace.http.config.Credentials;
import com.dynatrace.http.config.ServerConfig;
import com.dynatrace.incidents.Incident;
import com.dynatrace.incidents.IncidentAware;
import com.dynatrace.incidents.IncidentReference;
import com.dynatrace.incidents.IncidentRefresh;
import com.dynatrace.incidents.IncidentRule;
import com.dynatrace.incidents.IncidentRuleRefresh;
import com.dynatrace.incidents.IncidentState;
import com.dynatrace.license.LicenseAware;
import com.dynatrace.license.LicenseInfo;
import com.dynatrace.license.LicenseRefresh;
import com.dynatrace.mom.connector.MomConnectorAware;
import com.dynatrace.mom.connector.RefreshMoMConnectorVersion;
import com.dynatrace.mom.connector.RefreshSelfMonitoringProfile;
import com.dynatrace.mom.rest.ContextConstants;
import com.dynatrace.mom.runtime.ServerRepository;
import com.dynatrace.mom.runtime.XMLServerRepository;
import com.dynatrace.profiles.ProfileCollection;
import com.dynatrace.profiles.ProfileRefresh;
import com.dynatrace.profiles.SystemProfile;
import com.dynatrace.reporting.Availability;
import com.dynatrace.reporting.HealthDashboardAware;
import com.dynatrace.reporting.HealthRefresh;
import com.dynatrace.reporting.Measure;
import com.dynatrace.reporting.MeasureAware;
import com.dynatrace.sysinfo.SysInfoRefresh;
import com.dynatrace.utils.Closeables;
import com.dynatrace.utils.DefaultExecutionContext;
import com.dynatrace.utils.ExecutionContext;
import com.dynatrace.utils.Unchecked;
import com.dynatrace.utils.Version;
import com.dynatrace.utils.Versionable;
import com.dynatrace.xml.XMLUtil;

@XmlRootElement(name = ServerRecord.TAG)
@XmlAccessorType(XmlAccessType.PROPERTY)
public class ServerContext
	extends DefaultExecutionContext
	implements ThreadFactory, ConnectionAware, Versionable, LicenseAware, HealthDashboardAware, Restartable, FixPackAware, IncidentAware, MeasureAware, MomConnectorAware  {

	private static final Logger LOGGER =
			Logger.getLogger(ServerContext.class.getName());
	
	private final ExecutorService executorService =
			Executors.newSingleThreadExecutor(this);
	
	private final ServerRecord serverRecord;
	private final ExecutionContext ctx;
	
	public ServerContext(ServerRecord serverRecord, ExecutionContext ctx) {
		Objects.requireNonNull(ctx);
		Objects.requireNonNull(serverRecord);
		this.serverRecord = serverRecord;
		this.ctx = ctx;
		this.ctx.register(this, serverRecord.name());
		setAttribute(ServerConfig.class, serverRecord.getConfig());
		setAttribute(
			CollectorCollection.class,
			(CollectorCollection) serverRecord.getCollectors()
		);
		setAttribute(
			ProfileCollection.class,
			(ProfileCollection) serverRecord.getProfiles()
		);
		setAttribute(
			DashboardCollection.class,
			(DashboardCollection) serverRecord.getDashboards()
		);
		setAttribute(
			Versionable.class,
			this
		);
		setAttribute(LicenseAware.class, this);
		setAttribute(AgentAware.class, serverRecord);
	}
	
	public ExecutionContext getContext() {
		return this;
	}
	
	public File getDataFolder() {
		File serversHome = (File) getAttribute(
			ContextConstants.FLD_SERVERS_HOME
		);
		File fld = new File(serversHome, getConnectionConfig().getHost());
		fld.mkdirs();
		return fld;
	}
	
	public File getProfileStorage() {
		File fld = new File(getDataFolder(), "profiles");
		fld.mkdirs();
		return fld;
	}
	
	public File getDashboardStorage() {
		File fld = new File(getDataFolder(), "dashboards");
		fld.mkdirs();
		return fld;
	}
	
	public final ServerRecord getServerRecord() {
		return serverRecord;
	}
	
	public String getHost() {
		return getConnectionConfig().getHost();
	}
	
	public int getPort() {
		return getConnectionConfig().getPort();
	}
	
	public Protocol getProtocol() {
		return getConnectionConfig().getProtocol();
	}
	
	public ConnectionConfig getConnectionConfig() {
		return getServerConfig().getConnectionConfig();
	}
	
	public Credentials getCredentials() {
		return getServerConfig().getCredentials();
	}
	
	public ServerConfig getServerConfig() {
		return serverRecord.getConfig();
	}
	
	public void refreshVersion() {
		LOGGER.log(Level.FINER, "refreshing version [" + getName() + "]");
		execute(new VersionRefresh(this, getServerConfig()));
	}
	
	public void refreshMoMConnectorVersion() {
		execute(new RefreshMoMConnectorVersion(this, getServerConfig()));
	}
	
	public void refreshSelfMonitoringProfile() {
		LOGGER.log(Level.FINER, "refreshing [" + getName() + "] dynaTrace Self-Monitoring.profile.xml");
		execute(new RefreshSelfMonitoringProfile(this, getServerConfig()) {
			@Override
			public void onSystemProfile(SystemProfile systemProfile) {
				if (systemProfile == null) {
					return;
				}
				SystemProfile profile = serverRecord.getProfile(
					systemProfile.getId()
				);
				LOGGER.log(Level.FINER, "[" + getName() + "] dynaTrace Self-Monitoring.profile.xml received");
				if (profile == null) {
					serverRecord.addProfile(systemProfile);
				} else {
					Closeables.delete(profile.getLocalFile());
					profile.setLocalFile(systemProfile.getLocalFile());
				}
			}
		});
	}
	
	public void refreshCollectors() {
		LOGGER.log(Level.FINER, "refreshing collectors [" + getName() + "]");
		execute(new CollectorRefresh(this, getServerConfig()));
	}
	
	public void installFixpack(FixPack fixPack) {
		LOGGER.log(Level.INFO,  "installing fixpack " + fixPack.getVersion() + " [" + getName() + "]");
		execute(new FixPackUpload(this, getServerConfig(), fixPack));
	}
	
	public void uploadHealthDashboard() {
		LOGGER.log(Level.INFO, "uploading health dashboard [" + getName() + "]");
		execute(new FastPackUpload(this, getServerConfig()) {
			
			@Override
			protected boolean prepare() {
				if (!super.prepare()) {
					return false;
				}
				Availability availability = getHealthDashboardAvailability();
				if (availability == Availability.Available) {
					return false;
				}
				return true;
			}
			
			@Override
			public void onSuccess() {
				setHealthDashboardAvailability(Availability.Available);
			}
		});
	}
	
	public void uploadMomConnector() {
		if (getMoMConnectorAvailability() != Availability.NotYetAvailable) {
			return;
		}
		LOGGER.log(Level.INFO, "Uploading MoM Connector [" + getName() + "]");
		execute(new FastPackUpload(this, getServerConfig()) {
			
			@Override
			protected InputStream openStream() throws IOException {
				return this.getClass().getClassLoader().getResourceAsStream("/com.dynatrace.mom.connector.fastpack.dtp");
			}
			
			@Override
			protected boolean prepare() {
				if (!super.prepare()) {
					return false;
				}
				switch (getMoMConnectorAvailability()) {
				case Available:
				case Determining:
				case unknown:
				case Unavailable:
					return false;
				case NotYetAvailable:
					break;
				}
				return true;
			}
			
			@Override
			public void onSuccess() {
				setMoMConnectorAvailability(Availability.Available);
			}
		});
	}
	
	public void refreshDashboardStatus() {
		LOGGER.log(Level.FINER, "refreshing dashboard status [" + getName() + "]");
		execute(new DashboardRefresh(this, getServerConfig()));
	}
	
	public void refreshLicense() {
		LOGGER.log(Level.FINER, "refreshing licenses [" + getName() + "]");
		execute(new LicenseRefresh(this, getServerConfig()));
	}
	
	public void refreshProfiles() {
		LOGGER.log(Level.FINER, "refreshing profiles [" + getName() + "]");
		execute(new ProfileRefresh(this, getServerConfig()));
	}
	
	public void refreshDynatraceServerHealthMeasures() {
		LOGGER.log(Level.FINER, "refreshing health measures [" + getName() + "]");
		execute(new HealthRefresh(this, getServerConfig()));
	}
	
	public void refreshConfigs() {
		LOGGER.log(Level.FINER, "refreshing configs [" + getName() + "]");
		execute(new SysInfoRefresh(this, getServerConfig()) {
			
			protected String[] getSupportedFileTypes() {
				return new String[] {
						"configfiles",
						"agentrecords",
						"licensefile",
						"componentproperties"	
					};
			}
			
			@Override
			public void onSuccess() {
				refreshIncidentReferences();
			}
			
			@Override
			public void onSystemProfile(SystemProfile systemProfile) {
				SystemProfile profile = serverRecord.getProfile(
					systemProfile.getId()
				);
				if (profile == null) {
					serverRecord.addProfile(systemProfile);
				} else {
					profile.setLocalFile(systemProfile.getLocalFile());
				}
			}
			
			@Override
			public void onCollectorVersion(String name, String host, Version version) {
				if (!Version.isValid(version)) {
					return;
				}
				CollectorRecord collectorRecord =
						serverRecord.getCollector(name, host);
				if (collectorRecord != null) {
					collectorRecord.updateVersion(version);
				}
			}
			
			@Override
			public void onCollectorStartupTime(String name, String host, long time) {
				if (time == 0L) {
					return;
				}
				CollectorRecord collectorRecord =
					serverRecord.getCollector(name, host);
				if (collectorRecord == null) {
					return;
				}
				collectorRecord.setStartupTime(time);
			}
			
			@Override
			public void onServerFQDN(String fqdn) {
				ServerRepository repo =
						ctx.getAttribute(ServerRepository.class);
				if (repo.rename(serverRecord, fqdn)) {
					store();
				}
			}
			
		});
	}
	
	public void store() {
		ServerRepository repo =
				ctx.getAttribute(ServerRepository.class);
			XMLServerRepository xmlServerRepository = new XMLServerRepository();
			Collection<ServerRecord> serverRecords = repo.getServerRecords();
			Collection<ServerRecord> clonedServerRecords = new ArrayList<>();
			for (ServerRecord serverRecord : serverRecords) {
				ServerRecord clonedServerRecord = serverRecord.clone();
				ctx.log(Level.INFO, serverRecord.name());
				clonedServerRecord.encrypt();
				clonedServerRecords.add(clonedServerRecord);
			}
			xmlServerRepository.setServerRecords(clonedServerRecords);
			File storageFolder = ctx.getStorageFolder();
			storageFolder.mkdirs();
			File storageServersXml = new File(storageFolder, "servers.xml");
			ctx.log(Level.INFO, storageServersXml.toString());
			try (OutputStream out = new FileOutputStream(storageServersXml)) {
				XMLUtil.serialize(xmlServerRepository, out);
			} catch (IOException e) {
				ctx.log(Level.WARNING, "Unable to persist servers.xml");
			}
	}
	
	@Override
	public void refreshIncidentReferences() {
//		LOGGER.log(Level.INFO, "refreshIncidentReferences");
		Collection<IncidentRule> incidentRules = serverRecord.getIncidentRules();
		if (incidentRules.isEmpty()) {
			return;
		}
		execute(new IncidentRuleRefresh(this, getServerConfig(), incidentRules));
	}
	
	public void refreshRuleBasedIncidents() {
		for (IncidentRule incidentRule : serverRecord.getIncidentRules()) {
			refreshIncidents(incidentRule);
		}
	}
	
	public void refreshIncidents(IncidentRule incidentRule) {
		if (incidentRule == null) {
			return;
		}
		for (IncidentReference incidentReference : incidentRule) {
			if (incidentReference == null) {
				continue;
			}
			Incident incident = incidentReference.getIncident();
			if ((incident != null) && (incident.getState() == IncidentState.Confirmed)) {
				continue;
			}
			LOGGER.log(Level.FINE, "refreshing incident references [" + getName() + "][" + incidentRule.getId() + "][" + incidentReference.getId() + "]");
			execute(new IncidentRefresh(this, getServerConfig(), incidentRule, incidentReference));
		}
	}

	public void restart() {
		execute(new ServerRestart(this, getServerConfig()));
	}
	
	public void refreshAgents() {
		execute(new AgentRefresh(this, getServerConfig()));
	}
	
	public synchronized void restartCollector(String name, String host) {
		if (name == null) {
			return;
		}
		CollectorRecord collector = serverRecord.getCollector(name, host);
		if (collector == null) {
			return;
		}
		if (collector.getRestartStatus() != RestartStatus.SCHEDULED) {
			return;
		}
		if (!collector.isConnected()) {
			return;
		}
		execute(new CollectorRestart(this, getServerConfig(), name, host));
		// asap after collector restarts we would like to know if they now have
		// the correct version
		refreshConfigs();
	}
	
	public final String getName() {
		return serverRecord.name();
	}
	
	public final void close() {
		store();		
		serverRecord.close();
		LOGGER.log(Level.INFO, "Shutting down " + toString());
		try {
			executorService.shutdownNow();
			boolean success = executorService.awaitTermination(20, TimeUnit.SECONDS);
			if (!success) {
				LOGGER.log(Level.WARNING, "[" + toString() + "] Unable to await termination of Executor Service - shutting down ungracefully");
			}
			
		} catch (InterruptedException e) {
			return;
		}
	}
	
	
	@Override
	public final Thread newThread(final Runnable runnable) {
		final Thread thread = new Thread(
				runnable,
				ServerRecord.class.getSimpleName() +
				"[" + serverRecord.name() + "][" + ServerContext.class.getSimpleName() + "]"
		);
		thread.setDaemon(true);
		return thread;
	}
	
	@Override
	public final String toString() {
		return ServerContext.class.getSimpleName() + "[" + getName() + "]";
	}

	@Override
	public <T> T getAttribute(String name) {
		T self = super.<T>getAttribute(name);
		if (self != null) {
			return self;
		}
		return ctx.<T>getAttribute(name);
	}

	@Override
	public String getContextPath() {
		return ctx.getContextPath();
	}
	
	@Override
	protected Logger logger() {
		return LOGGER;
	}
	
	private HashMap<Integer, Runnable> runningTasks = new HashMap<>();

	@Override
	public void execute(Runnable command) {
		if (command == null) {
			return;
		}
		int hashCode = command.hashCode();
		Runnable runningTask = runningTasks.get(hashCode);
		String commandToString = command.toString();
		if (commandToString.trim().isEmpty()) {
			commandToString = command.getClass().getName();
		}
		if (runningTask != null) {
			LOGGER.log(Level.WARNING, "[" + getName() + "] An identical task of class " + commandToString + " is already executing - discarding duplicate");
			return;
		}
		LOGGER.log(Level.FINE, "[" + getName() + "] Scheduling " + commandToString + " (queue size: " + runningTasks.size() + ")");
		executorService.execute(new ContextTask(command));
	}
	
	private class ContextTask implements Runnable {
		
		private final Runnable runnable;
		
		public ContextTask(Runnable runnable) {
			Objects.requireNonNull(runnable);
			this.runnable = runnable;
		}
		
		@Override
		public void run() {
			String commandToString = runnable.toString();
			if (commandToString.trim().isEmpty()) {
				commandToString = runnable.getClass().getName();
			}
			try {
				LOGGER.log(Level.FINE, "[" + getName() + "] Executing " + commandToString);
				runnable.run();
			} finally {
				runningTasks.remove(runnable.hashCode());
				LOGGER.log(Level.FINER, "[" + getName() + "] Finished " + commandToString);
			}
		}
		
	}
	
	@Override
	public <T> T getAttribute(Class<T> c) {
		if (c.isAssignableFrom(ServerContext.class)) {
			return Unchecked.cast(this);
		}
		return super.getAttribute(c);
	}
	
	@Override
	public synchronized File getStorageFolder() {
		File storageFolder = super.getStorageFolder();
		File serversFolder = new File(storageFolder, "servers");
		serversFolder.mkdirs();
		String host = getConnectionConfig().getHost();
		File hostFolder = new File(serversFolder, host);
		hostFolder.mkdirs();
		int port = getConnectionConfig().getPort();
		File portFolder = new File(hostFolder, String.valueOf(port));
		portFolder.mkdirs();
		return portFolder;
	}

	@Override
	public ConnectionStatus getConnectionStatus() {
		return serverRecord.getConnectionStatus();
	}

	@Override
	public void setConnectionStatus(ConnectionStatus status) {
		serverRecord.setConnectionStatus(status);		
	}

	@Override
	public int compareTo(Versionable o) {
		return serverRecord.compareTo(o);
	}

	@Override
	public Version getVersion() {
		return serverRecord.getVersion();
	}

	@Override
	public boolean includes(Versionable versionable) {
		return serverRecord.includes(versionable);
	}

	@Override
	public void updateVersion(Version version) {
		serverRecord.updateVersion(version);
	}
	
	@Override
	public void setLicenseInfo(LicenseInfo licenseInfo) {
		serverRecord.setLicenseInfo(licenseInfo);
	}

	@Override
	public void setHealthDashboardAvailability(Availability availability) {
		serverRecord.setHealthDashboardAvailability(availability);
	}

	@Override
	public Availability getHealthDashboardAvailability() {
		return serverRecord.getHealthDashboardAvailability();
	}
	
	@Override
	public Availability getMoMConnectorAvailability() {
		return serverRecord.getMomConnectorAvailability();
	}
	
	@Override
	public void setMoMConnectorAvailability(Availability availability) {
		synchronized (this) {
			serverRecord.setMomConnectorAvailability(availability);
		}
	}
	
	@Override
	public void setMoMConnectorVersion(Version version) {
		LOGGER.log(Level.INFO, "[" + getName() + "] MoM Connector Version " + version + " available.");
		synchronized (this) {
			serverRecord.setMoMConnectorVersion(version);
			Availability connectorAvailability = serverRecord.getMomConnectorAvailability();
			if (connectorAvailability != Availability.Available) {
				serverRecord.setMomConnectorAvailability(Availability.Available);
				refreshSelfMonitoringProfile();
			}
		}
	}

	public Version getMoMConnectorVersion() {
		return serverRecord.getMoMConnectorVersion();
	}

	@Override
	public void updateFixPackState(FixPack fixPack, FixPackStatus status) {
		serverRecord.updateFixPackState(fixPack, status);
	}

	@Override
	public FixPackStatus getFixpackStatus(FixPack fixPack) {
		return serverRecord.getFixpackStatus(fixPack);
	}

	@Override
	public IncidentRule getIncidentRule(String name) {
		return serverRecord.getIncidentRule(name);
	}

	@Override
	public void setMeasures(Collection<Measure> measures, String dashletName) {
		serverRecord.setMeasures(measures, dashletName);
	}

	@Override
	public void setFixPackInstallStatus(FixPackInstallStatus status) {
		serverRecord.setFixPackInstallStatus(status);
	}

	@Override
	public boolean equals(Versionable versionable) {
		return getVersion().equals(versionable);
	}
	
	public void rename(String name) {
		this.ctx.unregister(serverRecord.name());
		serverRecord.setName(name);
		this.ctx.register(this, name);
	}

}
