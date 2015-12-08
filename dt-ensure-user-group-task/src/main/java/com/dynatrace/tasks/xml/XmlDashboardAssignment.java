package com.dynatrace.tasks.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "dashboardassignment")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlDashboardAssignment {
	
	@XmlAttribute(name = "dashboard")
	private String dashboard = null;
	@XmlAttribute(name = "profile")
	private String profile = null;
	
	public String getDashboard() {
		return dashboard;
	}
	
	public void setDashboard(String dashboard) {
		this.dashboard = dashboard;
	}
	
	public String getProfile() {
		return profile;
	}
	
	public void setProfile(String profile) {
		this.profile = profile;
	}

}
