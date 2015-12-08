package com.dynatrace.onboarding.usergroups;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/*
        <systemrole system="LoadGenMain" role="Administrator" />

 */
@XmlRootElement(name = "systemrole")
@XmlAccessorType(XmlAccessType.FIELD)
public final class XmlSystemRole {
	
	@XmlAttribute(name = "system")
	private String system = null;
	@XmlAttribute(name = "role")
	private String role = null;
	
	public String getRole() {
		return role;
	}
	
	public void setRole(String role) {
		this.role = role;
	}
	
	public String getSystem() {
		return system;
	}
	
	public void setSystem(String system) {
		this.system = system;
	}

}
