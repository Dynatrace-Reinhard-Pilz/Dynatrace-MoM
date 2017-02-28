package com.dynatrace.mom.runtime.components;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.dynatrace.agents.AgentAware;
import com.dynatrace.agents.AgentCollection;
import com.dynatrace.agents.AgentInfo;
import com.dynatrace.agents.DefaultAgentCollection;
import com.dynatrace.collectors.CollectorCollection;
import com.dynatrace.collectors.CollectorInfo;
import com.dynatrace.collectors.CollectorRecord;
import com.dynatrace.collectors.DefaultCollectorCollection;
import com.dynatrace.collectors.RestartStatus;
import com.dynatrace.dashboards.Dashboard;
import com.dynatrace.dashboards.DashboardCollection;
import com.dynatrace.dashboards.DefaultDashboardCollection;
import com.dynatrace.fixpacks.FixPack;
import com.dynatrace.fixpacks.FixPackAware;
import com.dynatrace.fixpacks.FixPackInstallStatus;
import com.dynatrace.fixpacks.FixPackStatus;
import com.dynatrace.http.ConnectionAware;
import com.dynatrace.http.ConnectionStatus;
import com.dynatrace.http.config.ServerConfig;
import com.dynatrace.incidents.Incident;
import com.dynatrace.incidents.IncidentAware;
import com.dynatrace.incidents.IncidentRule;
import com.dynatrace.license.LicenseAware;
import com.dynatrace.license.LicenseInfo;
import com.dynatrace.mom.PWHChecker;
import com.dynatrace.mom.web.FixPackState;
import com.dynatrace.profiles.DefaultProfileCollection;
import com.dynatrace.profiles.ProfileCollection;
import com.dynatrace.profiles.SystemProfile;
import com.dynatrace.reporting.Availability;
import com.dynatrace.reporting.HealthDashboardAware;
import com.dynatrace.reporting.Measure;
import com.dynatrace.reporting.MeasureAware;
import com.dynatrace.utils.Iterables;
import com.dynatrace.utils.Labelled;
import com.dynatrace.utils.Batch;
import com.dynatrace.utils.Strings;
import com.dynatrace.utils.Version;
import com.dynatrace.utils.Versionable;

@XmlRootElement(name = ServerRecord.TAG)
@XmlAccessorType(XmlAccessType.PROPERTY)
public class ServerRecord
implements Versionable, ConnectionAware, LicenseAware, HealthDashboardAware, FixPackAware, AgentAware, IncidentAware, MeasureAware, Labelled, Closeable, Cloneable {
	
	private static final Logger LOGGER =
			Logger.getLogger(ServerRecord.class.getName());
	
	public static final String TAG = "server";

	private ServerConfig config;
	private Version version = Version.UNDEFINED;
	private LicenseInfo licenseInfo = new LicenseInfo();
	private final CollectorCollection collectors =
			new DefaultCollectorCollection(this);
	private FixPackInstallStatus fixPackInstallStatus = null;
	private boolean isVersionOutdated = false;
	private final PWHInactiveIncident pwhCheckIncident = new PWHInactiveIncident();
	private PWHChecker pwhChecker = PWHChecker.NULL;
	
	public void encrypt() {
		if (config == null) {
			return;
		}
		config.encrypt();
	}

	public void decrypt() {
		if (config == null) {
			return;
		}
		config.decrypt();
	}
	
	public long getPWHDelayInSeconds() {
		return pwhChecker.getDelayInSeconds();
	}
	
	public PWHChecker.DelaySeverity getPWHSeverity() {
		return pwhChecker.getSeverity();
	}
	
	public String getLastFormattedPWHTimeStamp() {
		return pwhChecker.getLastFormattedTimeStamp();
	}
	
	public long getLastPWHTimeStamp() {
		return pwhChecker.getLastTimeStamp();
	}
	
	public void setVersionOutdated(boolean isVersionOutdated) {
		synchronized (this) {
			this.isVersionOutdated = isVersionOutdated;
		}
	}
	
	@XmlTransient
	public boolean isVersionOutdated() {
		synchronized (this) {
			return isVersionOutdated;
		}
	}
	
	private AgentCollection agents = new DefaultAgentCollection() {
		
		@Override
		public void onAgentAdded(AgentInfo agentInfo) {
			LOGGER.log(Level.FINE, "Agent " + agentInfo.getInstanceName() + " added");
		}
		
		@Override
		public void onAgentRemoved(AgentInfo agentInfo) {
			LOGGER.log(Level.FINE, "Agent " + agentInfo.getInstanceName() + " removed");
		}
		
		@Override
		public void onAgentUpdated(AgentInfo agentInfo) {
			LOGGER.log(Level.FINE, "Agent " + agentInfo.getInstanceName() + " updated");
		}
		
	};
	private Availability healthDashboardAvailability = Availability.unknown;
	private Availability momConnectorAvailablitiy = Availability.unknown;
	private final HashMap<String,Collection<Measure>> dtServerHealthMeasures =
			new HashMap<String,Collection<Measure>>();
	private final DashboardCollection dashboards =
			new DefaultDashboardCollection() {
		
		public void addAll(Iterable<Dashboard> dashboards) {
			super.addAll(dashboards);
			if (getHealthDashboardAvailability() == Availability.unknown) {
				setHealthDashboardAvailability(Availability.Unavailable);
			}
		}
		
		@Override
		public void onDashboardAdded(Dashboard dashboard) {
			if (dashboard == null) {
				return;
			}
			String dashboardName = dashboard.getId();
//			LOGGER.log(Level.INFO, "[" + getName() + "'] DASHBOARD: " + dashboardName);
			if ("MoM dynaTrace Server Health".equals(dashboardName)) {
				synchronized (ServerRecord.this) {
					setHealthDashboardAvailability(Availability.Available);
					LOGGER.log(Level.FINE, "Health Dashboard AVAILABLE");
				}
			}
			LOGGER.log(Level.FINE, "Dashboard " + dashboard.getId() + " added");
		}
		
		@Override
		public void onDashboadUpdated(Dashboard dashboard) {
			if (dashboard == null) {
				return;
			}
			LOGGER.log(Level.FINE, "Dashboard " + dashboard.getId() + " updated");
		}
		
		@Override
		public void onDashboardRemoved(Dashboard dashboard) {
			if (dashboard == null) {
				return;
			}
			String dashboardName = dashboard.getId();
			if ("MoM dynaTrace Server Health".equals(dashboardName)) {
				synchronized (ServerRecord.this) {
					healthDashboardAvailability = Availability.Unavailable;
					LOGGER.log(Level.INFO, "Health Dashboard UNAVAILABLE (removed)");
				}
			}
			LOGGER.log(Level.FINE, "Dashboard " + dashboardName + " removed");
		}
	};
	private final ProfileCollection profiles =
			new DefaultProfileCollection() {
		
		@Override
		public void onProfileAdded(SystemProfile profile) {
			if (profile == null) {
				return;
			}
			LOGGER.log(Level.FINE, "SystemProfile " + profile.getId() + " added");
		}
		
		@Override
		public void onProfileUpdated(SystemProfile profile) {
			if (profile == null) {
				return;
			}
			LOGGER.log(Level.FINE, "SystemProfile " + profile.getId() + " updated");
		}
		
		@Override
		public void onProfileRemoved(SystemProfile profile) {
			if (profile == null) {
				return;
			}
			LOGGER.log(Level.FINE, "SystemProfile " + profile.getId() + " removed");
		}
		
	};
	private final Map<FixPack, FixPackStatus> fixPackStates =
			new HashMap<FixPack, FixPackStatus>();
	private String name = null;
	private ConnectionStatus connectionStatus = ConnectionStatus.OFFLINE;

	private Version momConnectorVersion = Version.UNDEFINED;
	
	public ServerRecord() {
		this.config = null;
	}
	
	public ServerRecord(ServerConfig config) {
		Objects.requireNonNull(version);
		this.config = config;
		if (this.pwhChecker != null) {
			LOGGER.info("this.pwhChecker.close();");
			this.pwhChecker.close();
			LOGGER.info("/this.pwhChecker.close();");
			try {
				if (pwhChecker.isAlive()) {
					LOGGER.info("pwhChecker.join();");
					pwhChecker.join();
					LOGGER.info("/pwhChecker.join();");
				}
			} catch (InterruptedException e) {
				// ignore
			}
		}
		this.pwhChecker = new PWHChecker(this, config.getPwhConfig());
	}
	
	@XmlTransient
	public Collection<FixPackState> getFixPackStates() {
		synchronized (this) {
			ArrayList<FixPackState> result = new ArrayList<FixPackState>();
			Set<FixPack> fixPacks = this.fixPackStates.keySet();
			for (FixPack fixPack : fixPacks) {
				result.add(new FixPackState(fixPack, fixPackStates.get(fixPack)));
			}
			return result;
		}
	}
	
	@Override
	public void setHealthDashboardAvailability(Availability availability) {
		synchronized (this) {
			this.healthDashboardAvailability = availability;
		}
	}
	
	@XmlTransient
	@Override
	public Availability getHealthDashboardAvailability() {
		synchronized (this) {
			return healthDashboardAvailability;
		}
	}
	
	@XmlTransient
	public Availability getMomConnectorAvailability() {
		synchronized (this) {
			return momConnectorAvailablitiy;
		}
	}
	
	public void setMomConnectorAvailability(Availability availability) {
		synchronized (this) {
			this.momConnectorAvailablitiy = availability;
		}
	}
	
	public void setLicenseInformation(LicenseInfo licenseInformation) {
		synchronized (this) {
			this.licenseInfo = licenseInformation;
		}
	}
	
	@XmlTransient
	public Collection<String> getChartNames() {
		synchronized (dtServerHealthMeasures) {
			return this.dtServerHealthMeasures.keySet();
		}
	}
	
	@Override
	public void setMeasures(Collection<Measure> measures, String dashletName) {
		synchronized (this) {
			final Collection<Measure> storedMeasures =
					this.dtServerHealthMeasures.get(dashletName);
			if (storedMeasures == null) {
				final ArrayList<Measure> newMeasures =
						new ArrayList<Measure>(measures);
				dtServerHealthMeasures.put(dashletName, newMeasures);
			} else {
				storedMeasures.clear();
				storedMeasures.addAll(measures);
			}
		}
	}
	
	public Collection<Measure> getMeasures(String dashletName) {
		synchronized (this) {
			final Collection<Measure> measures =
					this.dtServerHealthMeasures.get(dashletName);
			if (measures == null) {
				return Collections.emptyList();
			}
			return new ArrayList<Measure>(measures);
		}
	}
	
	public void setIncidentRules(Map<String, IncidentRule> incidentRules) {
		synchronized (this) {
			SystemProfile smp = profiles.getSelfMonitoringProfile();
			if (smp == null) {
				return;
			}
			Iterables.merge(
					profiles.getSelfMonitoringProfile().getIncidentRuleMap(),
				incidentRules
			);
		}
	}
	
	@XmlTransient
	public Collection<Incident> getOpenIncidents() {
		synchronized (this) {
			SystemProfile smp = profiles.getSelfMonitoringProfile();
			if (smp == null) {
				return Collections.emptyList();
			}
			Collection<Incident> openIncidents =
					IncidentRule.getOpenIncidents(smp.getIncidentRules());
			if (pwhCheckIncident.isOpen()) {
				openIncidents.add(pwhCheckIncident);
			}
			return openIncidents;
		}
	}
	
	public boolean isPWHConnected() {
		synchronized (this) {
			Collection<Incident> incidents = getOpenIncidents();
			if (!Iterables.isNullOrEmpty(incidents)) {
				for (Incident incident : incidents) {
					if (incident == null) {
						continue;
					}
					String incidentId = incident.getId();
					if (incidentId == null) {
						continue;
					}
					if (Strings.contains(incident.getMessage(), "Performance Warehouse is offline")) {
						if (incident.isOpen()) {
							return false;
						}
					}
				}
			}
			return true;
		}
	}
	
	@XmlTransient
	public int getOpenIncidentCount() {
		int openIncidentCount = 0;
		Iterable<IncidentRule> incidentRules = getIncidentRules();
		for (IncidentRule rule : incidentRules) {
			openIncidentCount += IncidentRule.getOpenIncidentCount(rule);
		}
		return openIncidentCount;
	}
	
	@XmlTransient
	public Collection<IncidentRule> getIncidentRules() {
		synchronized (this) {
			SystemProfile smp = profiles.getSelfMonitoringProfile();
			if (smp == null) {
				return Collections.emptyList();
			}
			return smp.getIncidentRules();
		}
	}
	
	@Override
	public IncidentRule getIncidentRule(String name) {
		synchronized (this) {
			SystemProfile smp = profiles.getSelfMonitoringProfile();
			if (smp == null) {
				LOGGER.log(Level.SEVERE, "smp is NULL!!!! ... " + profiles.size());
				for (Iterator<SystemProfile> it = profiles.iterator(); it.hasNext(); ) {
					LOGGER.log(Level.INFO, " .... " + it.next());
				}
				return null;
			}
			return smp.getIncidentRule(name);
		}
	}
	
	@XmlTransient
	@Override
	public Version getVersion() {
		synchronized (this) {
			return version;
		}
	}
	
	
	public void setVersion(Version version) {
		if (version == null) {
			return;
		}
		
		try {
			synchronized (this) {
				if (this.version.equals(version)) {
					return;
				}
				this.version = version;
				setConnectionStatus(ConnectionStatus.ONLINE);
			}			
		} finally {
			// in case a collector has changed its version or is out of synch
			// with the servers version it should get flagged for restart.
			// even if the servers version has not changed we are checking
			// regularly - as a side effect that the version of the server
			// is getting updated frequently anyways.
			checkCollectorVersions();
		}
	}
	
	public void checkCollectorVersions() {
		Version thisVersion = getVersion();
		if (thisVersion == null) {
			return;
		}
		for (CollectorRecord collector : collectors.values()) {
			Version collectorVersion = collector.getVersion();
			
			if (!Version.isValid(collectorVersion)) {
				LOGGER.log(Level.WARNING, "Collector Version " + collectorVersion + " is not valid");
			}
			
			if (!thisVersion.equals(collectorVersion) && !Version.UNDEFINED.equals(collectorVersion) && !Version.UNDEFINED.equals(thisVersion)) {
				if (collector.getRestartStatus() == RestartStatus.NONE) {
					LOGGER.log(Level.INFO, "Setting REQUIRED this.version = " + thisVersion + ", collectorVersion = " + collectorVersion);
					collector.setRestartStatus(RestartStatus.REQUIRED);
				}
			} else if (collector.getRestartStatus() == RestartStatus.REQUIRED) {
				collector.setRestartStatus(RestartStatus.NONE);
			}
		};
	}
	
	public CollectorRecord getCollector(String name, String host) {
		Objects.requireNonNull(name);
		Objects.requireNonNull(host);
		return getCollector(new CollectorInfo(name, host));
	}
	
	public CollectorRecord getCollector(CollectorInfo collectorInfo) {
		return collectors.get(collectorInfo);
	}
	
	@XmlElement(name = "config")
	public ServerConfig getConfig() {
		synchronized (this) {
			return config;
		}
	}
	
	public String getHost() {
		if (config == null) {
			return null;
		}
		return config.getHost();
	}
	
	public int getPort() {
		if (config == null) {
			return 0;
		}
		return config.getPort();
	}
	
	public void setConfig(ServerConfig config) {
		synchronized (this) {
			this.config = config;
			if (this.pwhChecker != null) {
				this.pwhChecker.close();
			}
			if (PWHChecker.NONE != this.pwhChecker) {
				this.pwhChecker = new PWHChecker(this, config.getPwhConfig(), pwhCheckIncident);
			}
		}
	}
	
	public boolean isRestarting() {
		synchronized (this) {
			return connectionStatus == ConnectionStatus.RESTARTING || connectionStatus == ConnectionStatus.RESTARTSCHEDULED;
		}
	}
	
	public boolean isOnline() {
		synchronized (this) {
			return connectionStatus == ConnectionStatus.ONLINE;
		}
	}
	
	@Override
	@XmlAttribute(name = "name")
	public String name() {
		synchronized (this) {
			if (this.name == null) {
				return config.toString();
			}
			return name;
		}
	}
	
	public void setName(String name) {
		synchronized (this) {
			this.name = name;
		}
	}

	@Override
	public String toString() {
		return name();
	}
	
	@Override
	public void setAgents(Iterable<AgentInfo> agentInfos) {
		synchronized (this) {
			Collection<AgentInfo> agents = new ArrayList<AgentInfo>();
			for (AgentInfo agentInfo : agentInfos) {
				if (!"dynaTrace Self-Monitoring".equals(
					agentInfo.getSystemProfileName()
				)) {
					agents.add(agentInfo);
				}
			}
			this.agents.addAll(agents);
		}
	}
	
	@XmlTransient
	public Iterable<AgentInfo> getAgents() {
		synchronized (this) {
			return agents;
		}
	}
	
	@XmlTransient
	public int getAgentCount() {
		synchronized (this) {
			return agents.size();
		}
	}

	@XmlTransient
	public Batch<Dashboard> getDashboards() {
		synchronized (this) {
			return dashboards;
		}
	}
	
	@XmlTransient
	public Batch<SystemProfile> getProfiles() {
		return profiles;
	}

	public SystemProfile getProfile(String profileName) {
		return profiles.get(profileName);
	}
	
	public void addProfile(SystemProfile profile) {
		profiles.add(profile);
	}

	@Override
	public boolean includes(Versionable versionable) {
		if (versionable == null) {
			return false;
		}
		Version version = versionable.getVersion();
		if (version == null) {
			return false;
		}
		return version.includes(versionable);
	}

	@Override
	public int compareTo(Versionable o) {
		Version version = null;
		if (this.version == null) {
			if (o == null) {
				return 0;
			}
			version = o.getVersion();
			if (version == null) {
				return 0;
			}
		}
		return this.version.compareTo(version);
	}

	@XmlTransient
	@Override
	public ConnectionStatus getConnectionStatus() {
		synchronized (this) {
			return connectionStatus;
		}
	}

	@Override
	public void setConnectionStatus(ConnectionStatus status) {
		synchronized (this) {
			switch (status) {
			case OFFLINE:
			case UNREACHABLE:
				if (this.connectionStatus == ConnectionStatus.RESTARTING) {
					return;
				}
				if (status != this.connectionStatus) {
					LOGGER.log(Level.INFO, "Connection Status of " + this + " changed to " + status);
				}
				this.connectionStatus = status;
				break;
			case ERRONEOUS:
				if (status != this.connectionStatus) {
					LOGGER.log(Level.INFO, "Connection Status of " + this + " changed to " + status);
				}
				this.connectionStatus = status;
				break;
			case ONLINE:
				if (this.connectionStatus == ConnectionStatus.RESTARTSCHEDULED) {
					break;
				}
			case RESTARTSCHEDULED:
			case RESTARTING:
				if (status != this.connectionStatus) {
					LOGGER.log(Level.INFO, "Connection Status of " + this + " changed to " + status);
				}
				this.connectionStatus = status;
				break;
			}
		}
	}

	@Override
	public void updateVersion(Version version) {
		setVersion(version);
	}

	@Override
	public void setLicenseInfo(LicenseInfo licenseInfo) {
		synchronized (this) {
			this.licenseInfo = licenseInfo;
		}
	}

	@XmlTransient
	public LicenseInfo getLicenseInfo() {
		synchronized (this) {
			return licenseInfo;
		}
	}

	@Override
	public FixPackStatus getFixpackStatus(FixPack fixPack) {
		synchronized (this) {
			FixPackStatus result = this.fixPackStates.get(fixPack);
			if (result == null) {
				return FixPackStatus.None;
			}
			return result;
		}
	}
	
	@Override
	public void updateFixPackState(FixPack fixPack, FixPackStatus status) {
		synchronized (this) {
			this.fixPackStates.put(fixPack, status);
		}
	}

	@Override
	public void refreshIncidentReferences() {
		throw new UnsupportedOperationException();
	}
	
	@XmlTransient
	public Batch<CollectorRecord> getCollectors() {
		return collectors;
	}
	
	public int getCollectorCount() {
		return collectors.size();
	}

	@Override
	public void setFixPackInstallStatus(FixPackInstallStatus status) {
		synchronized (this) {
			if (status != null) {
				if (this.fixPackInstallStatus == null) {
					LOGGER.info("[" + name() + "] Setting FPIS to " + status.getVersion());
				} else {
					Version curVersion = this.fixPackInstallStatus.getVersion();
					if (!curVersion.equals(status.getVersion())) {
						LOGGER.info("[" + name() + "] Setting FPIS to " + status.getVersion());
					}
				}
			} else if (this.fixPackInstallStatus != null) {
				LOGGER.info("[" + name() + "] Clearing FPIS");
			}
			this.fixPackInstallStatus = status;
		}
	}
	
	
	public FixPackInstallStatus getFixPackInstallStatus() {
		synchronized (this) {
			return fixPackInstallStatus;
		}
	}
	
	
	@Override
	public boolean equals(Versionable versionable) {
		return getVersion().equals(versionable);
	}
	
	@Override
	public void close() {
		if (this.pwhChecker != null) {
//			LOGGER.info("this.pwhChecker.close();");
			this.pwhChecker.close();
//			LOGGER.info("/this.pwhChecker.close();");
			try {
				if (pwhChecker.isAlive()) {
//					LOGGER.info("pwhChecker.join();");
					pwhChecker.join();
//					LOGGER.info("/pwhChecker.join();");
				}
			} catch (InterruptedException e) {
				// ignore
			}
		}
	}
	
	@Override
	protected ServerRecord clone() {
		try {
			ServerRecord clone = (ServerRecord) super.clone();
			clone.pwhChecker = PWHChecker.NONE;
			clone.setConfig(config.clone());
			clone.setName(name());
			clone.setVersion(version);
			return clone;
		} catch (CloneNotSupportedException e) {
			throw new InternalError(e.getMessage());
		}
	}

	public void setMoMConnectorVersion(Version version) {
		this.momConnectorVersion = version;
	}

	@XmlTransient
	public Version getMoMConnectorVersion() {
		return momConnectorVersion;
	}

}
