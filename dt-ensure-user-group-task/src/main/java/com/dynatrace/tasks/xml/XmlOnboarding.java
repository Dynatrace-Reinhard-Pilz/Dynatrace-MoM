package com.dynatrace.tasks.xml;

import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

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

}
