package com.dynatrace.fastpacks;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import com.dynatrace.fastpacks.metadata.Plugin;
import com.dynatrace.fastpacks.metadata.manifest.ManifestBuilder;
import com.dynatrace.fastpacks.metadata.resources.Resource;
import com.dynatrace.fastpacks.metadata.resources.ResourceType;
import com.dynatrace.utils.Closeables;
import com.dynatrace.utils.Version;
import com.dynatrace.xml.XMLUtil;

public class FastpackBuilder {
	
	private Version version = new Version(1, 0, 0, 0);
	private String author = "no.author";
	private Plugin plugin;
	
	public FastpackBuilder(String fastPackName, String fastPackId) {
		this.plugin = new Plugin(fastPackName, fastPackId);
	}
	
	public FastpackBuilder addResource(File file, String targetDir) {
		this.plugin = this.plugin.addResource(file, targetDir);
		return this;
	}
	
	public FastpackBuilder addDashboard(File file) {
		this.plugin = this.plugin.addDashboard(file);
		return this;
	}
	
	public FastpackBuilder addProfile(File file) {
		this.plugin = this.plugin.addProfile(file);
		return this;
	}
	
	public FastpackBuilder addUserPlugin(File file) {
		this.plugin = this.plugin.addUserPlugin(file);
		return this;
	}
	
	public Version getVersion() {
		return version;
	}
	
	public void setVersion(Version version) {
		this.version = version;
	}
	
	public void setAuthor(String author) {
		this.author = author;
	}
	
	public String getAuthor() {
		return author;
	}

	public void build(OutputStream out) throws IOException {
		ManifestBuilder manifestBuilder = new ManifestBuilder();
		Manifest manifest = manifestBuilder.build(
			plugin.getName(),
			plugin.getId(),
			version.toString(),
			author
		);
		
		try (
			JarOutputStream jos = new JarOutputStream(out, manifest);
		) {
			createPluginXml(plugin, jos);
			for (ResourceType resourceType : ResourceType.values()) {
				createResources(plugin, resourceType, jos);
			}
		}
	}
	
	private void createResources(Plugin plugin, ResourceType resourceType, JarOutputStream out) throws IOException {
		final Collection<Resource> resources = plugin.getResources();
		boolean isFolderExisting = false;
		for (Resource resource : resources) {
			if (resource.getType() == resourceType) {
				if (!isFolderExisting) {
					createResourceTypeFolder(resourceType, out);
					isFolderExisting = true;
				}
				createResource(resource, out);
			}
		}
	}
	
	private void createResource(Resource resource, JarOutputStream out) throws IOException {
		final JarEntry entry = new JarEntry(resource.getType().getStorage() + "/" + resource.getName());
		entry.setTime(System.currentTimeMillis());
		out.putNextEntry(entry);
		InputStream in = resource.getInputStream();
		Closeables.copy(in, out);
		in.close();
		out.flush();
		out.closeEntry();
	}
	
	private void createResourceTypeFolder(ResourceType resourceType, JarOutputStream out) throws IOException {
		final JarEntry entry = new JarEntry(resourceType.getStorage() + "/");
		entry.setTime(System.currentTimeMillis());
		entry.setMethod(JarOutputStream.STORED);
		entry.setSize(0L);
		entry.setCrc(0L);
		out.putNextEntry(entry);
		out.flush();
		out.closeEntry();
	}
	
	private void createPluginXml(Plugin plugin, JarOutputStream out) throws IOException {
		final JarEntry entry = new JarEntry(Plugin.FILENAME);
		entry.setTime(System.currentTimeMillis());
		out.putNextEntry(entry);
		XMLUtil.serialize(plugin, out, StandardCharsets.UTF_8, Plugin.class);
		out.flush();
		out.closeEntry();
	}

}
