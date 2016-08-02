package com.dynatrace.utils.jar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dynatrace.utils.Closeables;
import com.dynatrace.utils.Version;

public final class Jars {
	
	private static final Logger LOGGER = Logger.getLogger(Jars.class.getName());
	
	public static final String ATTRIBUTE_BUNDLE_VERSION =
			"Bundle-Version".intern();

	private Jars() {
		// prevent instantiation
	}
	
	public static Version getBundleVersion(File jarFile) {
		String sBundleVersion = getManifestAttribute(
			jarFile,
			ATTRIBUTE_BUNDLE_VERSION
		);
		return resolveVersion(sBundleVersion);
	}
	
	private static Version resolveVersion(String sBundleVersion) {
		if (sBundleVersion == null) {
			LOGGER.log(Level.SEVERE, "Unable to determine Bundle Version");
			return Version.UNDEFINED;
		}
		try {
			return Version.parse(sBundleVersion);
		} catch (IllegalArgumentException e) {
			LOGGER.log(
				Level.SEVERE,
				"Bundle Version '" + sBundleVersion + "' is invalid"
			);
			return Version.UNDEFINED;
		}
	}
	
	public static Version getBundleVersion(InputStream manifest) {
		String sBundleVersion = getManifestAttribute(
			manifest,
			ATTRIBUTE_BUNDLE_VERSION
		);
		return resolveVersion(sBundleVersion);
	}
	
	public static String getManifestAttribute(File jarFile, String attributeName) {
		if (jarFile == null) {
			return null;
		}
		if (attributeName == null) {
			return null;
		}
		if (!Closeables.existsFile(jarFile)) {
			return null;
		}
		try (
			InputStream is = new FileInputStream(jarFile);
			JarInputStream in = new JarInputStream(is);
		) {
			Manifest manifest = in.getManifest();
			if (manifest == null) {
				return null;
			}
			Attributes attributes = manifest.getMainAttributes();
			if (attributes == null) {
				return null;
			}
			return attributes.getValue(attributeName);
		} catch (IOException e) {
			LOGGER.log(Level.WARNING, "Unable to access JAR file '" + jarFile.getAbsolutePath() + "'", e);
			return null;
		}
	}
	
	public static String getManifestAttribute(InputStream in, String attributeName) {
		try {
			Manifest manifest = new Manifest(in);
			return getManifestAttribute(manifest, attributeName);
		} catch (IOException e) {
			LOGGER.log(Level.WARNING, "Unable to resolve Manifest from input stream", e);
			return null;
		}
	}
	
	public static String getManifestAttribute(Manifest manifest, String attributeName) {
		Attributes attributes = manifest.getMainAttributes();
		if (attributes == null) {
			return null;
		}
		return attributes.getValue(attributeName);
	}
	
	public static File extractResource(ClassLoader c, String project, String bundleName) {
		if (c == null) {
			return null;
		}
		if (project == null) {
			return null;
		}
		if (bundleName == null) {
			return null;
		}
		try (
			InputStream in = c.getResourceAsStream("resources/" + project + "/resources");
			InputStreamReader isr = new InputStreamReader(in);
			BufferedReader br = new BufferedReader(isr);
		) {
			if (in == null) {
				return null;
			}
			String readLine = br.readLine();
			while (readLine != null) {
				try (InputStream bundleFileIn = c.getResourceAsStream("resources/" + project + "/" + readLine)) {
					File tmpFile = File.createTempFile(Jars.class.getName() + ".", ".tmp");
					tmpFile.deleteOnExit();
					try (OutputStream out = new FileOutputStream(tmpFile)) {
						Closeables.copy(bundleFileIn, out);
						Closeables.close(out);
					} catch (IOException e) {
						e.printStackTrace(System.err);
						throw e;
					}
					String bundleNameAttribute = getManifestAttribute(tmpFile, "Bundle-Name");
					if (bundleName.equals(bundleNameAttribute)) {
						return tmpFile;
					}
					tmpFile.delete();
				} catch (IOException e) {
					e.printStackTrace(System.err);
					return null;
				}
				readLine = br.readLine();
			}
			return null;
		} catch (IOException e) {
			e.printStackTrace(System.err);
			return null;
		}
	}	
	
}
