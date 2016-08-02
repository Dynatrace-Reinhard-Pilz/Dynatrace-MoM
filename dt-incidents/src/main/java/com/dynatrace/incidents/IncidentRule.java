package com.dynatrace.incidents;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.dynatrace.utils.Iterables;
import com.dynatrace.utils.SizedIterable;
import com.dynatrace.utils.Strings;
import com.dynatrace.utils.Unique;

/*

<incidents>
<incidentreference id="c64e8a40-05e4-45f0-9286-c6766b2a2d86" href="https://localhost:8021/rest/management/profiles/dynaTrace%20Self-Monitoring/incidentrules/Performance%20Warehouse%20Offline/incidents/c64e8a40-05e4-45f0-9286-c6766b2a2d86"/>
<incidentreference id="fb63ca40-c27d-4e9a-9383-68bf4ac6f1af" href="https://localhost:8021/rest/management/profiles/dynaTrace%20Self-Monitoring/incidentrules/Performance%20Warehouse%20Offline/incidents/fb63ca40-c27d-4e9a-9383-68bf4ac6f1af"/>
<incidentreference id="4f0198bc-60ac-489d-ad40-69e55f51800f" href="https://localhost:8021/rest/management/profiles/dynaTrace%20Self-Monitoring/incidentrules/Performance%20Warehouse%20Offline/incidents/4f0198bc-60ac-489d-ad40-69e55f51800f"/>
</incidents>

 */
@XmlRootElement(name = "incidents")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class IncidentRule
implements SizedIterable<IncidentReference>, Unique<String> {
	
	private String name = null;
	private Collection<IncidentReference> incidents =
			new ArrayList<IncidentReference>();
	
	/**
	 * c'tor
	 */
	public IncidentRule() {
		
	}
	
	/**
	 * c'tor
	 * 
	 * @param name the incident rule name
	 */
	public IncidentRule(String name) {
		this.name = name;
	}
	
	public IncidentSeverity getSeverity() {
		if (incidents == null) {
			return null;
		}
		for (IncidentReference incidentReference : incidents) {
			if (incidentReference == null) {
				continue;
			}
			if (incidentReference.getId() == null) {
				continue;
			}
			if (incidentReference != null) {
				IncidentSeverity severity = incidentReference.getSeverity();
				if (severity != null) {
					return severity;
				}
			}
		}
		return IncidentSeverity.informational;
	}
	
	@XmlTransient
	public int getOpenIncidentCount() {
		return IncidentReference.getOpenIncidentCount(getIncidents());
	}
	
	public static int getOpenIncidentCount(IncidentRule incidentRule) {
		if (incidentRule == null) {
			return 0;
		}
		return incidentRule.getOpenIncidentCount();
	}
	
	@XmlTransient
	public Collection<Incident> getOpenIncidents() {
		return IncidentReference.getOpenIncidents(getIncidents());
	}	
	
	public static Collection<Incident> getOpenIncidents(
		IncidentRule incidentRule
	) {
		if (incidentRule == null) {
			return Collections.emptyList();
		}
		return incidentRule.getOpenIncidents();
	}
	
	public static Collection<Incident> getOpenIncidents(
		Collection<IncidentRule> incidentRules
	) {
		if (Iterables.isNullOrEmpty(incidentRules)) {
			return Collections.emptyList();
		}
		Collection<Incident> openIncidents = new ArrayList<Incident>();
		for (IncidentRule incidentRule : incidentRules) {
			openIncidents.addAll(getOpenIncidents(incidentRule));
		}
		return openIncidents;
	}
	
	@Override
	@XmlTransient
	public String getId() {
		return name;
	}
	
	@XmlAttribute(name = "name")
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@XmlTransient
	public String getSizedName() {
		int size = size();
		if (size == 0) {
			return name;
		}
		return new StringBuilder(name).append(" (").append(size).append(")")
			.toString();
	}
	
	@XmlElement(type = IncidentReference.class)
	public Collection<IncidentReference> getIncidents() {
		synchronized (this) {
			return incidents;
		}
	}
	
	public void setIncidents(Collection<IncidentReference> incidents) {
		synchronized (this) {
			this.incidents = incidents;
		}
	}
	
	private boolean containsIncident(String id) {
		if (id == null) {
			return false;
		}
		synchronized (this) {
			for (IncidentReference incidentReference : incidents) {
				if (incidentReference == null) {
					continue;
				}
				if (id.equals(incidentReference.getId())) {
					return true;
				}
			}
			return false;
		}
	}
	
	public void updateIncidentReferences(
		Collection<IncidentReference> incidentReferences
	) {
		synchronized (this) {
			for (IncidentReference incidentReference : incidentReferences) {
				if (incidentReference == null) {
					continue;
				}
				String id = incidentReference.getId();
				if (id == null) {
					continue;
				}
				if (!containsIncident(id)) {
					incidents.add(incidentReference);
				}
			}
			for (Iterator<IncidentReference> it = incidents.iterator(); it.hasNext(); ) {
				IncidentReference incidentReference = it.next();
				if (incidentReference == null) {
					it.remove();
					continue;
				}
				String id = incidentReference.getId();
				if (id == null) {
					it.remove();
					continue;
				}
				boolean exists = false;
				for (IncidentReference iref : incidentReferences) {
					if (incidentReference == null) {
						continue;
					}
					if (Strings.equals(id, iref.getId())) {
						exists = true;
						break;
					}
				}
				if (!exists) {
					it.remove();
				}
			}
		}
	}
	
	public void clearIncidentReferences() {
		this.incidents.clear();
	}
	
	public void removeIncidentReference(IncidentReference incidentReference) {
		Objects.requireNonNull(incidentReference);
		this.incidents.remove(incidentReference);
	}

	@Override
	public Iterator<IncidentReference> iterator() {
		synchronized (this) {
			if (incidents == null) {
				return Collections.emptyIterator();
			}
			return new ArrayList<IncidentReference>(
				incidents
			).iterator();
		}
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
		IncidentRule other = (IncidentRule) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	@Override
	public int size() {
		int count = 0;
		synchronized (this) {
			for (IncidentReference incidentReference : incidents) {
				if (incidentReference == null) {
					continue;
				}
				Incident incident = incidentReference.getIncident();
				if (incident == null) {
					continue;
				}
				if (incident.getState() == IncidentState.Confirmed) {
					continue;
				}
				count++;
			}
		}
		return count;
	}

}
