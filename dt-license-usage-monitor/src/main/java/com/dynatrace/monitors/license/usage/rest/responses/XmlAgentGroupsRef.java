package com.dynatrace.monitors.license.usage.rest.responses;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/*

<agentgroupsreference href="http://localhost:8020/rest/management/profiles/easyTravel/agentgroups"/>

 */
@XmlRootElement(name = "agentgroupsreference")
@XmlAccessorType(XmlAccessType.PROPERTY)
public final class XmlAgentGroupsRef {

	private String href = null;
	
	@XmlAttribute(name = "href")
	public String getHref() {
		return href;
	}
	
	public void setHref(String href) {
		this.href = href;
	}
}
