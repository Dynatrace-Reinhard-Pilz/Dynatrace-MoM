package com.dynatrace.sysinfo;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import com.dynatrace.profiles.SystemProfile;

public class SysInfoResult {
	
	private final File file;
	private final Collection<SystemProfile> profiles =
			new ArrayList<SystemProfile>();
	
	public SysInfoResult(File file) {
		this.file = file;
	}
	
	public File getFile() {
		return file;
	}
	
	@Override
	protected void finalize() throws Throwable {
		file.delete();
	}
	
	public Collection<SystemProfile> getProfiles() {
		return new ArrayList<SystemProfile>(profiles);
	}
	
	public void addProfile(SystemProfile systemProfile) {
		profiles.add(systemProfile);
	}

}
