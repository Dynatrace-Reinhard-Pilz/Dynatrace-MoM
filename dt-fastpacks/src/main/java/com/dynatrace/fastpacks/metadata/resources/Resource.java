package com.dynatrace.fastpacks.metadata.resources;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.dynatrace.fastpacks.metadata.instances.Instance;
import com.dynatrace.fastpacks.metadata.instances.InstanceType;
import com.dynatrace.utils.Source;
import com.dynatrace.utils.Strings;

/**
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 */
@XmlRootElement(name = "resource")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Resource {

	@XmlTransient
	private Source<?> source = null;
	private String name = null;
	private ResourceType type = null;
	private boolean overwrite = true;
	private String targetDir = null;
	private final Collection<Instance> instances = new ArrayList<Instance>();
	
	public Resource() {
		// constructor for JAXB
	}
	
	public Resource(Source<?> source, ResourceType type) {
		Objects.requireNonNull(source);
		ResourceType.validate(type);
		this.source = source;
		this.name = source.name();
		this.type = type;
	}

	@XmlTransient
	public String getName() {
		return name;
	}
	
	@XmlAttribute(name = "resource", required = true)
	public String getResource() {
		return type.getStorage() + "/" + name;
	}
	
	@XmlAttribute(name = "resource_type", required = true)
	public ResourceType getType() {
		return type;
	}
	
	@XmlAttribute(name = "overwrite", required = false)
	public boolean isOverwrite() {
		return overwrite;
	}
	
	@XmlAttribute(name = "target_dir", required = false)
	public String getTargetDir() {
		return targetDir;
	}
	
	@XmlElementRef(type = Instance.class, required = true)
	public Collection<Instance> getInstances() {
		return instances;
	}
	
	public void setTargetDir(String targetDir) {
		this.targetDir = targetDir;
	}
	
	public void setName(String name) {
		if (Strings.isNullOrEmpty(name)) {
			throw new IllegalArgumentException(
				"Resource name must not be null or empty"
			);
		}
		this.name = name;
	}
	
	public void setType(ResourceType type) {
		this.type = type;
	}
	
	public void setOverwrite(boolean overwrite) {
		this.overwrite = overwrite;
	}
	
	public void setInstances(Collection<Instance> instances) {
		this.instances.clear();
		if (instances == null) {
			return;
		}
		for (Instance instance : instances) {
			if (Instance.isValid(instance) && !this.instances.contains(instance)) {
				this.instances.add(instance);
			}
		}
	}
	
	public void addInstance(Instance instance) {
		if (!Instance.isValid(instance)) {
			return;
		}
		if (instances.contains(instance)) {
			return;
		}
		instances.add(instance);
	}
	
	public static boolean isValid(Resource resource) {
		if (resource == null) {
			return false;
		}
		if (!ResourceType.isValid(resource.getType())) {
			return false;
		}
		final String resourceName = resource.getName();
		if (resourceName == null) {
			return false;
		}
		return true;
	}
	
	public static void validate(Resource resource) {
		if (resource == null) {
			throw new IllegalArgumentException("Resource must not be null");
		}
		String name = resource.getName();
		if (Strings.isNullOrEmpty(name)) {
			throw new IllegalArgumentException("Resource name must not be null");
		}
	}
	
	public static Resource create(Source<?> source, ResourceType type) {
//		if (!file.exists() || !file.isFile()) {
//			throw new IllegalArgumentException(
//				file.getAbsolutePath() + " does not exist"
//			);
//		}
		Resource resource = new Resource(source, type);
		switch (type) {
		case dashboard:
			resource.setInstances(
				Collections.<Instance>nCopies(
					1,
					new Instance(InstanceType.server)
				)
			);
			break;
		case systemProfile:
			resource.setInstances(
				Collections.<Instance>nCopies(
					1,
					new Instance(InstanceType.server)
				)
			);
			break;
		case userPlugin:
			resource.setInstances(
				Collections.<Instance>nCopies(
					1,
					new Instance(InstanceType.server)
				)
			);
			break;
		case resource:
			resource.setInstances(
					Collections.<Instance>nCopies(
						1,
						new Instance(InstanceType.server)
					)
				);
				break;
		case agentRes:
			resource.setInstances(
				Collections.<Instance>nCopies(
					1,
					new Instance(InstanceType.server)
				)
			);
			break;
		case licenseFile:
			resource.setInstances(
				Collections.<Instance>nCopies(
					1,
					new Instance(InstanceType.server)
				)
			);
			break;
		case sensorPack:
			resource.setInstances(
				Collections.<Instance>nCopies(
					1,
					new Instance(InstanceType.server)
				)
			);
			break;
		case session:
			resource.setInstances(
				Collections.<Instance>nCopies(
					1,
					new Instance(InstanceType.server)
				)
			);
			break;
		case coreBundle:
			break;
		case eueDatabase:
			break;
		case geoInfoDatabase:
			break;
		case systemPlugin:
			break;
		}
		return resource;
	}
	
	public InputStream openStream() throws IOException {
		return source.openStream();
	}
	
	public long length() {
		return source.length();
	}
	
}
