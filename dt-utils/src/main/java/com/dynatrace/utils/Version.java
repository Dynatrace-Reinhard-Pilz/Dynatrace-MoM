package com.dynatrace.utils;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * 
 * @author Reinhard Pilz
 *
 */
@XmlRootElement(name = "version")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "major", "minor", "revision", "build" })
public final class Version implements Versionable, Serializable {
	
	private static final long serialVersionUID = 1L;

	public static final Version UNDEFINED = new Version();
	
	private static final String MSG_ILLEGAL_ARGUMENT =
			"\"{0}\" is not a valid version number".intern();
	public static final char DOT = '.';

	@XmlAttribute(name = "major")
	private final int major;
	@XmlAttribute(name = "minor")
	private final int minor;
	@XmlAttribute(name = "revision")
	private final int revision;
	@XmlAttribute(name = "build")
	private final int build;
	
	public Version() {
		major = 0;
		minor = 0;
		revision = 0;
		build = 0;
	}
	
	/**
	 * c'tor
	 * 
	 * @param major major version
	 * @param minor minor version
	 * @param revision revision
	 * @param buildNumber the build number of the version
	 */
	public Version(
			final int major,
			final int minor,
			final int revision,
			final int build
	) {
		this.major = major;
		this.minor = minor;
		this.revision = revision;
		this.build = build;
	}
	
	public static final Version parse(final String s) {
		return parse(s, true);
	}
	
	public static boolean isValid(Version version) {
		if (version == null) {
			return false;
		}
		if (version.equals(Version.UNDEFINED)) {
			return false;
		}
		return true;
	}
	
	/**
	 * Parses
	 * @param s
	 * @return
	 */
	public static final Version parse(String s, boolean failOnError) {
		if (failOnError) {
			Objects.requireNonNull(s);
		} else if (s == null) {
			return null;
		}
		try {
			String sVersion = s.trim();
			String part = null;
			int idx = sVersion.indexOf(DOT);
			if (idx == -1) {
				if (failOnError) {
					throw new IllegalArgumentException(
							MessageFormat.format(MSG_ILLEGAL_ARGUMENT, s)
					);
				}
				return null;
			}
			part = sVersion.substring(0, idx);
			final int major = Integer.parseInt(part);
			sVersion = sVersion.substring(idx + 1);
			idx = sVersion.indexOf(DOT);
			if (idx == -1) {
				if (failOnError) {
					throw new IllegalArgumentException(
							MessageFormat.format(MSG_ILLEGAL_ARGUMENT, s)
					);
				}
				return null;
			}
			part = sVersion.substring(0, idx);
			final int minor = Integer.parseInt(part);
			sVersion = sVersion.substring(idx + 1);
			idx = sVersion.indexOf(DOT);
			if (idx == -1) {
				if (failOnError) {
					throw new IllegalArgumentException(
							MessageFormat.format(MSG_ILLEGAL_ARGUMENT, s)
					);
				}
				return null;
			}
			part = sVersion.substring(0, idx);
			final int revision = Integer.parseInt(part);
			part = sVersion.substring(idx + 1);
			if (part.indexOf(DOT) != -1) {
				if (failOnError) {
					throw new IllegalArgumentException(
							MessageFormat.format(MSG_ILLEGAL_ARGUMENT, s)
					);
				}
				return null;
			}
			final int build = Integer.parseInt(part);
			return new Version(major, minor, revision, build);
		} catch (final NumberFormatException e) {
			if (failOnError) {
				throw new IllegalArgumentException(
						MessageFormat.format(MSG_ILLEGAL_ARGUMENT, s),
						e
				);
			}
			return null;
		}
	}
	
	public boolean matchesMajor(Version version) {
		return (this.major == version.major);
	}
	
	public boolean matchesMinor(Version version) {
		return matchesMajor(version) && (this.minor == version.minor);
	}
	
	public boolean matchesRevision(Version version) {
		return matchesMinor(version) && (this.revision == version.revision);
	}
	
	@Override
	public boolean includes(Versionable versionable) {
		if (versionable == null) {
			return false;
		}
		Version version = versionable.getVersion();
		if (version == null) {
			return false;
		}
		if ((this.major == 6) && (this.minor == 2)) {
			if (this.matchesMinor(version) && (this.revision >= version.revision)) {
				return true;
			}
		}
		return matchesRevision(version) && (this.build >= version.build);
	}
	
	/**
	 * @return the major version
	 */
	public final int getMajor() {
		return major;
	}
	
	/**
	 * @return the minor version
	 */
	public final int getMinor() {
		return minor;
	}
	
	/**
	 * @return the revision of the version
	 */
	public final int getRevision() {
		return revision;
	}
	
	/**
	 * @return the build number of the version
	 */
	public final int getBuild() {
		return build;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + build;
		result = prime * result + major;
		result = prime * result + minor;
		result = prime * result + revision;
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		return equals((Version) obj);
	}
	
	public final boolean equals(Version other) {
		if (other == null) {
			return false;
		}
		if (build != other.build)
			return false;
		if (major != other.major)
			return false;
		if (minor != other.minor)
			return false;
		if (revision != other.revision)
			return false;
		return true;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {
		return new StringBuilder().append(major).append(DOT).append(minor).
				append(DOT).append(revision).append(DOT).
				append(build).toString();
	}

	@Override
	public Version getVersion() {
		return this;
	}

	@Override
	public int compareTo(Versionable o) {
		return compareTo(o, false);
	}
	
	public int compareTo(Versionable o, boolean includeBuild) {
		if (o == null) {
			return 1;
		}
		Version version = o.getVersion();
		if (version == null) {
			return 1;
		}
		
		if (this.major > version.getMajor()) {
			return 1;
		} else if (this.major < version.getMajor()) {
			return -1;
		} else if (this.minor > version.getMinor()) {
			return 1;
		} else if (this.minor < version.getMinor()) {
			return -1;
		} else if (this.revision > version.getRevision()) {
			return 1;
		} else if (this.revision < version.getRevision()) {
			return -1;
		}
		if (includeBuild) {
			if (this.build > version.getBuild()) {
				return 1;
			} else if (this.build < version.getBuild()) {
				return -1;
			}
		}
		return 0;
		
	}

	@Override
	public void updateVersion(Version version) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean equals(Versionable versionable) {
		if (versionable == null) {
			return false;
		}
		Version version = versionable.getVersion();
		return equals(version);
	}
	
	public static boolean equals(Versionable a, Versionable b) {
		if (a == null) {
			return b == null;
		} else if (b == null) {
			return false;
		}
		return a.equals(b);
	}
}
