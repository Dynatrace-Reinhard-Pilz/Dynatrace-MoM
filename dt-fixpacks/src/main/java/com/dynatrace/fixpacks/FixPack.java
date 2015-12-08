package com.dynatrace.fixpacks;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dynatrace.utils.Version;
import com.dynatrace.utils.Versionable;
import com.dynatrace.xml.XMLUtil;

public class FixPack implements Versionable {
	
	private static final Logger LOGGER =
			Logger.getLogger(FixPack.class.getName());
	
	public static final String FILE_EXTENSION = ".dtf";
	public static final String MANIFEST_HEADER_BUNDLE_VERSION =
			"Bundle-Version";

	private final File file;
	private final Version version;
	private final FixPackInfo fpi;
	
	public FixPack(final File file, FixPackInfo fpi, final Version version) {
		Objects.requireNonNull(file);
		Objects.requireNonNull(version);
		Objects.requireNonNull(fpi);
		this.file = file;
		this.version = version;
		this.fpi = fpi;
	}
	
	public final FixPackInfo getFixPackInfo() {
		return fpi;
	}
	
	public final File getFile() {
		return file;
	}
	
	public final InputStream openStream() throws IOException {
		return new FileInputStream(file);
	}
	
	public final void delete() {
		file.delete();
	}
	
	@Override
	public final Version getVersion() {
		return version;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {
		return version.toString();
	}
	
	public static final boolean isFixPackFile(final File fixPackFile) {
		if (fixPackFile == null) {
			return false;
		}
		if (!fixPackFile.isFile()) {
			return false;
		}
		if (!fixPackFile.getName().endsWith(FILE_EXTENSION)) {
			return false;
		}
		return true;
	}
	
	public static final Version getFixPackVersion(final File fixPackFile) {
		try (
			final InputStream fis = new FileInputStream(fixPackFile);
			final JarInputStream jis = new JarInputStream(fis);
		) {
			String bundleVersion = jis.getManifest().getMainAttributes()
					.getValue(MANIFEST_HEADER_BUNDLE_VERSION);
			return Version.parse(bundleVersion);
		} catch (final Exception e) {
			LOGGER.log(Level.WARNING,
					"FixPack File " + fixPackFile + " is invalid.");
		}
		return null;
	}
	
	public static final FixPackInfo extractFixPackInfo(final File fixPackFile) {
		try (
				final InputStream fis = new FileInputStream(fixPackFile);
				final JarInputStream jis = new JarInputStream(fis);
			) {
				JarEntry entry = jis.getNextJarEntry();
				while (entry != null) {
					String entryName = entry.getName();
					if (entryName.equals("fixpackinfo.xml")) {
						return XMLUtil.<FixPackInfo>deserialize(jis, FixPackInfo.class);
					}
					entry = jis.getNextJarEntry();
				}
				return null;
			} catch (final Exception e) {
				LOGGER.log(Level.WARNING,
						"FixPack File " + fixPackFile + " is invalid.");
				fixPackFile.delete();
			}
			return null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((version == null) ? 0 : version.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FixPack other = (FixPack) obj;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}

	@Override
	public boolean includes(Versionable versionable) {
		return this.version.includes(versionable);
	}

	@Override
	public int compareTo(Versionable o) {
		Version version = null;
		if (this.version == null) {
			if (o == null) {
				return 0;
			}
			version = o.getVersion();
			if (version == null) {
				return 0;
			}
		}
		return this.version.compareTo(version);
	}

	@Override
	public void updateVersion(Version version) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean equals(Versionable versionable) {
		return getVersion().equals(versionable);
	}
	
}
