package com.dynatrace.mom.config;

import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.dynatrace.http.config.ServerConfig;

@XmlRootElement(name = "dynatrace-mom")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Configuration {
	
	private String version = null;
	private Collection<ServerConfig> serverConfigs = null;
	
	@XmlAttribute(name = "version")
	public String getVersion() {
		return version;
	}
	
	public void setVersion(String version) {
		this.version = version;
	}
	
	@XmlElementWrapper(name = "servers")
	@XmlElement(type = ServerConfig.class)
	public Collection<ServerConfig> getServerConfigs() {
		return serverConfigs;
	}
	
	public void setServerConfigs(Collection<ServerConfig> serverConfigs) {
		this.serverConfigs = serverConfigs;
	}
}
