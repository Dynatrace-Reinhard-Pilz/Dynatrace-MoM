package com.dynatrace.fixpacks;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name = "fixpackcontent")
@XmlAccessorType(XmlAccessType.FIELD)
public class FixPackContent {

	@XmlAttribute(name = "version")
	private String version = null;
	
	@XmlElement(type = Description.class, name = "description")
	private Description description = new Description();
	
	@XmlElementRef(type = Bundle.class, name = "bundle")
	private Collection<Bundle> bundles = new ArrayList<Bundle>();
	
	public String getVersion() {
		return version;
	}
	
	public void setVersion(String version) {
		this.version = version;
		if (description == null) {
			description = new Description();
		}
		description.setDescriptiveText("FixPack " + version);
	}
	
	@XmlRootElement(name = "target")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static final class Target {
		
		@XmlValue
		private String value = "server";
		
		public String getValue() {
			return value;
		}
		
		public void setValue(String value) {
			this.value = value;
		}
	}
	
	
	@XmlRootElement(name = "bundle")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static final class Bundle {
		@XmlAttribute(name = "symbolicName")
		private String symbolicName = null;
		@XmlAttribute(name = "version")
		private String version = null;
		@XmlElement(type = Target.class, name = "target")
		private Target target = new Target();
		
		public String getVersion() {
			return version;
		}
		
		public void setVersion(String version) {
			this.version = version;
		}
		
		public String getSymbolicName() {
			return symbolicName;
		}
		
		public void setSymbolicName(String symbolicName) {
			this.symbolicName = symbolicName;
		}
		
		public final Target getTarget() {
			return target;
		}
		
		public final void setTarget(final Target target) {
			this.target = target;
		}
	}
	
	@XmlRootElement(name = "description")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static final class Description {
		@XmlAttribute(name = "author")
		private String author = "Compuware Corporation";
		@XmlAttribute(name = "descriptive_text")
		private String descriptiveText="FixPack 6.1.0.8191";
		@XmlAttribute(name = "license_ile")
		private String licenseFile="dynaTraceBSD.txt";
		@XmlAttribute(name = "provider")
		private String provider="dynatrace";
		@XmlAttribute(name = "support_level")
		private String supportLevel="supported";
		
		public String getAuthor() {
			return author;
		}
		
		public void setAuthor(String author) {
			this.author = author;
		}
		
		public String getDescriptiveText() {
			return descriptiveText;
		}
		
		public void setDescriptiveText(String descriptiveText) {
			this.descriptiveText = descriptiveText;
		}
		
		public String getLicenseFile() {
			return licenseFile;
		}
		
		public void setLicenseFile(String licenseFile) {
			this.licenseFile = licenseFile;
		}
		
		public String getProvider() {
			return provider;
		}
		
		public void setProvider(String provider) {
			this.provider = provider;
		}
		
		public String getSupportLevel() {
			return supportLevel;
		}
		
		public void setSupportLevel(String supportLevel) {
			this.supportLevel = supportLevel;
		}
	}

}
