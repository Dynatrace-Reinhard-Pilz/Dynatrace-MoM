package com.dynatrace.reporting;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.datatype.DatatypeFactory;

import com.dynatrace.xml.XMLUtil;

/*
	<incidentsoverviewdashlet name="Incidents" description="" displaysource="Base">
	  <incidentoverviews>
	    <incidentoverview name="Performance Warehouse is offline" start="2015-03-09T13:08:44.223-04:00" source="-" session="-"></incidentoverview>
	  </incidentoverviews>
	</incidentsoverviewdashlet>
*/

@XmlRootElement(name = "incidentoverview")
@XmlAccessorType(XmlAccessType.PROPERTY)
public final class IncidentOverview {

	private String name = null;
	private String start = null;
	private String duration = null;
	private String end = null;
	private String source = null;
	private String session = null;
	
	public final void setName(final String name) {
		this.name = name;
	}
	
	@XmlAttribute(name = "name")
	public final String getName() {
		return name;
	}
	
	public final void setStart(final String start) {
		this.start = start;
	}
	
	@XmlAttribute(name = "start")
	public final String getStart() {
		try {
			Date date = DatatypeFactory.newInstance().newXMLGregorianCalendar(start).toGregorianCalendar().getTime();
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
		} catch (Throwable t) {
			
		}
		return start;
	}
	
	public final void setDuration(final String duration) {
		this.duration = duration;
	}
	
	@XmlAttribute(name = "duration")
	public final String getDuration() {
		return duration;
	}
	
	public final void setEnd(final String end) {
		this.end = end;
	}
	
	@XmlAttribute(name = "end")
	public final String getEnd() {
		return end;
	}
	
	public final void setSource(final String source) {
		this.source = source;
	}
	
	@XmlAttribute(name = "source")
	public final String getSource() {
		return source;
	}
	
	public final void setSession(final String session) {
		this.session = session;
	}
	
	@XmlAttribute(name = "session")
	public final String getSession() {
		return session;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {
		return XMLUtil.toString(this);
	}
}
