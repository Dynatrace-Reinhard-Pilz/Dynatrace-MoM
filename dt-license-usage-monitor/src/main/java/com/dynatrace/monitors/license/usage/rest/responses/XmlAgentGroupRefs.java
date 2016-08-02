package com.dynatrace.monitors.license.usage.rest.responses;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

/*

http://localhost:8020/rest/management/profiles/easyTravel/agentgroups

<agentgroups>
	<agentgroupreference name="Business Backend Server (Java)" href="http://localhost:8020/rest/management/profiles/easyTravel/agentgroups/Business%20Backend%20Server%20(Java)"/>
	<agentgroupreference name="Customer Web Frontend (Java)" href="http://localhost:8020/rest/management/profiles/easyTravel/agentgroups/Customer%20Web%20Frontend%20(Java)"/>
	<agentgroupreference name="Browser" href="http://localhost:8020/rest/management/profiles/easyTravel/agentgroups/Browser"/>
	<agentgroupreference name="CreditCardAuthorization (C++)" href="http://localhost:8020/rest/management/profiles/easyTravel/agentgroups/CreditCardAuthorization%20(C++)"/>
	<agentgroupreference name="Test" href="http://localhost:8020/rest/management/profiles/easyTravel/agentgroups/Test"/>
	<agentgroupreference name="Payment Backend (.NET)" href="http://localhost:8020/rest/management/profiles/easyTravel/agentgroups/Payment%20Backend%20(.NET)"/>
	<agentgroupreference name="B2B Web Frontend (.NET)" href="http://localhost:8020/rest/management/profiles/easyTravel/agentgroups/B2B%20Web%20Frontend%20(.NET)"/>
	<agentgroupreference name="Web Server" href="http://localhost:8020/rest/management/profiles/easyTravel/agentgroups/Web%20Server"/>
	<agentgroupreference name="WebSphere Message Broker" href="http://localhost:8020/rest/management/profiles/easyTravel/agentgroups/WebSphere%20Message%20Broker"/>
	<agentgroupreference name="Host Metrics" href="http://localhost:8020/rest/management/profiles/easyTravel/agentgroups/Host%20Metrics"/>
	<agentgroupreference name="Cassandra Nodes (Java)" href="http://localhost:8020/rest/management/profiles/easyTravel/agentgroups/Cassandra%20Nodes%20(Java)"/>
	<agentgroupreference name="RichClientADKTest.vshost" href="http://localhost:8020/rest/management/profiles/easyTravel/agentgroups/RichClientADKTest.vshost"/>
	<agentgroupreference name="gggg" href="http://localhost:8020/rest/management/profiles/easyTravel/agentgroups/gggg"/>
</agentgroups

 */
@XmlRootElement(name = "agentgroups")
@XmlAccessorType(XmlAccessType.PROPERTY)
public final class XmlAgentGroupRefs {
	
	private Collection<XmlAgentGroupRef> agentGroupRefs = new ArrayList<XmlAgentGroupRef>();
	
	@XmlElementRef(type = XmlAgentGroupRef.class)
	public Collection<XmlAgentGroupRef> getAgentGroupRefs() {
		return agentGroupRefs;
	}
	
	public void setAgentGroupRefs(Collection<XmlAgentGroupRef> agentGroupRefs) {
		this.agentGroupRefs = agentGroupRefs;
	}
	
}
