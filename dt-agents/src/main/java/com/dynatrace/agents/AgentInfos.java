package com.dynatrace.agents;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

/*

<agents>
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
</agents>

 */
@XmlRootElement(name = "agents")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class AgentInfos implements Iterable<AgentInfo> {

	private Collection<AgentInfo> agents =
			new ArrayList<AgentInfo>();
	
	@XmlElementRef(type = AgentInfo.class)
	public Collection<AgentInfo> getAgents() {
		return new ArrayList<AgentInfo>(agents);
	}
	
	public void setAgents(Collection<AgentInfo> agents) {
		this.agents.clear();
		if (agents != null) {
			this.agents.addAll(agents);
		}
	}

	@Override
	public Iterator<AgentInfo> iterator() {
		return new ArrayList<AgentInfo>(agents).iterator();
	}

}
