package com.dynatrace.onboarding.usergroups;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import com.dynatrace.onboarding.config.Config;
import com.dynatrace.onboarding.dashboards.LocalDashboard;

@XmlRootElement(name = "onboarding")
@XmlAccessorType(XmlAccessType.FIELD)
public final class XmlOnboarding {
	
	@XmlElementRef(name = "group", type = XmlUserGroup.class)
	private Collection<XmlUserGroup> usergroups = null;
	@XmlElementRef(name = "dashboardassignment", type = XmlDashboardAssignment.class)
	private Collection<XmlDashboardAssignment> assignments = null;
	
	public Collection<XmlDashboardAssignment> getAssignments() {
		return assignments;
	}
	
	public void setAssignments(Collection<XmlDashboardAssignment> assignments) {
		this.assignments = assignments;
	}
	
	public Collection<XmlUserGroup> getUsergroups() {
		return usergroups;
	}
	
	public void setUsergroups(Collection<XmlUserGroup> usergroups) {
		this.usergroups = usergroups;
	}
	
	public static XmlOnboarding create(LocalDashboard[] dashboards, String profileName) {
		if (!Config.areUserGroupsConfigured()) {
			return null;
		}
		XmlOnboarding xmlOnboarding = new XmlOnboarding();
		Collection<XmlUserGroup> userGroups = new ArrayList<>();
		String[] userGroupKeys = Config.userGroupKeys();
		for (String userGroupKey : userGroupKeys) {
			String userGroup = Config.userGroup(userGroupKey);
			XmlUserGroup xmlUserGroup = new XmlUserGroup();
			xmlUserGroup.setLdap(true);
			xmlUserGroup.setDescription("");
			xmlUserGroup.setEditable(true);
			xmlUserGroup.setName(userGroup);
			xmlUserGroup.setManagementrole(Config.managementRole(userGroupKey));
			
			Collection<XmlDashboardPermission> dashboardPermissions = new ArrayList<>();
			if (dashboards != null) {
				for (LocalDashboard dashboard : dashboards) {
					XmlDashboardPermission dashboardPermission = new XmlDashboardPermission();
					Config.dashboardAutoOpen(userGroupKey, dashboard.getKey());
					dashboardPermission.setAutoopen(Config.dashboardAutoOpen(userGroupKey, dashboard.getKey()));
					dashboardPermission.setDashboard(dashboard.id());
					Config.dashboardPermission(userGroupKey, dashboard.getKey());
					dashboardPermission.setPermission(Config.dashboardPermission(userGroupKey, dashboard.getKey()));
					if (dashboardPermission.getPermission() != DashboardPermission.None) {
						dashboardPermissions.add(dashboardPermission);
					}
				}
			}
			xmlUserGroup.setDashboardPermissions(dashboardPermissions);
			
			Collection<XmlSystemRole> systemRoles = new ArrayList<>();
			XmlSystemRole systemRole = new XmlSystemRole();
			systemRole.setRole(Config.profileRole(userGroupKey));
			systemRole.setSystem(profileName);
			systemRoles.add(systemRole);
			xmlUserGroup.setSystemRoles(systemRoles);
			
			userGroups.add(xmlUserGroup);
			xmlOnboarding.setUsergroups(userGroups);
			
			Collection<XmlDashboardAssignment> assignments = new ArrayList<>();
//			XmlDashboardAssignment dashboardAssignment = new XmlDashboardAssignment();
//			dashboardAssignment.setDashboard(dashboardName);
//			dashboardAssignment.setProfile(profileName);
//			assignments.add(dashboardAssignment);
			xmlOnboarding.setAssignments(assignments);
		}
		return xmlOnboarding;
	}
	

}
