package com.dynatrace.onboarding.usergroups;

import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

/*
      <group ldap="true" iseditable="true" description="" name="NewUserGroup51" managementrole="Guest">
        <systemrole system="LoadGenMain" role="Administrator" />
        <dashboardpermission autoopen="true" permission="Read_Write" dashboard="*" />
      </group>

 */
@XmlRootElement(name = "group")
@XmlAccessorType(XmlAccessType.FIELD)
public final class XmlUserGroup {
	
	@XmlAttribute(name = "ldap")
	private boolean isLdap = false;
	@XmlAttribute(name = "iseditable")
	private boolean isEditable = true;
	@XmlAttribute(name = "description")
	private String description = "";
	@XmlAttribute(name = "name")
	private String name = null;
	@XmlAttribute(name = "managementrole")
	private String managementrole = null;
	@XmlElementRef(name = "systemrole", type = XmlSystemRole.class)
	private Collection<XmlSystemRole> systemRoles = null;
	@XmlElementRef(name = "dashboardpermission", type = XmlDashboardPermission.class)
	private Collection<XmlDashboardPermission> dashboardPermissions = null;
	
	
	public Collection<XmlDashboardPermission> getDashboardPermissions() {
		return dashboardPermissions;
	}
	
	public void setDashboardPermissions(Collection<XmlDashboardPermission> dashboardPermissions) {
		this.dashboardPermissions = dashboardPermissions;
	}
	
	public Collection<XmlSystemRole> getSystemRoles() {
		return systemRoles;
	}
	
	public void setSystemRoles(Collection<XmlSystemRole> systemRoles) {
		this.systemRoles = systemRoles;
	}
	
	public boolean isEditable() {
		return isEditable;
	}
	
	public void setEditable(boolean isEditable) {
		this.isEditable = isEditable;
	}
	
	public boolean isLdap() {
		return isLdap;
	}
	
	public void setLdap(boolean isLdap) {
		this.isLdap = isLdap;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getManagementrole() {
		return managementrole;
	}
	
	public void setManagementrole(String managementrole) {
		this.managementrole = managementrole;
	}
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

}
