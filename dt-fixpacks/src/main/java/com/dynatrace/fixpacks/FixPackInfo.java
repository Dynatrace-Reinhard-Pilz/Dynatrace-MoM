package com.dynatrace.fixpacks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "dt:fixpackinfo")
@XmlAccessorType(XmlAccessType.FIELD)
public final class FixPackInfo {

	@XmlAttribute(name = "version")
	private String version = null;
	@XmlElementRef(type = Fix.class)
	private Collection<Fix> fixes = null;
	
	
	public final void setVersion(final String version) {
		this.version = version;
	}
	
	public final String getVersion() {
		return version;
	}
	
	public final Collection<Fix> getFixes() {
		return fixes;
	}
	
	public final void setFixes(final Collection<Fix> fixes) {
		this.fixes = fixes;
	}
	
	public final void addFix(final Fix fix) {
		Objects.requireNonNull(fix);
		if (this.fixes == null) {
			this.fixes = new ArrayList<Fix>();
		}
		fixes.add(fix);
	}

}
