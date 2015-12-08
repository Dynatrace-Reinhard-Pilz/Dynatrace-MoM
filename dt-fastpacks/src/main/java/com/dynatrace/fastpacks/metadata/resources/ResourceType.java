package com.dynatrace.fastpacks.metadata.resources;

import java.util.Objects;

public enum ResourceType {
	
	dashboard("dashboards"),
	systemProfile("profiles"),
	userPlugin("userPlugins"),
	systemPlugin("systemPlugins"),
	sensorPack("sensorPacks"),
	agentRes("agentRes"),
	resource("resources"),
	geoInfoDatabase("geoInfoDatabase"),
	eueDatabase("eueDatabase"),
	licenseFile("licenseFile"),
	session("sessions"),
	coreBundle("coreBundles");
	
	private final String storage;
	
	private ResourceType(final String storage) {
		Objects.requireNonNull(storage);
		this.storage = storage.intern();
	}
	
	public static boolean isValid(ResourceType resourceType) {
		return (resourceType != null);
	}
	
	public static void validate(ResourceType resourceType) {
		if (!isValid(resourceType)) {
			throw new IllegalArgumentException(
					"ResourceType must not be null or undefined"
			);
		}
	}
	
	public String getStorage() {
		return storage;
	}
	
}
