package com.dynatrace.fastpacks.metadata.manifest;

import java.util.jar.Attributes;
import java.util.jar.Manifest;

import static com.dynatrace.fastpacks.metadata.manifest.ManifestAttribute.*;

public final class ManifestBuilder {
	
	public static final String MANIFEST_VERSION = "1.0".intern();
	public static final String BUNDLE_MANIFEST_VERSION = "2".intern();
	public static final String VENDOR = "dynatrace".intern();
	public static final String IMPLEMENTATION_VERSION = "1".intern();
	public static final String SPECIFICATION_VERSION = "6.3".intern();

	public Manifest build(
		String bundleName,
		String symbolicName,
		String version,
		String author
	) {
		Manifest manifest = new Manifest();
		Attributes attributes = manifest.getMainAttributes();
		ManifestVersion.putValue(attributes, MANIFEST_VERSION);
		BundleManifestVersion.putValue(attributes, BUNDLE_MANIFEST_VERSION);
		BundleName.putValue(attributes, bundleName);
		BundleSymbolicName.putValue(
			attributes,
			symbolicName + ";singleton:=true"
		);
		BundleVersion.putValue(attributes, version);
		BundleVendor.putValue(attributes, VENDOR);
		BundleAuthor.putValue(attributes, author);
		SpecificationTitle.putValue(attributes, bundleName);
		SpecificationVersion.putValue(attributes, SPECIFICATION_VERSION);
		SpecificationVendor.putValue(attributes, VENDOR);
		ImplementationTitle.putValue(attributes, bundleName);
		ImplementationVersion.putValue(attributes, IMPLEMENTATION_VERSION);
		ImplementationVendor.putValue(attributes, VENDOR);
		return manifest;
	}
}
