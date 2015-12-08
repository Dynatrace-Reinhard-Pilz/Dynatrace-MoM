package com.dynatrace.fixpacks;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlValue;

import com.dynatrace.utils.Strings;
import com.dynatrace.utils.Version;
import com.dynatrace.utils.Versionable;

/*
<installjob id="65f566ba-2cb6-4127-936e-8a5feb63dea7">
    <isfinished>true</isfinished>
    <isstarted>true</isstarted>
    <issuccess>true</issuccess>
    <isserverrestartrequired>true</isserverrestartrequired>
    <result />
</installjob>
 */

@XmlRootElement(name = "installjob")
@XmlAccessorType(XmlAccessType.FIELD)
public final class FixPackInstallStatus implements Versionable {
	
	@XmlAttribute(name = "id")
	private String id = null;	
	@XmlElement(name = "isfinished")
	private boolean isFinished = false;
	@XmlElement(name = "isstarted")
	private boolean isStarted = false;
	@XmlElement(name = "issuccess")
	private boolean isSuccess = false;
	@XmlElement(name = "issserverrestartrequired")
	private boolean isServerRestartRequired = false;
	@XmlElement(type = XmlResultJobResult.class)
	private XmlResultJobResult result = null;
	
	@XmlTransient
	private Version fixPackVersion = Version.UNDEFINED;
	@XmlTransient
	private InstallStatus installStatus = InstallStatus.INSTALLING;
	
	public InstallStatus getInstallStatus() {
		synchronized (this) {
			return installStatus;
		}
	}
	
	public void setInstallStatus(InstallStatus installStatus) {
		synchronized (this) {
			this.installStatus = installStatus;
		}
	}
	
	public Version getFixPackVersion() {
		return fixPackVersion;
	}
	
	public void setFixPackVersion(Version fixPackVersion) {
		this.fixPackVersion = fixPackVersion;
	}
	
	public final boolean isFinished() {
		return isFinished;
	}
	
	public final boolean isStarted() {
		return isStarted;
	}
	
	public final boolean isSuccess() {
		return isSuccess;
	}
	
	public final boolean isServerRestartRequired() {
		return isServerRestartRequired;
	}
	
	public final String getMessage() {
		if (result == null) {
			return null;
		}
		return result.getMessage();
	}
	
	public final String getLanguage() {
		if (result == null) {
			return null;
		}
		return result.getLanguage();
	}
	
	@XmlRootElement(name = "result")
	public final static class XmlResultJobResult {
		
		@XmlElement(name = "errormessage")
		private XmlResultJobMessage message = null;
		
		public final String getMessage() {
			if (message == null) {
				return null;
			}
			return message.getMessage();
		}
		
		public final String getLanguage() {
			if (message == null) {
				return null;
			}
			return message.getLanguage();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((message == null) ? 0 : message.hashCode());
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
			final XmlResultJobResult other = (XmlResultJobResult) obj;
			if (message == null) {
				if (other.message != null)
					return false;
			} else if (!message.equals(other.message))
				return false;
			return true;
		}
		
	}
	
	@XmlRootElement(name = "message")
	public final static class XmlResultJobMessage {
		@XmlValue
		public String message = null;
		
		@XmlAttribute(name = "language")
		public String language = null;
		
		public final String getMessage() {
			return message;
		}
		
		public final String getLanguage() {
			return language;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((language == null) ? 0 : language.hashCode());
			result = prime * result
					+ ((message == null) ? 0 : message.hashCode());
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
			final XmlResultJobMessage other = (XmlResultJobMessage) obj;
			if (language == null) {
				if (other.language != null)
					return false;
			} else if (!language.equals(other.language))
				return false;
			if (message == null) {
				if (other.message != null)
					return false;
			} else if (!message.equals(other.message))
				return false;
			return true;
		}
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {
		return Strings.toString(
			this,
			id,
			isFinished,
			isStarted,
			isSuccess,
			isServerRestartRequired,
			getMessage(),
			getLanguage()
		);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + (isFinished ? 1231 : 1237);
		result = prime * result + (isServerRestartRequired ? 1231 : 1237);
		result = prime * result + (isStarted ? 1231 : 1237);
		result = prime * result + (isSuccess ? 1231 : 1237);
		result = prime * result
				+ ((this.result == null) ? 0 : this.result.hashCode());
		return result;
	}

	@Override
	public final boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		return equals((FixPackInstallStatus) obj);
	}
	
	public final boolean equals(FixPackInstallStatus other) {
		if (other == null) {
			return false;
		}
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (isFinished != other.isFinished)
			return false;
		if (isServerRestartRequired != other.isServerRestartRequired)
			return false;
		if (isStarted != other.isStarted)
			return false;
		if (isSuccess != other.isSuccess)
			return false;
		if (result == null) {
			if (other.result != null)
				return false;
		} else if (!result.equals(other.result))
			return false;
		return true;
	}

	@Override
	public int compareTo(Versionable o) {
		synchronized (this) {
			return fixPackVersion.compareTo(o);
		}
	}

	@Override
	public Version getVersion() {
		synchronized (this) {
			return fixPackVersion;
		}
	}

	@Override
	public boolean includes(Versionable versionable) {
		synchronized (this) {
			return fixPackVersion.includes(versionable);
		}
	}

	@Override
	public void updateVersion(Version version) {
		synchronized (this) {
			fixPackVersion = version;
		}
	}
	
	@Override
	public boolean equals(Versionable versionable) {
		return getVersion().equals(versionable);
	}

}
