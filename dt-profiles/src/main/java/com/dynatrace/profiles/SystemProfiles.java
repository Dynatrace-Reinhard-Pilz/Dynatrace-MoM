package com.dynatrace.profiles;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

/*

<profiles href="https://52.5.210.190:8021/rest/management/profiles">
<systemprofile isrecording="false" id="dynaTrace Self-Monitoring" href="https://52.5.210.190:8021/rest/management/profiles/dynaTrace%20Self-Monitoring"/>
<systemprofile isrecording="false" id="Monitoring" href="https://52.5.210.190:8021/rest/management/profiles/Monitoring"/>
</profiles>

 */
@XmlRootElement(name = "profiles")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class SystemProfiles implements Iterable<SystemProfile> {
	
	private Collection<SystemProfile> profiles = new ArrayList<SystemProfile>();
	
	@XmlElementRef(type = SystemProfile.class)
	public Collection<SystemProfile> getProfiles() {
		synchronized (this.profiles) {
			return new ArrayList<SystemProfile>(profiles);
		}
	}
	
	public void setProfiles(Collection<SystemProfile> profiles) {
		synchronized (this.profiles) {
			this.profiles.clear();
			if (profiles != null) {
				this.profiles.addAll(profiles);
			}
		}
	}

	@Override
	public Iterator<SystemProfile> iterator() {
		synchronized (this.profiles) {
			return new ArrayList<SystemProfile>(profiles).iterator();
		}
	}
	
	public int size() {
		synchronized (this.profiles) {
			return this.profiles.size();
		}
	}
	
}
