package com.dynatrace.mom;

import java.io.File;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javax.servlet.ServletContextEvent;

public class MomConfig {
	
	private static final Logger LOGGER =
			Logger.getLogger(MomConfig.class.getName());
	
	private static Preferences PREFERENCES = Preferences.userNodeForPackage(
		MomConfig.class
	);
	
	// public static final String ATTRIBUTE = MomConfig.class.getName();
	public static final String ATTRIBUTE = UUID.randomUUID().toString();
	
	private static final String KEY_STORAGE = "storage";
	
	private File storage = null;
	
	public MomConfig() {
//		PREFERENCES.remove(KEY_STORAGE);
		String storageLocation = PREFERENCES.get(KEY_STORAGE, null);
		if (storageLocation != null) {
			setStorage(new File(storageLocation));
		}
	}
	
	public void setStorage(File storage) {
		if (storage == null) {
			return;
		}
		if (!storage.exists()) {
			if (!storage.mkdirs()) {
				return;
			}
		}
		this.storage = storage;
		LOGGER.log(Level.INFO, "Storage location: " + storage.getAbsolutePath());
		PREFERENCES.put(KEY_STORAGE, storage.getAbsolutePath());
	}
	
	public File getStorage() {
		return storage;
	}
	
	
}
