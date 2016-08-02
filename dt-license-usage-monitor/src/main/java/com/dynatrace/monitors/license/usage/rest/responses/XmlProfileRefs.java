package com.dynatrace.monitors.license.usage.rest.responses;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

/*

<profiles href="http://localhost:8020/rest/management/profiles">
	<systemprofile isrecording="false" id="dynaTrace Self-Monitoring" href="http://localhost:8020/rest/management/profiles/dynaTrace%20Self-Monitoring"/>
	<systemprofile isrecording="false" id="ADKSample" href="http://localhost:8020/rest/management/profiles/ADKSample"/>
	<systemprofile isrecording="false" id="APP1 JBoss" href="http://localhost:8020/rest/management/profiles/APP1%20JBoss"/>
	<systemprofile isrecording="false" id="BaseEI" href="http://localhost:8020/rest/management/profiles/BaseEI"/>
	<systemprofile isrecording="false" id="Cordys" href="http://localhost:8020/rest/management/profiles/Cordys"/>
	<systemprofile isrecording="false" id="easyTravel" href="http://localhost:8020/rest/management/profiles/easyTravel"/>
	<systemprofile isrecording="false" id="HikariCP" href="http://localhost:8020/rest/management/profiles/HikariCP"/>
	<systemprofile isrecording="false" id="Integration" href="http://localhost:8020/rest/management/profiles/Integration"/>
	<systemprofile isrecording="false" id="JavaWorld" href="http://localhost:8020/rest/management/profiles/JavaWorld"/>
	<systemprofile isrecording="false" id="JerseyJetty" href="http://localhost:8020/rest/management/profiles/JerseyJetty"/>
	<systemprofile isrecording="false" id="Jetty" href="http://localhost:8020/rest/management/profiles/Jetty"/>
	<systemprofile isrecording="false" id="License Monitoring" href="http://localhost:8020/rest/management/profiles/License%20Monitoring"/>
	<systemprofile isrecording="false" id="LoadGenMain" href="http://localhost:8020/rest/management/profiles/LoadGenMain"/>
	<systemprofile isrecording="false" id="MoM" href="http://localhost:8020/rest/management/profiles/MoM"/>
	<systemprofile isrecording="false" id="Monitoring" href="http://localhost:8020/rest/management/profiles/Monitoring"/>
	<systemprofile isrecording="false" id="mRemoteNG" href="http://localhost:8020/rest/management/profiles/mRemoteNG"/>
	<systemprofile isrecording="false" id="PocWcfApplication" href="http://localhost:8020/rest/management/profiles/PocWcfApplication"/>
	<systemprofile isrecording="false" id="Telus" href="http://localhost:8020/rest/management/profiles/Telus"/>
	<systemprofile isrecording="false" id="Tomcat" href="http://localhost:8020/rest/management/profiles/Tomcat"/>
	<systemprofile isrecording="false" id="vertx-samples" href="http://localhost:8020/rest/management/profiles/vertx-samples"/>
	<systemprofile isrecording="false" id="WcfSvcHost" href="http://localhost:8020/rest/management/profiles/WcfSvcHost"/>
	<systemprofile isrecording="false" id="{@application} JBoss" href="http://localhost:8020/rest/management/profiles/%7B@application%7D%20JBoss"/>
</profiles>

 */
@XmlRootElement(name = "profiles")
@XmlAccessorType(XmlAccessType.PROPERTY)
public final class XmlProfileRefs {
	
	private String href = null;
	
	private Collection<XmlProfileRef> profileRefs = new ArrayList<XmlProfileRef>();
	
	@XmlElementRef(type = XmlProfileRef.class)
	public Collection<XmlProfileRef> getProfileRefs() {
		return profileRefs;
	}
	
	@XmlAttribute(name = "href")
	public String getHref() {
		return href;
	}
	
	public void setProfileRefs(Collection<XmlProfileRef> profiles) {
		this.profileRefs = profiles;
	}

	public void setHref(String href) {
		this.href = href;
	}
	
}
