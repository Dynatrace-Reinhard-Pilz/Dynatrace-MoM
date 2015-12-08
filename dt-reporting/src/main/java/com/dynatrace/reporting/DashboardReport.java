package com.dynatrace.reporting;

import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.dynatrace.xml.XMLUtil;

/*
	<dashboardreport name="Incident Dashboard" version="6.1.0.8054" reportdate="2015-03-09T13:12:57.416-04:00" description="">
	  <source name="dynaTrace Self-Monitoring" filtersummary="last 30 minutes (auto)"></source>
	  <reportheader>
	    <reportdetails>
	      <user>admin</user>
	    </reportdetails>
	  </reportheader>
	  <data>
	    <incidentsoverviewdashlet name="Incidents" description="" displaysource="Base">
	      <incidentoverviews>
	        <incidentoverview name="Performance Warehouse is offline" start="2015-03-09T13:08:44.223-04:00" source="-" session="-"></incidentoverview>
	      </incidentoverviews>
	    </incidentsoverviewdashlet>
	  </data>
	</dashboardreport>
*/

@XmlRootElement(name = "dashboardreport")
@XmlAccessorType(XmlAccessType.PROPERTY)
public final class DashboardReport {

	private String name = null;
	private String version = null;
	private String reportDate = null;
	private String description = null;
	private Source source = null;
	private ReportHeader reportHeader = null;
	private Collection<Dashlet> dashlets = null;
	
	
	public final void setName(final String name) {
		this.name = name;
	}
	
	@XmlAttribute(name = "name")
	public final String getName() {
		return name;
	}
	
	public final void setVersion(final String version) {
		this.version = version;
	}
	
	@XmlAttribute(name = "version")
	public final String getVersion() {
		return version;
	}
	
	public final void setReportDate(final String reportDate) {
		this.reportDate = reportDate;
	}
	
	@XmlAttribute(name = "reportdate")
	public final String getReportDate() {
		return reportDate;
	}
	
	public final void setDescription(final String description) {
		this.description = description;
	}
	
	@XmlAttribute(name = "description")
	public final String getDescription() {
		return description;
	}
	
	public final void setSource(final Source source) {
		this.source = source;
	}
	
	@XmlElementRef(name = "source", type = Source.class)
	public final Source getSource() {
		return source;
	}
	
	public final void setReportHeader(final ReportHeader reportHeader) {
		this.reportHeader = reportHeader;
	}
	
	@XmlElementRef(name = "reportheader", type = ReportHeader.class)
	public final ReportHeader getReportHeader() {
		return reportHeader;
	}
	
	public final void setDashlets(final Collection<Dashlet> dashlets) {
		this.dashlets = dashlets;
	}
	
	@XmlElementWrapper(name = "data")
	@XmlElementRefs({
		@XmlElementRef(type = IncidentsOverviewDashlet.class),
		@XmlElementRef(type = ChartDashlet.class),
	})
	public final Collection<Dashlet> getDashlets() {
		return dashlets;
	}
	
	@Override
	public final String toString() {
		return XMLUtil.toString(this);
	}
}