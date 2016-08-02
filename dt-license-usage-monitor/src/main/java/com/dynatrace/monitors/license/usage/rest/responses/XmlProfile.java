package com.dynatrace.monitors.license.usage.rest.responses;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/*

<systemprofile enabled="true" isInteractiveLicensed="false" isrecording="false" id="easyTravel" href="http://localhost:8020/rest/management/profiles/easyTravel">
	<actions>
		<action id="configurations" href="http://localhost:8020/rest/management/profiles/easyTravel/configurations"/>
		<action id="enable" href="http://localhost:8020/rest/management/profiles/easyTravel/enable"/>
		<action id="disable" href="http://localhost:8020/rest/management/profiles/easyTravel/disable"/>
		<action id="storepurepaths" href="http://localhost:8020/rest/management/profiles/easyTravel/storepurepaths"/>
		<action id="clear" href="http://localhost:8020/rest/management/profiles/easyTravel/clear"/>
		<action id="setmetadata" href="http://localhost:8020/rest/management/profiles/easyTravel/setmetadata"/>
		<action id="registertest" href="http://localhost:8020/rest/management/profiles/easyTravel/registertestrun"/>
		<action id="testruns" href="http://localhost:8020/rest/management/profiles/easyTravel/testruns"/>
		<action id="testruns" href="http://localhost:8020/rest/management/profiles/easyTravel/testruns"/>
	</actions>
	<agentgroupsreference href="http://localhost:8020/rest/management/profiles/easyTravel/agentgroups"/>
	<description>
		Profile for the easyTravel demo application. {meta environment="QA"} {meta appid="-appid1"} {meta app="APP1"} {meta businessunit="BUNIT1"} {meta project="PROJECT1"}
	</description>
</systemprofile>

 */
@XmlRootElement(name = "systemprofile")
@XmlAccessorType(XmlAccessType.PROPERTY)
public final class XmlProfile {

	private boolean isEnabled = false;
	private boolean isInteractiveLicensed = false;
	private boolean isRecording = false;
	private String id = null;
	private String href = null;
	private Collection<XmlAction> actions = new ArrayList<XmlAction>();
	private XmlAgentGroupsRef agenttGroupsRef = null;
	private String description = null;
	
	@XmlAttribute(name = "id")
	public String getId() {
		return id;
	}
	
	@XmlAttribute(name = "href")
	public String getHref() {
		return href;
	}
	
	@XmlAttribute(name = "isrecording")
	public boolean isRecording() {
		return isRecording;
	}
	
	@XmlAttribute(name = "isInteractiveLicensed")
	public boolean isInteractiveLicensed() {
		return isInteractiveLicensed;
	}
	
	@XmlAttribute(name = "enabled")
	public boolean isEnabled() {
		return isEnabled;
	}
	
	@XmlElementRef(type = XmlAction.class)
    @XmlElementWrapper(name = "actions") 	
	public Collection<XmlAction> getActions() {
		return actions;
	}
	
	@XmlElementRef(type = XmlAgentGroupsRef.class)
	public XmlAgentGroupsRef getAgenttGroupsRef() {
		return agenttGroupsRef;
	}
	
	@XmlElement(name = "description")
	public String getDescription() {
		return description;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public void setHref(String href) {
		this.href = href;
	}
	
	public void setRecording(boolean isRecording) {
		this.isRecording = isRecording;
	}
	
	public void setInteractiveLicensed(boolean isInteractiveLicensed) {
		this.isInteractiveLicensed = isInteractiveLicensed;
	}
	
	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}
	
	public void setActions(Collection<XmlAction> actions) {
		this.actions = actions;
	}
	
	public void setAgenttGroupsRef(XmlAgentGroupsRef agenttGroupsRef) {
		this.agenttGroupsRef = agenttGroupsRef;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		XmlProfile other = (XmlProfile) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return id;
	}
	
}
