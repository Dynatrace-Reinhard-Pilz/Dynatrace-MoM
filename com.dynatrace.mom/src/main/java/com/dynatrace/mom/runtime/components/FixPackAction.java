package com.dynatrace.mom.runtime.components;

import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Logger;

import com.dynatrace.collectors.CollectorRecord;
import com.dynatrace.fixpacks.FixPack;
import com.dynatrace.fixpacks.FixPackInstallStatus;
import com.dynatrace.fixpacks.InstallStatus;
import com.dynatrace.utils.Version;
import com.dynatrace.utils.Versionable;

public class FixPackAction implements Versionable {
	
	@SuppressWarnings("unused")
	private static final Logger LOGGER =
			Logger.getLogger(FixPackAction.class.getName());

	private final FixPack fixPack;
	private final ServerRecord serverRecord;
	
	public FixPackAction(FixPack fixPack, ServerRecord serverRecord) {
		Objects.requireNonNull(fixPack);
		Objects.requireNonNull(serverRecord);
		this.fixPack = fixPack;
		this.serverRecord = serverRecord;
	}
	
	public boolean isInstalled() {
		return getInstallStatus() == InstallStatus.INSTALLED;
	}
	
	public boolean isInstalling() {
		switch (getInstallStatus()) {
		case INSTALLED:
		case NOTINSTALLED:
			return false;
		default:
			return true;
		}
	}
	
	public InstallStatus getInstallStatus() {
		if (serverRecord.equals(fixPack)) {
			for (CollectorRecord collector : getNonMatchingCollectors()) {
				if (!collector.includes(fixPack)) {
					return InstallStatus.COLLECTORRESTARTREQUIRED;
				}
			}
			FixPackInstallStatus s = serverRecord.getFixPackInstallStatus();
			if (s != null) {
				InstallStatus installStatus = s.getInstallStatus();
				if (installStatus == InstallStatus.INSTALLED) {
					serverRecord.setFixPackInstallStatus(null);
					return InstallStatus.INSTALLED;
				}
			}
			return InstallStatus.INSTALLED;
		} else {
			FixPackInstallStatus fpis = serverRecord.getFixPackInstallStatus();
			if ((fpis != null) && Version.equals(fpis, fixPack)) {
				return fpis.getInstallStatus();
			}
			return InstallStatus.NOTINSTALLED;
		}
	}
	
	public Iterable<CollectorRecord> getNonMatchingCollectors() {
		ArrayList<CollectorRecord> nonMatchingCollectors = new ArrayList<>();
		Iterable<CollectorRecord> collectors = serverRecord.getCollectors();
		for (CollectorRecord collector : collectors) {
			if (!collector.equals(fixPack)) {
				nonMatchingCollectors.add(collector);
			}
		}
		return nonMatchingCollectors;
	}
	
	public ServerRecord getServerRecord() {
		return serverRecord;
	}
	
	public FixPack getFixPack() {
		return fixPack;
	}

	@Override
	public int compareTo(Versionable o) {
		return fixPack.compareTo(o);
	}

	@Override
	public Version getVersion() {
		return fixPack.getVersion();
	}

	@Override
	public boolean includes(Versionable versionable) {
		return fixPack.includes(versionable);
	}

	@Override
	public void updateVersion(Version version) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean equals(Versionable versionable) {
		return getVersion().equals(versionable);
	}
	
	@Override
	public String toString() {
		return fixPack.getVersion().toString();
	}
	
}
