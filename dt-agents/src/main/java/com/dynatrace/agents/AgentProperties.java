package com.dynatrace.agents;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/*
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
		<logFileLocation>/home/labuser/dynatrace-6.1.0/log/dt_host_1873.0.log</logFileLocation>
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
 */
@XmlRootElement(name = AgentProperties.TAG)
@XmlAccessorType(XmlAccessType.PROPERTY)
public class AgentProperties {
	
	public static final String TAG = "agentProperties";
	
	private String agentHost = null;
	private String agentHostAddress = null;
	private String agentId = null;
	private String agentPlatform = null;
	private String agentVersion = null;
	private int bufferCount = 0;
	private int bufferSaturationThreshold = 0;
	private int bufferSize = 0;
	private long clockFrequency = 0;
	private String cloud = null;
	private boolean isHiResClock = false;
	private boolean isHotSensorPlaceable = false;
	private String hypervisor = null;
	private String instrumentationState = null;
	private String logFileLocation = null;
	private long maximumMemory = 0;
	private String operatingSystem = null;
	private String osArchitecture = null;
	private String osVersion = null;
	private int processors = 0;
	private boolean isRecoveryEnabled = false;
	private String runtimeVersion = null;
	private String startDate = null;
	private String startUp = null;
	private String timer = null;
	
	public void setAgentHost(String agentHost) {
		this.agentHost = agentHost;
	}
	
	@XmlElement(name = "agentHost")
	public String getAgentHost() {
		return agentHost;
	}
	
	public void setAgentHostAddress(String agentHostAddress) {
		this.agentHostAddress = agentHostAddress;
	}
	
	@XmlElement(name = "agentHostAddress")
	public String getAgentHostAddress() {
		return agentHostAddress;
	}
	
	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}
	
	@XmlElement(name = "agentId")
	public String getAgentId() {
		return agentId;
	}
	
	public void setAgentPlatform(String agentPlatform) {
		this.agentPlatform = agentPlatform;
	}
	
	@XmlElement(name = "agentPlatform")
	public String getAgentPlatform() {
		return agentPlatform;
	}
	
	public void setAgentVersion(String agentVersion) {
		this.agentVersion = agentVersion;
	}
	
	@XmlElement(name = "agentVersion")
	public String getAgentVersion() {
		return agentVersion;
	}
	
	public void setBufferCount(int bufferCount) {
		this.bufferCount = bufferCount;
	}
	
	@XmlElement(name = "bufferCount")
	public int getBufferCount() {
		return bufferCount;
	}

	public void setBufferSaturationThreshold(int bufferSaturationThreshold) {
		this.bufferSaturationThreshold = bufferSaturationThreshold;
	}
	
	@XmlElement(name = "bufferSaturationThreshold")
	public int getBufferSaturationThreshold() {
		return bufferSaturationThreshold;
	}
	
	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}
	
	@XmlElement(name = "bufferSize")
	public int getBufferSize() {
		return bufferSize;
	}
	
	public void setClockFrequency(long clockFrequency) {
		this.clockFrequency = clockFrequency;
	}

	@XmlElement(name = "clockFrequency")
	public long getClockFrequency() {
		return clockFrequency;
	}
	
	public void setCloud(String cloud) {
		this.cloud = cloud;
	}
	
	@XmlElement(name = "cloud")
	public String getCloud() {
		return cloud;
	}
	
	public void setHiResClock(boolean isHiResClock) {
		this.isHiResClock = isHiResClock;
	}
	
	@XmlElement(name = "hiResClock")
	public boolean isHiResClock() {
		return isHiResClock;
	}
	
	public void setHotSensorPlaceable(boolean isHotSensorPlaceable) {
		this.isHotSensorPlaceable = isHotSensorPlaceable;
	}
	
	@XmlElement(name = "hotSensorPlaceable")
	public boolean isHotSensorPlaceable() {
		return isHotSensorPlaceable;
	}
	
	public void setHypervisor(String hypervisor) {
		this.hypervisor = hypervisor;
	}
	
	@XmlElement(name = "hypervisor")
	public String getHypervisor() {
		return hypervisor;
	}
	
	public void setInstrumentationState(String instrumentationState) {
		this.instrumentationState = instrumentationState;
	}
	
	@XmlElement(name = "instrumentationState")
	public String getInstrumentationState() {
		return instrumentationState;
	}
	
	public void setLogFileLocation(String logFileLocation) {
		this.logFileLocation = logFileLocation;
	}
	
	@XmlElement(name = "logFileLocation")
	public String getLogFileLocation() {
		return logFileLocation;
	}
	
	public void setMaximumMemory(long maximumMemory) {
		this.maximumMemory = maximumMemory;
	}
	
	@XmlElement(name = "maximumMemory")
	public long getMaximumMemory() {
		return maximumMemory;
	}

	public void setOperatingSystem(String operatingSystem) {
		this.operatingSystem = operatingSystem;
	}
	
	@XmlElement(name = "operatingSystem")
	public String getOperatingSystem() {
		return operatingSystem;
	}
	
	public void setOsArchitecture(String osArchitecture) {
		this.osArchitecture = osArchitecture;
	}
	
	@XmlElement(name = "osArchitecture")
	public String getOsArchitecture() {
		return osArchitecture;
	}
	
	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}
	
	@XmlElement(name = "osVersion")
	public String getOsVersion() {
		return osVersion;
	}

	public void setProcessors(int processors) {
		this.processors = processors;
	}
	
	@XmlElement(name = "processors")
	public int getProcessors() {
		return processors;
	}
	
	public void setRecoveryEnabled(boolean isRecoveryEnabled) {
		this.isRecoveryEnabled = isRecoveryEnabled;
	}
	
	@XmlElement(name = "recoveryEnabled")
	public boolean isRecoveryEnabled() {
		return isRecoveryEnabled;
	}

	public void setRuntimeVersion(String runtimeVersion) {
		this.runtimeVersion = runtimeVersion;
	}
	
	@XmlElement(name = "runtimeVersion")
	public String getRuntimeVersion() {
		return runtimeVersion;
	}
	
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	
	@XmlElement(name = "startDate")
	public String getStartDate() {
		return startDate;
	}
	
	public void setStartUp(String startUp) {
		this.startUp = startUp;
	}
	
	@XmlElement(name = "startUp")
	public String getStartUp() {
		return startUp;
	}

	public void setTimer(String timer) {
		this.timer = timer;
	}
	
	@XmlElement(name = "timer")
	public String getTimer() {
		return timer;
	}

}
