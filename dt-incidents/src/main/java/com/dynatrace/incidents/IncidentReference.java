package com.dynatrace.incidents;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.dynatrace.utils.Iterables;
import com.dynatrace.utils.Unique;

/*

<incidentreference
	id="c64e8a40-05e4-45f0-9286-c6766b2a2d86"
	href="https://localhost:8021/rest/management/profiles/dynaTrace%20Self-Monitoring/incidentrules/Performance%20Warehouse%20Offline/incidents/c64e8a40-05e4-45f0-9286-c6766b2a2d86"
/>

 */
@XmlRootElement(name = "incidentreference")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class IncidentReference implements Unique<String> {
	
	private String id = null;
	private String href = null;
	private Incident incident = null;
	
	public static int getOpenIncidentCount(
		Collection<IncidentReference> references
	) {
		int openIncidentCount = 0;
		if (!Iterables.isNullOrEmpty(references)) {
			for (IncidentReference reference : references) {
				if (IncidentReference.isOpen(reference)) {
					openIncidentCount++;
				}
			}
		}
		return openIncidentCount;
	}
	
	public static Collection<Incident> getOpenIncidents(
		Collection<IncidentReference> references
	) {
		Collection<Incident> openIncidents = new ArrayList<Incident>();
		if (Iterables.isNullOrEmpty(references)) {
			return openIncidents;
		}
		for (IncidentReference reference : references) {
			if (IncidentReference.isOpen(reference)) {
				openIncidents.add(reference.getIncident());
			}
		}
		return openIncidents;
	}	
	
	@XmlTransient
	public boolean isConfirmed() {
		return Incident.isConfirmed(incident);
	}
	
	public static boolean isOpen(IncidentReference reference) {
		return (reference != null) && Incident.isOpen(reference.getIncident());
	}
	
	public static boolean isConfirmed(IncidentReference reference) {
		return (reference != null) && Incident.isConfirmed(
			reference.getIncident()
		);
	}
	
	@XmlTransient
	public boolean isOpen() {
		return Incident.isOpen(incident);
	}
	
	@XmlTransient
	public IncidentSeverity getSeverity() {
		return Incident.getSeverity(incident);
	}
	
	@XmlTransient
	public boolean isResolved() {
		synchronized (this) {
			return incident != null;
		}
	}
	
	@XmlAttribute(name = "id")
	@Override
	public String getId() {
		return id;
	}
	
	@XmlAttribute(name = "href")
	public String getHref() {
		return href;
	}
	
	@XmlElementRef(type = Incident.class)
	public Incident getIncident() {
		synchronized (this) {
			return incident;
		}
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public void setHref(String href) {
		this.href = href;
	}
	
	public void setIncident(Incident incident) {
		synchronized (this) {
			this.incident = incident;
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
		IncidentReference other = (IncidentReference) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
}
