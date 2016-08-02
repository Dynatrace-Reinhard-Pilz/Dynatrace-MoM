package com.dynatrace.dashboards;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

/*
<dashboards href="http://[server]:[port]/rest/management/dashboards">
  <dashboard author="[author]" description="" hrefrel="/rest/management/reports/create/[dashboardname]" jnlp="http://[server]:[port]/webstart/Client/client.jnlp?argument=-reuse&argument=-dashboard&argument=online://ip-172-30-3-164/Incident+Dashboard" modified="2011-07-26T19:15:22Z" modifiedby="admin" session="dynaTrace Self-Monitoring" id="Incident Dashboard" href="http://52.5.176.15:8020/rest/management/reports/create/Incident%20Dashboard"/>
  <description>description</description>
</dashboards>
*/
@XmlRootElement(name = "dashboards")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Dashboards implements Iterable<Dashboard> {

	private Collection<Dashboard> dashboards = new ArrayList<Dashboard>();
	private String href = null;
	
	public void setHref(String href) {
		this.href = href;
	}
	
	@XmlAttribute(name = "href")
	public String getHref() {
		return href;
	}
	
	
	@XmlElementRef(type = Dashboard.class, name = "dashboard")
	public Collection<Dashboard> getDashboards() {
		return dashboards;
	}
	
	public void setDashboards(Collection<Dashboard> dashboards) {
		this.dashboards = dashboards;
	}

	@Override
	public Iterator<Dashboard> iterator() {
		return new ArrayList<Dashboard>(dashboards).iterator();
	}

}
