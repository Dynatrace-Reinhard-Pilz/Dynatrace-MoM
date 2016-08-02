package com.dynatrace.fastpacks.metadata;

import java.util.Collection;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import com.dynatrace.fastpacks.metadata.resources.Resource;
import com.dynatrace.fastpacks.metadata.resources.ResourceType;
import com.dynatrace.utils.Source;

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
	
	public Plugin add(Source<?> source, ResourceType resourceType) {
		add(Resource.create(source, resourceType));
		return this;
	}

	public Plugin add(Source<?> source, ResourceType resourceType, String targetDir) {
		Resource resource = Resource.create(source, resourceType);
		resource.setTargetDir(targetDir);
		add(resource);
		return this;
	}
	
	public Plugin addUserPlugin(Source<?> source) {
		return add(source, ResourceType.userPlugin);
	}
	
	public Plugin addProfile(Source<?> source) {
		return add(source, ResourceType.systemProfile);
	}
	
	public final Plugin addDashboard(Source<?> source) {
		return add(source, ResourceType.dashboard);
	}
	
	public final Plugin addResource(Source<?> source, String targetDir) {
		return add(source, ResourceType.resource, targetDir);
	}
	
	public final void setName(String name) {
		extension.setName(name);
	}
	
}
