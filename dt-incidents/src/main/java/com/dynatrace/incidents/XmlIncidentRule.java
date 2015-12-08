package com.dynatrace.incidents;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

/*

<incidents>
<incidentreference id="c64e8a40-05e4-45f0-9286-c6766b2a2d86" href="https://localhost:8021/rest/management/profiles/dynaTrace%20Self-Monitoring/incidentrules/Performance%20Warehouse%20Offline/incidents/c64e8a40-05e4-45f0-9286-c6766b2a2d86"/>
<incidentreference id="fb63ca40-c27d-4e9a-9383-68bf4ac6f1af" href="https://localhost:8021/rest/management/profiles/dynaTrace%20Self-Monitoring/incidentrules/Performance%20Warehouse%20Offline/incidents/fb63ca40-c27d-4e9a-9383-68bf4ac6f1af"/>
<incidentreference id="4f0198bc-60ac-489d-ad40-69e55f51800f" href="https://localhost:8021/rest/management/profiles/dynaTrace%20Self-Monitoring/incidentrules/Performance%20Warehouse%20Offline/incidents/4f0198bc-60ac-489d-ad40-69e55f51800f"/>
</incidents>

 */
@XmlRootElement(name = "incidents")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class XmlIncidentRule {
	
	private String name = null;
	private Collection<IncidentReference> incidents = new ArrayList<IncidentReference>();
	
	/**
	 * c'tor
	 */
	public XmlIncidentRule() {
		
	}
	
	/**
	 * c'tor
	 * 
	 * @param name the incident rule name
	 */
	public XmlIncidentRule(String name) {
		this.name = name;
	}
	

	
	@XmlAttribute(name = "name")
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@XmlElementRef(type = IncidentReference.class)
	public Collection<IncidentReference> getIncidents() {
		return incidents;
	}
	
	public void setIncidents(Collection<IncidentReference> incidents) {
		this.incidents = incidents;
	}
	
	public void clearIncidentReferences() {
		this.incidents.clear();
	}
	
	public void removeIncidentReference(IncidentReference incidentReference) {
		Objects.requireNonNull(incidentReference);
		this.incidents.remove(incidentReference);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		XmlIncidentRule other = (XmlIncidentRule) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
