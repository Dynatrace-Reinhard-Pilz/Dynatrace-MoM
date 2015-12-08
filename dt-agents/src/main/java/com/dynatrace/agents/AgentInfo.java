package com.dynatrace.agents;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import com.dynatrace.collectors.CollectorInfo;

/*

<agentinformation>
	<agentConfigured>true</agentConfigured>
	<agentGroup>-</agentGroup>
	<agentGroupId>-</agentGroupId>
	<agentId>-1656435213</agentId>
	<agentInstanceName>host@ip-172-30-3-164:1873</agentInstanceName>
	<agentMappingId>-</agentMappingId>
	<agentProperties>
		<agentHost>ip-172-30-3-164</agentHost>
		<agentHostAddress>127.0.0.1</agentHostAddress>
		<agentId>9d44cdf3</agentId>
		<agentPlatform>Linux</agentPlatform>
		<agentVersion>6.1.0.7880</agentVersion>
		<bufferCount>100000</bufferCount>
		<bufferSaturationThreshold>80</bufferSaturationThreshold>
		<bufferSize>96</bufferSize>
		<clockFrequency>1000000000</clockFrequency>
		<cloud>EC2</cloud>
		<hiResClock>true</hiResClock>
		<hotSensorPlaceable>false</hotSensorPlaceable>
		<hypervisor>Xen</hypervisor>
		<instrumentationState>Instrumentation enabled</instrumentationState>
		<logFileLocation>
			/home/labuser/dynatrace-6.1.0/log/dt_host_1873.0.log
		</logFileLocation>
		<maximumMemory>7843336192</maximumMemory>
		<operatingSystem>Linux</operatingSystem>
		<osArchitecture>x86_64</osArchitecture>
		<osVersion>3.13.0-48-generic</osVersion>
		<processors>2</processors>
		<recoveryEnabled>false</recoveryEnabled>
		<runtimeVersion>ANSI_X3.4-1968</runtimeVersion>
		<startDate>Thu Apr 09 14:17:17 UTC 2015</startDate>
		<startUp>1721461260415</startUp>
		<timer>High-resolution POSIX timer</timer>
	</agentProperties>
	<capture>false</capture>
	<captureCPUTimes>false</captureCPUTimes>
	<classLoadCount>0</classLoadCount>
	<collectorinformation>
		<connected>true</connected>
		<embedded>true</embedded>
		<host>ip-172-30-3-164</host>
		<name>Embedded dynaTrace Collector</name>
	</collectorinformation>
	<collectorName>Embedded dynaTrace Collector</collectorName>
	<configuration>-</configuration>
	<configurationId>-</configurationId>
	<connected>true</connected>
	<counterSkipEvents>-1</counterSkipEvents>
	<counterSkipExecPaths>-1</counterSkipExecPaths>
	<eventCount>0</eventCount>
	<fromCmdb>false</fromCmdb>
	<host>ip-172-30-3-164</host>
	<hotUpdateCritical>true</hotUpdateCritical>
	<hotUpdateable>false</hotUpdateable>
	<instanceName>host@ip-172-30-3-164:1873</instanceName>
	<isvLicenseSupported>false</isvLicenseSupported>
	<licenseInformation>skipped by license check</licenseInformation>
	<licenseOk>false</licenseOk>
	<name>host</name>
	<processId>1873</processId>
	<processorCount>2</processorCount>
	<required>false</required>
	<skippedEvents>-1</skippedEvents>
	<skippedPurePaths>-1</skippedPurePaths>
	<sourceGroupId>-</sourceGroupId>
	<startupTimeUTC>1428589037431</startupTimeUTC>
	<supportsHotSensorPlacement>false</supportsHotSensorPlacement>
	<syncThreshold>0.0</syncThreshold>
	<systemProfile>-</systemProfile>
	<systemProfileName>-</systemProfileName>
	<technologyType>Unknown</technologyType>
	<technologyTypeId>4</technologyTypeId>
	<timestamp>1428589037114</timestamp>
	<totalClassLoadCount>0</totalClassLoadCount>
	<totalCpuTime>0.0</totalCpuTime>
	<totalExecutionTime>0.0</totalExecutionTime>
	<totalPurePathCount>0</totalPurePathCount>
	<virtualTimeUTC>1428589037431</virtualTimeUTC>
	<vmVendor>unknown</vmVendor>
	<vmVersionString>unknown</vmVersionString>
</agentinformation>

 */
@XmlRootElement(name = "agentinformation")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class AgentInfo {

	private boolean isAgentConfigured = false;
	private String agentGroup = null;
	private String agentGroupId = null;
	private String agentId = null;
	private String agentInstanceName = null;
	private String agentMappingId = null;
	private AgentProperties agentProperties = null;
	private boolean isCapture = false;
	private boolean isCaptureCPUTimes = false;
	private int classLoadCount = 0;
	private CollectorInfo collectorInfo = null;
	private String collectorName = null;
	private String configuration = null;
	private String configurationId = null;
	private boolean isConnected = false;
	private int counterSkipEvents = -1;
	private int counterSkipExecPaths = -1;
	private int eventCount = 0;
	private boolean isFromCmdb = false;
	private String host = null;
	private boolean isHotUpdateCritical = false;
	private boolean isHotUpdateable = false;
	private String instanceName = null;
	private boolean isIsvLicenseSupported = false;
	private String licenseInformation = null;
	private boolean isLicenseOk = false;
	private String name = null;
	private String processId = null;
	private int processorCount = 0;
	private boolean isRequired = false;
	private int skippedEvents = 0;
	private int skippedPurePaths = 0;
	private String sourceGroupId = null;
	private long startupTimeUTC = 0L;
	private boolean isHSPSupported = false;
	private String syncThreshold = null;
	private String systemProfile = null;
	private String systemProfileName = null;
	private String technologyType = null;
	private int technologyTypeId = 0;
	private long timestamp = 0L;
	private int totalClassLoadCount = 0;
	private double totalCpuTime = 0.0;
	private double totalExecutionTime = 0.0;
	private int totalPurePathCount = 0;
	private long virtualTimeUTC = 0L;
	private String vmVendor = null;
	private String vmVersionString = null;

	public void setSyncThreshold(String syncThreshold) {
		this.syncThreshold = syncThreshold;
	}
	
	@XmlElement(name = "syncThreshold")
	public String getSyncThreshold() {
		return syncThreshold;
	}
	
	public void setSystemProfile(String systemProfile) {
		this.systemProfile = systemProfile;
	}
	
	@XmlElement(name = "systemProfile")
	public String getSystemProfile() {
		return systemProfile;
	}
	
	public void setSystemProfileName(String systemProfileName) {
		this.systemProfileName = systemProfileName;
	}
	
	@XmlElement(name = "systemProfileName")
	public String getSystemProfileName() {
		return systemProfileName;
	}
	
	public void setTechnologyType(String technologyType) {
		this.technologyType = technologyType;
	}
	
	@XmlElement(name = "technologyType")
	public String getTechnologyType() {
		return technologyType;
	}
	
	public void setTechnologyTypeId(int technologyTypeId) {
		this.technologyTypeId = technologyTypeId;
	}
	
	@XmlElement(name = "technologyTypeId")
	public int getTechnologyTypeId() {
		return technologyTypeId;
	}
	
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	@XmlElement(name = "timestamp")
	public long getTimestamp() {
		return timestamp;
	}
	
	public void setTotalClassLoadCount(int totalClassLoadCount) {
		this.totalClassLoadCount = totalClassLoadCount;
	}
	
	@XmlElement(name = "totalClassLoadCount")
	public int getTotalClassLoadCount() {
		return totalClassLoadCount;
	}
	
	public void setTotalCpuTime(double totalCpuTime) {
		this.totalCpuTime = totalCpuTime;
	}
	
	@XmlElement(name = "totalCpuTime")
	public double getTotalCpuTime() {
		return totalCpuTime;
	}
	
	public void setTotalExecutionTime(double totalExecutionTime) {
		this.totalExecutionTime = totalExecutionTime;
	}
	
	@XmlElement(name = "totalExecutionTime")
	public double getTotalExecutionTime() {
		return totalExecutionTime;
	}
	
	public void setTotalPurePathCount(int totalPurePathCount) {
		this.totalPurePathCount = totalPurePathCount;
	}
	
	@XmlElement(name = "totalPurePathCount")
	public int getTotalPurePathCount() {
		return totalPurePathCount;
	}
	
	public void setVirtualTimeUTC(long virtualTimeUTC) {
		this.virtualTimeUTC = virtualTimeUTC;
	}
	
	@XmlElement(name = "virtualTimeUTC")
	public long getVirtualTimeUTC() {
		return virtualTimeUTC;
	}
	
	public void setVmVendor(String vmVendor) {
		this.vmVendor = vmVendor;
	}
	
	@XmlElement(name = "vmVendor")
	public String getVmVendor() {
		return vmVendor;
	}
	
	public void setVmVersionString(String vmVersionString) {
		this.vmVersionString = vmVersionString;
	}
	
	@XmlElement(name = "vmVersionString")
	public String getVmVersionString() {
		return vmVersionString;
	}
	
	public void setHotUpdateable(boolean isHotUpdateable) {
		this.isHotUpdateable = isHotUpdateable;
	}
	
	@XmlElement(name = "hotUpdateable")
	public boolean isHotUpdateable() {
		return isHotUpdateable;
	}
	
	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}
	
	@XmlElement(name = "instanceName")
	public String getInstanceName() {
		return instanceName;
	}
	
	public void setIsvLicenseSupported(boolean isIsvLicenseSupported) {
		this.isIsvLicenseSupported = isIsvLicenseSupported;
	}
	
	@XmlElement(name = "isvLicenseSupported")
	public boolean isIsvLicenseSupported() {
		return isIsvLicenseSupported;
	}
	
	public void setLicenseInformation(String licenseInformation) {
		this.licenseInformation = licenseInformation;
	}
	
	@XmlElement(name = "licenseInformation")
	public String getLicenseInformation() {
		return licenseInformation;
	}
	
	public void setLicenseOk(boolean isLicenseOk) {
		this.isLicenseOk = isLicenseOk;
	}
	
	@XmlElement(name = "licenseOk")
	public boolean isLicenseOk() {
		return isLicenseOk;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@XmlElement(name = "name")
	public String getName() {
		return name;
	}
	
	public void setProcessId(String processId) {
		this.processId = processId;
	}
	
	@XmlElement(name = "processId")
	public String getProcessId() {
		return processId;
	}
	
	public void setProcessorCount(int processorCount) {
		this.processorCount = processorCount;
	}
	
	@XmlElement(name = "processorCount")
	public int getProcessorCount() {
		return processorCount;
	}
	
	public void setRequired(boolean isRequired) {
		this.isRequired = isRequired;
	}
	
	@XmlElement(name = "required")
	public boolean isRequired() {
		return isRequired;
	}
	
	public void setSkippedEvents(int skippedEvents) {
		this.skippedEvents = skippedEvents;
	}
	
	@XmlElement(name = "skippedEvents")
	public int getSkippedEvents() {
		return skippedEvents;
	}
	
	public void setSkippedPurePaths(int skippedPurePaths) {
		this.skippedPurePaths = skippedPurePaths;
	}
	
	@XmlElement(name = "skippedPurePaths")
	public int getSkippedPurePaths() {
		return skippedPurePaths;
	}
	
	public void setSourceGroupId(String sourceGroupId) {
		this.sourceGroupId = sourceGroupId;
	}
	
	@XmlElement(name = "sourceGroupId")
	public String getSourceGroupId() {
		return sourceGroupId;
	}
	
	public void setStartupTimeUTC(long startupTimeUTC) {
		this.startupTimeUTC = startupTimeUTC;
	}
	
	@XmlElement(name = "startupTimeUTC")
	public long getStartupTimeUTC() {
		return startupTimeUTC;
	}
	public void setHSPSupported(
			boolean isHSPSupported) {
		this.isHSPSupported = isHSPSupported;
	}
	
	@XmlElement(name = "supportsHotSensorPlacement")
	public boolean isHSPSupported() {
		return isHSPSupported;
	}
	
	public void setCollectorName(String collectorName) {
		this.collectorName = collectorName;
	}
	
	@XmlElement(name = "collectorName")
	public String getCollectorName() {
		return collectorName;
	}
	
	public void setConfiguration(String configuration) {
		this.configuration = configuration;
	}
	
	@XmlElement(name = "configuration")
	public String getConfiguration() {
		return configuration;
	}
	
	public void setConfigurationId(String configurationId) {
		this.configurationId = configurationId;
	}
	
	@XmlElement(name = "configurationId")
	public String getConfigurationId() {
		return configurationId;
	}
	
	public void setConnected(boolean isConnected) {
		this.isConnected = isConnected;
	}
	
	@XmlElement(name = "connected")
	public boolean isConnected() {
		return isConnected;
	}
	
	public void setCounterSkipEvents(int counterSkipEvents) {
		this.counterSkipEvents = counterSkipEvents;
	}
	
	@XmlElement(name = "counterSkipEvents")
	public int getCounterSkipEvents() {
		return counterSkipEvents;
	}
	
	public void setCounterSkipExecPaths(int counterSkipExecPaths) {
		this.counterSkipExecPaths = counterSkipExecPaths;
	}
	
	@XmlElement(name = "counterSkipExecPaths")
	public int getCounterSkipExecPaths() {
		return counterSkipExecPaths;
	}
	
	public void setEventCount(int eventCount) {
		this.eventCount = eventCount;
	}
	
	@XmlElement(name = "eventCount")
	public int getEventCount() {
		return eventCount;
	}
	
	public void setFromCmdb(boolean isFromCmdb) {
		this.isFromCmdb = isFromCmdb;
	}
	
	@XmlElement(name = "fromCmdb")
	public boolean isFromCmdb() {
		return isFromCmdb;
	}
	
	public void setHost(String host) {
		this.host = host;
	}
	
	@XmlElement(name = "host")
	public String getHost() {
		return host;
	}
	
	public void setHotUpdateCritical(boolean isHotUpdateCritical) {
		this.isHotUpdateCritical = isHotUpdateCritical;
	}
	
	@XmlElement(name = "hotUpdateCritical")
	public boolean isHotUpdateCritical() {
		return isHotUpdateCritical;
	}

	public void setCollectorInformation(
			CollectorInfo collectorInformation) {
		this.collectorInfo = collectorInformation;
	}
	
	@XmlElementRef(type = CollectorInfo.class, name = "collectorinformation")
	public CollectorInfo getCollectorInformation() {
		return collectorInfo;
	}
	
	public void setClassLoadCount(int classLoadCount) {
		this.classLoadCount = classLoadCount;
	}
	
	@XmlElement(name = "classLoadCount")
	public int getClassLoadCount() {
		return classLoadCount;
	}
	
	public void setCapture(boolean isCapture) {
		this.isCapture = isCapture;
	}
	
	@XmlElement(name = "capture")
	public boolean isCapture() {
		return isCapture;
	}
	
	public void setCaptureCPUTimes(boolean isCaptureCPUTimes) {
		this.isCaptureCPUTimes = isCaptureCPUTimes;
	}
	
	@XmlElement(name = "captureCPUTimes")
	public boolean isCaptureCPUTimes() {
		return isCaptureCPUTimes;
	}
	
	public void setAgentProperties(AgentProperties agentProperties) {
		this.agentProperties = agentProperties;
	}
	
	@XmlElementRef(type = AgentProperties.class, name = AgentProperties.TAG)
	public AgentProperties getAgentProperties() {
		return agentProperties;
	}
	
	public void setAgentConfigured(boolean isAgentConfigured) {
		this.isAgentConfigured = isAgentConfigured;
	}
	
	@XmlElement(name = "agentConfigured")
	public boolean isAgentConfigured() {
		return isAgentConfigured;
	}
	
	public void setAgentGroup(String agentGroup) {
		this.agentGroup = agentGroup;
	}
	
	@XmlElement(name = "agentGroup")
	public String getAgentGroup() {
		return agentGroup;
	}
	
	public void setAgentGroupId(String agentGroupId) {
		this.agentGroupId = agentGroupId;
	}
	
	@XmlElement(name = "agentGroupId")
	public String getAgentGroupId() {
		return agentGroupId;
	}
	
	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}
	
	@XmlElement(name = "agentId")
	public String getAgentId() {
		return agentId;
	}

	public void setAgentInstanceName(final String agentInstanceName) {
		this.agentInstanceName = agentInstanceName;
	}
	
	@XmlElement(name = "agentInstanceName")
	public String getAgentInstanceName() {
		return agentInstanceName;
	}
	
	public void setAgentMappingId(String agentMappingId) {
		this.agentMappingId = agentMappingId;
	}
	
	@XmlElement(name = "agentMappingId")
	public String getAgentMappingId() {
		return agentMappingId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((agentId == null) ? 0 : agentId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AgentInfo other = (AgentInfo) obj;
		if (agentId == null) {
			if (other.agentId != null)
				return false;
		} else if (!agentId.equals(other.agentId))
			return false;
		return true;
	}

}
