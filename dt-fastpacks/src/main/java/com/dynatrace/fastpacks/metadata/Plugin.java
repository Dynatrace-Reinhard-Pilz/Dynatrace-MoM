package com.dynatrace.fastpacks.metadata;

import java.io.File;
import java.util.Collection;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import com.dynatrace.fastpacks.metadata.resources.Resource;
import com.dynatrace.fastpacks.metadata.resources.ResourceType;

@XmlRootElement(name = "plugin")
@XmlAccessorType(XmlAccessType.FIELD)
public final class Plugin {
	
	public static final String FILENAME = "plugin.xml";

	@XmlElementRef(type = Extension.class)
	private Extension extension = new Extension();
	
	public Plugin() {
		
	}
	
	public Plugin(String name, String id) {
		setName(name);
		setId(id);
	}
	
	public Extension getExtension() {
		return extension;
	}
	
	public String getId() {
		return extension.getId();
	}

	public String getName() {
		return extension.getMetaInfo().getName();
	}
	
	public String getPoint() {
		return extension.getPoint();
	}

	public MetaInfo getMetaInfo() {
		return extension.getMetaInfo();
	}
	
	public void setId(String id) {
		this.extension.setId(id);
	}
	
	public InstallerType getInstallerType() {
		return extension.getMetaInfo().getInstallerType();
	}
	
	public Collection<Resource> getResources() {
		return extension.getMetaInfo().getResources();
	}
	
	public Plugin add(Resource resource) {
		Objects.requireNonNull(resource);
		extension.getMetaInfo().getResources().add(resource);
		return this;
	}
	
	public Plugin add(Resource... resources) {
		Objects.requireNonNull(resources);
		for (Resource resource : resources) {
			add(resource);
		}
		return this;
	}
	
	public Plugin add(File file, ResourceType resourceType) {
		add(Resource.create(file, resourceType));
		return this;
	}

	public Plugin add(File file, ResourceType resourceType, String targetDir) {
		Resource resource = Resource.create(file, resourceType);
		resource.setTargetDir(targetDir);
		add(resource);
		return this;
	}
	
	public Plugin addUserPlugin(File file) {
		return add(file, ResourceType.userPlugin);
	}
	
	public Plugin addProfile(File file) {
		return add(file, ResourceType.systemProfile);
	}
	
	public final Plugin addDashboard(File file) {
		return add(file, ResourceType.dashboard);
	}
	
	public final Plugin addResource(File file, String targetDir) {
		return add(file, ResourceType.resource, targetDir);
	}
	
	public final void setName(String name) {
		extension.setName(name);
	}
	
}
