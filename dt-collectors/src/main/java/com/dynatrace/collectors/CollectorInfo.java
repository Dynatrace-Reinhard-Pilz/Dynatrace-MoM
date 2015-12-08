package com.dynatrace.collectors;

import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.dynatrace.utils.Version;

@XmlRootElement(name = "collectorinformation")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class CollectorInfo {
	
	private static final String SELFMONITORING = "Dynatrace Self-Monitoring";
	
	private boolean isConnected = false;
	private boolean isEmbedded = false;
	private String host = null;
	private String name = null;
	private String version = Version.UNDEFINED.toString();
	
	public CollectorInfo() {
		
	}
	
	public CollectorInfo(String name, String host) {
		Objects.requireNonNull(name);
		Objects.requireNonNull(host);
		this.name = name;
		this.host = host;
	}
	
	@XmlTransient
	public boolean isSelfMonitoring() {
		return SELFMONITORING.equals(name);
	}
	
	public static boolean isSelfMonitoring(CollectorInfo collectorInfo) {
		return (collectorInfo != null) && collectorInfo.isSelfMonitoring();
	}
	
	public void setConnected(boolean isConnected) {
		this.isConnected = isConnected;
	}
	
	@XmlElement(name = "connected")
	public boolean isConnected() {
		return isConnected;
	}
	
	public void setEmbedded(boolean isEmbedded) {
		this.isEmbedded = isEmbedded;
	}
	
	@XmlElement(name = "embedded")
	public boolean isEmbedded() {
		return isEmbedded;
	}
	
	public void setHost(String host) {
		this.host = host;
	}
	
	@XmlElement(name = "host")
	public String getHost() {
		return host;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@XmlElement(name = "name")
	public String getName() {
		return name;
	}

	@XmlElement(name = "version")
	public String getVersionString() {
		return version;
	}
	
	public void setVersionString(String version) {
		this.version = version;
	}

	@XmlTransient
	public Version getVersion() {
		return Version.parse(version);
	}
	
	public void setVersion(Version version) {
		this.version = version.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((host == null) ? 0 : host.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		CollectorInfo other = (CollectorInfo) obj;
		if (host == null) {
			if (other.host != null)
				return false;
		} else if (!host.equals(other.host))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return name + "@" + host;
	}

}
