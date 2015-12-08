package com.dynatrace.fixpacks;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import com.dynatrace.utils.Version;

/*
<plugin>
  <extension id="InstallerContent-dynaTrace-6.1.0.7881" name="InstallerContent-dynaTrace-6.1.0.7881" point=\"com.dynatrace.diagnostics.InstallerContent\">
    <metainfo base_version="6.1.0.7880" installer_type="patch" name="6.1.0.7881" version="6.1.0.7881">
      <resource resource="filename" resource_type="resource" target_dir="targetfolder">
        <instance instance="server"/>
      </resource>
    </metainfo>
  </extension>
</plugin>
*/

@XmlRootElement(name = "plugin")
@XmlAccessorType(XmlAccessType.FIELD)
public final class InstallerContent {
	
	@XmlElementRef(type = Extension.class, name = "extension")
	private Extension extension = new Extension();
	
	public final void setExtension(final Extension extension) {
		this.extension = extension;
	}

	public final void setVersion(final Version version) {
		ensureExtension();
		extension.setId("InstallerContent-dynaTrace-" + version);
		extension.setName("InstallerContent-dynaTrace-" + version);
		extension.setVersion(version.toString());
	}
	
	public final Version getVersion() {
		if (extension == null) {
			return null;
		}
		return Version.parse(extension.getVersion());
	}
	
	public final void setId(final String id) {
		ensureExtension();
		extension.setId(id);
	}
	
	public final String getId() {
		if (extension == null) {
			return null;
		}
		return extension.getId();
	}
	
	public final void setName(final String name) {
		ensureExtension();
		extension.setName(name);
	}
	
	public final String getName() {
		if (extension == null) {
			return null;
		}
		return extension.getName();
	}
	
	public final void setPoint(final String point) {
		ensureExtension();
		extension.setPoint(point);
	}
	
	public final String getPoint() {
		if (extension == null) {
			return null;
		}
		return extension.getPoint();
	}
	
	public final void setInstallerType(final InstallerType installerType) {
		ensureExtension();
		extension.setInstallerType(installerType);
	}
	
	public final InstallerType getInstallerType() {
		if (extension == null) {
			return null;
		}
		return extension.getInstallerType();
	}
	
	public final void setBaseVersion(final Version version) {
		ensureExtension();
		extension.setBaseVersion(version.toString());
	}
	
	public final Version getBaseVersion() {
		if (extension == null) {
			return null;
		}
		return Version.parse(extension.getBaseVersion());
	}
	
	public final Collection<Resource> getResources() {
		if (extension == null) {
			return null;
		}
		return extension.getResources();
	}
	
	public final void setResources(final Collection<Resource> resources) {
		ensureExtension();
		extension.setResources(resources);
	}
	
	public final void addResource(final Resource resource) {
		ensureExtension();
		extension.addResource(resource);
	}
	
	public final void addResources(final Collection<Resource> resources) {
		ensureExtension();
		extension.addResources(resources);
	}	
	
	
	public final MetaInfo getMetaInfo() {
		if (extension == null) {
			return null;
		}
		return extension.getMetaInfo();
	}
	
	public final void setMetaInfo(final MetaInfo metaInfo) {
		ensureExtension();
		extension.setMetaInfo(metaInfo);
	}
	
	private void ensureExtension() {
		if (extension == null) {
			extension = new Extension();
		}
	}
	
	/*
	  <extension id="InstallerContent-dynaTrace-6.1.0.7881" name="InstallerContent-dynaTrace-6.1.0.7881" point=\"com.dynatrace.diagnostics.InstallerContent\">
	    <metainfo base_version="6.1.0.7880" installer_type="patch" name="6.1.0.7881" version="6.1.0.7881">
	      <resource resource="filename" resource_type="resource" target_dir="targetfolder">
	        <instance instance="server"/>
	      </resource>
	    </metainfo>
	  </extension>
	*/
	@XmlRootElement(name = "extension")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static final class Extension {
		
		@XmlAttribute(name = "id")
		private String id = null;
		@XmlAttribute(name = "name")
		private String name = null;
		@XmlAttribute(name = "point")
		private String point = "com.dynatrace.diagnostics.InstallerContent";
		@XmlElement(type = MetaInfo.class, name = "metainfo")
		private MetaInfo metaInfo = new MetaInfo();
		
		public final void setId(final String id) {
			this.id = id;
		}
		
		public final String getId() {
			return id;
		}
		
		public final void setName(String name) {
			this.name = name;
		}
		
		public final String getName() {
			return name;
		}
		
		public final String getPoint() {
			return point;
		}
		
		public final void setPoint(final String point) {
			this.point = point;
		}
		
		public final void setInstallerType(final InstallerType installerType) {
			ensureMetaInfo();
			metaInfo.setInstallerType(installerType);
		}
		
		public final InstallerType getInstallerType() {
			if (metaInfo == null) {
				return null;
			}
			return metaInfo.getInstallerType();
		}
		
		public final void setBaseVersion(final String version) {
			ensureMetaInfo();
			metaInfo.setBaseVersion(version);
		}
		
		public final String getBaseVersion() {
			if (metaInfo == null) {
				return null;
			}
			return metaInfo.getBaseVersion();
		}
		
		public final void setVersion(final String version) {
			ensureMetaInfo();
			metaInfo.setVersion(version);
			metaInfo.setName("dynaTrace-" + version);
		}
		
		public final String getVersion() {
			if (metaInfo == null) {
				return null;
			}
			return metaInfo.getVersion();
		}
		
		public final MetaInfo getMetaInfo() {
			return metaInfo;
		}
		
		public final void setMetaInfo(final MetaInfo metaInfo) {
			this.metaInfo = metaInfo;
		}
		
		public final Collection<Resource> getResources() {
			if (metaInfo == null) {
				return null;
			}
			return metaInfo.getResources();
		}
		
		public final void setResources(final Collection<Resource> resources) {
			ensureMetaInfo();
			metaInfo.setResources(resources);
		}
		
		public final void addResource(final Resource resource) {
			ensureMetaInfo();
			metaInfo.addResource(resource);
		}
		
		public final void addResources(final Collection<Resource> resources) {
			ensureMetaInfo();
			metaInfo.addResources(resources);
		}	
		
		private void ensureMetaInfo() {
			if (metaInfo == null) {
				metaInfo = new MetaInfo();
			}
		}
	}
	
	/*
	    <metainfo base_version="6.1.0.7880" installer_type="patch" name="6.1.0.7881" version="6.1.0.7881">
	      <resource resource="filename" resource_type="resource" target_dir="targetfolder">
	        <instance instance="server"/>
	      </resource>
	    </metainfo>
	*/
	@XmlRootElement(name = "metainfo")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static final class MetaInfo {
		
		@XmlAttribute(name = "base_version")
		private String baseVersion = null;
		@XmlAttribute(name = "installer_type")
		private InstallerType installerType = InstallerType.patch;
		@XmlAttribute(name = "name")
		private String name = null;
		@XmlAttribute(name = "version")
		private String version = null;
		@XmlElementRef(type = Resource.class, name = "resource")
		private Collection<Resource> resources = new ArrayList<Resource>();
		
		public final String getBaseVersion() {
			return baseVersion;
		}
		
		public final void setBaseVersion(final String baseVersion) {
			this.baseVersion = baseVersion;
		}
		
		public final InstallerType getInstallerType() {
			return installerType;
		}
		
		public final void setInstallerType(final InstallerType installerType) {
			this.installerType = installerType;
		}
		
		public final String getName() {
			return name;
		}
		
		public final void setName(final String name) {
			this.name = name;
		}
		
		public final String getVersion() {
			return version;
		}
		
		public final void setVersion(final String version) {
			this.version = version;
		}
		
		public final Collection<Resource> getResources() {
			return resources;
		}
		
		public final void setResources(final Collection<Resource> resources) {
			this.resources = resources;
		}
		
		public final void addResource(final Resource resource) {
			if (resource == null) {
				return;
			}
			if (resources == null) {
				resources = new ArrayList<Resource>();
			}
			resources.add(resource);
		}
		
		public final void addResources(final Collection<Resource> resources) {
			if (resources == null) {
				return;
			}
			if (this.resources == null) {
				this.resources = new ArrayList<Resource>();
			}
			this.resources.addAll(resources);
		}		
	}
	
	/*
      <resource resource="filename" resource_type="resource" target_dir="targetfolder">
        <instance instance="server"/>
      </resource>
	*/
	@XmlRootElement(name = "resource")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static final class Resource {
		
		@XmlAttribute(name = "resource")
		private String resource = null;
		@XmlAttribute(name = "resource_type")
		private ResourceType resourceType = ResourceType.coreBundle;
		@XmlAttribute(name = "target_dir")
		private String targetDir = null;
		@XmlAttribute(name = "overwrite")
		private boolean isOverwrite = true;
		@XmlElementRef(type = Instance.class, name = "instance")
		private Collection<Instance> instances = new ArrayList<Instance>();
		
		public final String getResource() {
			return resource;
		}
		
		public final void setResource(final String resource) {
			this.resource = resource;
		}
		
		public final ResourceType getResourceType() {
			return resourceType;
		}
		
		public final void setResourceType(final ResourceType resourceType) {
			this.resourceType = resourceType;
		}
		
		public final String getTargetDir() {
			return targetDir;
		}
		
		public final void setTargetDir(final String targetDir) {
			this.targetDir = targetDir;
		}
		
		public final boolean isOverwrite() {
			return isOverwrite;
		}
		
		public final void setOverwrite(final boolean isOverwrite) {
			this.isOverwrite = isOverwrite;
		}
		
		public final void addInstance(final PluginInstance instance) {
			if (instance == null) {
				return;
			}
			if (instances == null) {
				instances = new ArrayList<Instance>();
			}
			instances.add(new Instance(instance));
		}
		
		public final Collection<Instance> getInstances() {
			return instances;
		}
		
		public final void setInstances(final Collection<Instance> instances) {
			this.instances = instances;
		}
		
		public final void addInstance(final Instance instance) {
			if (instance == null) {
				return;
			}
			if (instances == null) {
				instances = new ArrayList<Instance>();
			}
			instances.add(instance);
		}
		
		public final void addInstances(final Collection<Instance> instances) {
			if (instances == null) {
				return;
			}
			if (this.instances == null) {
				this.instances = new ArrayList<Instance>();
			}
			this.instances.addAll(instances);
		}
	}
	
	/*
      <instance instance="server"/>
	*/
	@XmlRootElement(name = "instance")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static final class Instance {
		
		public static final Instance Server =
				new Instance(PluginInstance.server);
		public static final Instance Collector =
				new Instance(PluginInstance.collector);
		public static final Instance Client =
				new Instance(PluginInstance.client);
		
		@XmlAttribute(name = "instance")
		private PluginInstance instance = PluginInstance.server;
		
		public Instance() {
			this.instance = PluginInstance.server;
		}
		
		public Instance(final PluginInstance instance) {
			this.instance = instance;
		}
		
		public final PluginInstance getInstance() {
			return instance;
		}
		
		public final void setInstance(final PluginInstance instance) {
			this.instance = instance;
		}
	}
	
	public static enum InstallerType {
		plugin,
		patch,
		resourcepack,
		languagepack
	}
	
	public static enum ResourceType {
		coreBundle,					// ==> root/plugins
		systemPlugin,				// ==> root/dtplugins/plugins
		userPlugin,					// ==> root/conf/plugins
		sensorPack,					// ==> root/conf/sensors/plugins
		systemProfile,				// ==> root/conf/profiles
		dashboard,					// ==> root/conf/dashboards
		agentRes,					// ==> root/lib
		resource,					// no specific directory
		eueDatabase,				// ==> fragment bundle for EUE module
		geoInfoDatabase,			// ==> fragment bundle for geoinfo module > will be replaced by eueDatabase
		licenseFile,				// ==> server license file
		session						// ==> root/sessions
	}
	
	public static enum PluginInstance {
		client,
		server,
		collector
	}
	
}
