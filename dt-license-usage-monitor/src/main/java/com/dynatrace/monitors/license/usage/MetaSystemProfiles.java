package com.dynatrace.monitors.license.usage;

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
public class MetaSystemProfiles implements Iterable<MetaSystemProfile> {
	
	private Collection<MetaSystemProfile> profiles = new ArrayList<MetaSystemProfile>();
	
	@XmlElementRef(type = MetaSystemProfile.class)
	public Collection<MetaSystemProfile> getProfiles() {
		synchronized (this.profiles) {
			return new ArrayList<MetaSystemProfile>(profiles);
		}
	}
	
	public void setProfiles(Collection<MetaSystemProfile> profiles) {
		synchronized (this.profiles) {
			this.profiles.clear();
			if (profiles != null) {
				this.profiles.addAll(profiles);
			}
		}
	}

	@Override
	public Iterator<MetaSystemProfile> iterator() {
		synchronized (this.profiles) {
			return new ArrayList<MetaSystemProfile>(profiles).iterator();
		}
	}
	
	public int size() {
		synchronized (this.profiles) {
			return this.profiles.size();
		}
	}
	
}
