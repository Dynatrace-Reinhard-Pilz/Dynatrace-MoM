package com.dynatrace.onboarding.usergroups;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/*

  <dashboardpermission autoopen="true" permission="Read_Write" dashboard="*" />

 */
@XmlRootElement(name = "dashboardpermission")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlDashboardPermission {

	@XmlAttribute(name = "autoopen")
	private boolean isAutoopen = false;
	@XmlAttribute(name = "permission")
	private DashboardPermission permission = DashboardPermission.Read;
	@XmlAttribute(name = "dashboard")
	private String dashboard = null;
	
	public boolean isAutoopen() {
		return isAutoopen;
	}
	
	public void setAutoopen(boolean isAutoopen) {
		this.isAutoopen = isAutoopen;
	}
	
	public String getDashboard() {
		return dashboard;
	}
	
	public void setDashboard(String dashboard) {
		this.dashboard = dashboard;
	}
	
	public DashboardPermission getPermission() {
		return permission;
	}
	
	public void setPermission(DashboardPermission permission) {
		this.permission = permission;
	}
}
