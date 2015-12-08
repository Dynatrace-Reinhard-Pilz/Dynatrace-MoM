package com.dynatrace.fastpacks.metadata.manifest;

import java.util.Objects;
import java.util.jar.Attributes;

public enum ManifestAttribute {
	
	ManifestVersion("Manifest-Version"),
	BundleManifestVersion("Bundle-ManifestVersion"),
	BundleName("Bundle-Name"),
	BundleSymbolicName("Bundle-SymbolicName"),
	BundleVersion("Bundle-Version"),
	BundleVendor("Bundle-Vendor"),
	BundleAuthor("Bundle-Author"),
	SpecificationTitle("Specification-Title"),
	SpecificationVersion("Specification-Version"),
	SpecificationVendor("Specification-Vendor"),
	ImplementationTitle("Implementation-Title"),
	ImplementationVersion("Implementation-Version"),
	ImplementationVendor("Implementation-Vendor");
	
	private final String key;
	
	private ManifestAttribute(String key) {
		this.key = key;
	}
	
	public String getKey() {
		return key;
	}
	
	public void putValue(Attributes attributes, String value) {
		Objects.requireNonNull(attributes);
		Objects.requireNonNull(value);
		attributes.putValue(key, value);
	}
}
