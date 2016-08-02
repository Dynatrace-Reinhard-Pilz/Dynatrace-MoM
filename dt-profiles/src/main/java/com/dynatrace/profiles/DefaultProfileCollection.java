package com.dynatrace.profiles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Logger;

public class DefaultProfileCollection implements ProfileCollection {
	
	private static final Logger LOGGER =
			Logger.getLogger(DefaultProfileCollection.class.getName());

	private final HashMap<String, SystemProfile> profiles =
			new HashMap<String, SystemProfile>();

	@Override
	public SystemProfile get(String profileName) {
		synchronized (this.profiles) {
			return this.profiles.get(profileName);
		}
	}
	
	public SystemProfile getSelfMonitoringProfile() {
		synchronized (this.profiles) {
			return this.profiles.get(SystemProfile.SELFMONITORING);
		}
	}
	
	public void onProfileRemoved(SystemProfile profile) {
		// subclasses may override
	}
	
	public void onProfileAdded(SystemProfile profile) {
		// subclasses may override
	}
	
	public void onProfileUpdated(SystemProfile profile) {
		// subclasses may override
	}
	
	private void removeMissing(Iterable<SystemProfile> profiles) {
		Iterator<String> it = null;
		for (it = this.profiles.keySet().iterator(); it.hasNext(); ) {
			String profileName = it.next();
			boolean found = false;
			for (SystemProfile profile : profiles) {
				if (profileName.equals(profile.getId())) {
					found = true;
				}
			}
			if (!found) {
				SystemProfile profile = get(profileName);
				it.remove();
				onProfileRemoved(profile);
			}
		}
	}
	
	@Override
	public void addAll(Iterable<SystemProfile> profiles) {
		if (profiles == null) {
			return;
		}
		synchronized (this.profiles) {
			removeMissing(profiles);
			for (SystemProfile profile : profiles) {
				add(profile);
			}
		}
	}
	
	@Override
	public void add(SystemProfile profile) {
		if (profile == null) {
			return;
		}
		synchronized (this.profiles) {
			String profileName = profile.getId();
			SystemProfile storedProfile = this.profiles.get(profileName);
			if (storedProfile == null) {
				this.profiles.put(profileName, profile);
				onProfileAdded(profile);
			} else {
				storedProfile.setLocalFile(profile.getLocalFile());
				onProfileUpdated(profile);
			}
		}
	}
	
	@Override
	public Iterator<SystemProfile> iterator() {
		synchronized (this.profiles) {
			return new ArrayList<SystemProfile>(profiles.values()).iterator();
		}
	}

	@Override
	public int size() {
		synchronized (this.profiles) {
			return this.profiles.size();
		}
	}
}
