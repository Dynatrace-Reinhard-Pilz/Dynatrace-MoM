package com.dynatrace.license;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.dynatrace.utils.Iterables;
import com.dynatrace.xml.XMLUtil;

@XmlRootElement(name = "licenseinformation")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class LicenseInfo {
	
	private String licensedTo = null;
	private String licenseNumber = null;
	private String licenseEdition = null;
	private String usedVolumePercentage = null;
	private String nextVolumeRenwalDate = null;
	private String validFrom = null;
	private String expireData = null;
	private String currentUemTransactions = null;
	private String maximalUemTransactions = null;
	private Collection<LicensedAgent> licensedAgents =
			new ArrayList<LicensedAgent>();
	
	public void setLicensedTo(String licensedTo) {
		this.licensedTo = licensedTo;
	}
	
	@XmlElement(name = "licensedto")
	public String getLicensedTo() {
		return licensedTo;
	}
	
	public void setLicenseNumber(String licenseNumber) {
		this.licenseNumber = licenseNumber;
	}
	
	@XmlElement(name = "licensenumber")
	public String getLicenseNumber() {
		return licenseNumber;
	}
	
	public void setLicenseEdition(String licenseEdition) {
		this.licenseEdition = licenseEdition;
	}
	
	@XmlElement(name = "licenseedition")
	public String getLicenseEdition() {
		return licenseEdition;
	}
	
	public void setUsedVolumePercentage(String usedVolumePercentage) {
		this.usedVolumePercentage = usedVolumePercentage;
	}
	
	@XmlElement(name = "usedvolumepercentage")
	public String getUsedVolumePercentage() {
		return usedVolumePercentage;
	}
	
	public void setNextVolumeRenwalDate(String nextVolumeRenwalDate) {
		this.nextVolumeRenwalDate = nextVolumeRenwalDate;
	}
	
	@XmlElement(name = "nextvolumerenewaldate")
	public String getNextVolumeRenwalDate() {
		return nextVolumeRenwalDate;
	}
	
	public void setValidFrom(String validFrom) {
		this.validFrom = validFrom;
	}
	
	@XmlElement(name = "validfrom")
	public String getValidFrom() {
		return validFrom;
	}
	
	public void setExpireData(String expireData) {
		this.expireData = expireData;
	}
	
	@XmlElement(name = "validfrom")
	public String getExpireData() {
		return expireData;
	}
	
	public void setCurrentUemTransactions(String currentUemTransactions) {
		this.currentUemTransactions = currentUemTransactions;
	}
	
	@XmlElement(name = "currentuemtransactions")
	public String getCurrentUemTransactions() {
		return currentUemTransactions;
	}
	
	public void setMaximalUemTransactions(String maximalUemTransactions) {
		this.maximalUemTransactions = maximalUemTransactions;
	}
	
	@XmlElement(name = "maximaluemtransactions")
	public String getMaximalUemTransactions() {
		return maximalUemTransactions;
	}
	
	public void setLicensedAgents(Collection<LicensedAgent> licensedAgents) {
		synchronized (this.licensedAgents) {
			this.licensedAgents.clear();
			if (!Iterables.isNullOrEmpty(licensedAgents)) {
				for (LicensedAgent licensedAgent : licensedAgents) {
					if (licensedAgent == null) {
						continue;
					}
					if (licensedAgent.getCount() == LicensedAgent.NONE) {
						continue;
					}
					this.licensedAgents.add(licensedAgent);
				}
			}
		}
	}
	
	@XmlElementWrapper(name = "licensedagents")
	@XmlElementRef(type = LicensedAgent.class)
	public Collection<LicensedAgent> getLicensedAgents() {
		synchronized (licensedAgents) {
			return new ArrayList<LicensedAgent>(licensedAgents);
		}
	}
	
	@Override
	public String toString() {
		return XMLUtil.toString(this);
	}
	
}