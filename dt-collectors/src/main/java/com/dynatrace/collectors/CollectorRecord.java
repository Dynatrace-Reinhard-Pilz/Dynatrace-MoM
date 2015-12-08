package com.dynatrace.collectors;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dynatrace.utils.Labelled;
import com.dynatrace.utils.Unique;
import com.dynatrace.utils.Version;
import com.dynatrace.utils.Versionable;

/**
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 */
public class CollectorRecord implements Labelled, Unique<CollectorInfo>, Versionable {
	
	private static final Logger LOGGER =
			Logger.getLogger(CollectorRecord.class.getName());
	
	private final SimpleDateFormat sdf =
			new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private Version version = Version.UNDEFINED;
	
	private CollectorInfo collectorInfo = null;
	private Labelled parent = null;
	private RestartStatus restartStatus = RestartStatus.NONE;
	private long startupTime = 0L;
	
	public CollectorRecord() {
	}
	
	public CollectorRecord(Labelled parent, CollectorInfo collectorInfo) {
		Objects.requireNonNull(collectorInfo);
		setCollectorInfo(collectorInfo);
		this.parent = parent;
		this.version = collectorInfo.getVersion();
	}
	
	public boolean isRestartRequired() {
		synchronized (this) {
			return this.restartStatus == RestartStatus.REQUIRED;
		}
	}
	
	public boolean isRestarting() {
		synchronized (this) {
			return RestartStatus.isRestarting(this.restartStatus);
		}
	}
	
	public long getStartupTime() {
		return startupTime;
	}
	
	public void setStartupTime(long startupTime) {
		if (startupTime == 0L) {
			return;
		}
		synchronized (this) {
			if (this.startupTime == startupTime) {
				return;
			}
			LOGGER.log(Level.FINER, "Startup Time of Collector " + this + " set to " + sdf.format(new Date(startupTime)) + " (Restart Status: " + this.restartStatus + ")"); 
			this.startupTime = startupTime;
			if (RestartStatus.isRestarting(this.restartStatus)) {
				setRestartStatus(RestartStatus.NONE);
			}
		}
	}
	
	public RestartStatus getRestartStatus() {
		return restartStatus;
	}
	
	public void setRestartStatus(RestartStatus restartStatus) {
		synchronized (this) {
			if (this.restartStatus != restartStatus) {
				LOGGER.log(Level.INFO, "Restart " + restartStatus + " for " + this);
			}
			this.restartStatus = restartStatus;
		}
	}
	
	public String getServerName() {
		return parent.getName();
	}
	
	public CollectorInfo getCollectorInfo() {
		return collectorInfo;
	}
	
	public void setCollectorInfo(CollectorInfo collectorInfo) {
		Objects.requireNonNull(collectorInfo);
		if (this.collectorInfo != null && this.collectorInfo.isConnected() != collectorInfo.isConnected()) {
			if (!this.collectorInfo.isConnected()) {
				this.restartStatus = RestartStatus.NONE;
			}
		}
		this.collectorInfo = collectorInfo;
		Version version = collectorInfo.getVersion();
		if (Version.isValid(version)) {
			this.version = version;
		}
	}
	
	public boolean isConnected() {
		return collectorInfo.isConnected();
	}
	
	public boolean isEmbedded() {
		return collectorInfo.isEmbedded();
	}
	
	public String getHost() {
		return collectorInfo.getHost();
	}

	@Override
	public String getName() {
		return collectorInfo.getName();
	}

	public Version getVersion() {
		synchronized (this) {
			return version;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((collectorInfo == null) ? 0 : collectorInfo.hashCode());
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
		CollectorRecord other = (CollectorRecord) obj;
		if (collectorInfo == null) {
			if (other.collectorInfo != null)
				return false;
		} else if (!collectorInfo.equals(other.collectorInfo))
			return false;
		return true;
	}

	@Override
	public CollectorInfo getId() {
		return collectorInfo;
	}
	
	@Override
	public String toString() {
		return collectorInfo.toString();
	}

	@Override
	public int compareTo(Versionable o) {
		return version.compareTo(o);
	}

	@Override
	public boolean includes(Versionable versionable) {
		return version.includes(versionable);
	}

	@Override
	public void updateVersion(Version version) {
		synchronized (this) {
			this.version = version;
		}
	}
	
	@Override
	public boolean equals(Versionable versionable) {
		return getVersion().equals(versionable);
	}
	
}
