package com.dynatrace.fastpacks.metadata.instances;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = Instance.XML_ELEMENT_INSTANCE)
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Instance {
	
	public static final String XML_ELEMENT_INSTANCE = "instance";
	public static final String XML_ATTRIBUTE_INSTANCE = "instance";

	private InstanceType instanceType = null;
	
	public Instance() {
		// constructor for JAXB
	}
	
	public Instance(InstanceType instanceType) {
		this.instanceType = instanceType;
	}
	
	@XmlAttribute(name = Instance.XML_ATTRIBUTE_INSTANCE)
	public InstanceType getInstanceType() {
		return instanceType;
	}
	
	public void setInstanceType(InstanceType instance) {
		this.instanceType = instance;
	}
	
	public static boolean isValid(Instance instance) {
		if (instance == null) {
			return false;
		}
		return InstanceType.isValid(instance.getInstanceType());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((instanceType == null) ? 0 : instanceType.hashCode());
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Instance other = (Instance) obj;
		if (instanceType != other.instanceType)
			return false;
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return new StringBuilder("Instance[instanceType=").append(instanceType.toString()).append("]").toString();
	}
	
}
