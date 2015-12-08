package com.dynatrace.incidents;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.dynatrace.utils.Strings;

/*

incident id="4f0198bc-60ac-489d-ad40-69e55f51800f">
	<message>Performance Warehouse is offline</message>
	<start>2015-08-27T13:59:48.810-04:00</start>
	<end>2015-08-27T14:19:47.598-04:00</end>
	<severity>severe</severity>
	<state>Created</state>
</incident>

 */
@XmlRootElement(name = "incident")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Incident {
	
	private static final Logger LOGGER =
			Logger.getLogger(Incident.class.getName());
	
	private static final DatatypeFactory FACTORY = createDataTypeFactory();
	private static final SimpleDateFormat sdf =
			new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	
	private static DatatypeFactory createDataTypeFactory() {
		try {
			return DatatypeFactory.newInstance();
		} catch (DatatypeConfigurationException e) {
			LOGGER.log(Level.WARNING, "Unable to create DatatypeFactory", e);
			return null;
		}
	}
	
	private String id = null;
	private String message = null;
	private String start = null;
	private String end = null;
	private IncidentSeverity severity = IncidentSeverity.informational;
	private IncidentState state = null;
	private long startTime = 0L;
	private long endTime = 0L;
	
	public static boolean isOpen(Incident incident) {
		return (incident != null) && incident.isOpen(); 
	}
	
	public static boolean isConfirmed(Incident incident) {
		return (incident != null) && incident.isConfirmed(); 
	}
	
	@XmlTransient
	public boolean isOpen() {
		return isValid() && !isConfirmed() && (endTime <= 0);
	}
	
	@XmlTransient
	public boolean isValid() {
		return startTime > 0;
	}
	
	@XmlAttribute(name = "id")
	public String getId() {
		return id;
	}
	
	@XmlElement(name = "message")
	public String getMessage() {
		return message;
	}
	
	@XmlTransient
	public long getStartTime() {
		return startTime;
	}
	
	@XmlTransient
	public long getEndTime() {
		return endTime;
	}
	
	@XmlElement(name = "start")
	public String getStart() {
		return start;
	}
	
	@XmlElement(name = "end")
	public String getEnd() {
		return end;
	}
	
	@XmlTransient
	public String getFormattedTime() {
		if (startTime <= 0) {
			return Strings.EMPTY;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(sdf.format(new Date(startTime))).append(" - ");
		if (endTime > 0) {
			sb.append(sdf.format(new Date(endTime)));
		}
		return sb.toString();
	}
	
	@XmlElement(name = "severity")
	public IncidentSeverity getSeverity() {
		return severity;
	}
	
	public static IncidentSeverity getSeverity(Incident incident) {
		if (incident == null) {
			return IncidentSeverity.undefined;
		}
		return incident.getSeverity();
	}
	
	@XmlElement(name = "state")
	public IncidentState getState() {
		synchronized (this) {
			return state;
		}
	}
	
	public boolean isConfirmed() {
		return getState() == IncidentState.Confirmed;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public void setStart(String start) {
		this.start = start;
		this.startTime = getTime(start);
	}
	
	private static long getTime(String xmlTime) {
		if (xmlTime == null) {
			return 0L;
		}
		if (FACTORY == null) {
			return 0L;
		}
		XMLGregorianCalendar cal = FACTORY.newXMLGregorianCalendar(xmlTime);
		return cal.toGregorianCalendar().getTime().getTime();
	}
	
	public void setEnd(String end) {
		this.end = end;
		this.endTime = getTime(end);
	}
	
	public void setSeverity(IncidentSeverity severity) {
		this.severity = severity;
	}
	
	public void setState(IncidentState state) {
		synchronized (this) {
			this.state = state;
		}
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
		Incident other = (Incident) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
