package com.dynatrace.reporting;

import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.dynatrace.xml.XMLUtil;

/*
	<incidentsoverviewdashlet name="Incidents" description="" displaysource="Base">
	  <incidentoverviews>
	    <incidentoverview name="Performance Warehouse is offline" start="2015-03-09T13:08:44.223-04:00" source="-" session="-"></incidentoverview>
	  </incidentoverviews>
	</incidentsoverviewdashlet>
*/
@XmlRootElement(name = "incidentsoverviewdashlet")
@XmlAccessorType(XmlAccessType.PROPERTY)
public final class IncidentsOverviewDashlet extends Dashlet {

	private String name = null;
	private String description = null;
	private String displaySource = null;
	private Collection<IncidentOverview> incidentOverviews = null;
	
	public final void setName(final String name) {
		this.name = name;
	}
	
	@XmlAttribute(name = "name")
	public final String getName() {
		return name;
	}
	
	public final void setDescription(final String description) {
		this.description = description;
	}
	
	@XmlAttribute(name = "description")
	public final String getDescription() {
		return description;
	}
	
	public final void setDisplaySource(final String displaySource) {
		this.displaySource = displaySource;
	}
	
	@XmlAttribute(name = "displaysource")
	public final String getDisplaySource() {
		return displaySource;
	}
	
	public final void setIncidentOverviews(
			final Collection<IncidentOverview> incidentOverviews) {
		this.incidentOverviews = incidentOverviews;
	}
	
	@XmlElementWrapper(name = "incidentoverviews")
	@XmlElementRef(name = "incidentoverview", type = IncidentOverview.class)
	public final Collection<IncidentOverview> getIncidentOverviews() {
		return incidentOverviews;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {
		return XMLUtil.toString(this);
	}
}
