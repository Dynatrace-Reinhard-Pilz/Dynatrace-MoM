package com.dynatrace.utils;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Storage {
	
	private static final Logger LOGGER =
			Logger.getLogger(Storage.class.getName());
	
	private static final String PROPERTY_USER_HOME = "user.home";
	private static final String PROPERTY_USER_DIR = "user.dir";
	private static final String PROPERTY_TEMP_DIR = "java.io.tmpdir";
	public static final String PROPERTY_WORK_DIR = "mom.working.dir";
	
	private static final String FOLDER_NAME_DYNATRACE = ".dynatrace";

	private static File STANDARD = resolveStorage();
	
	public final static File standard() {
		return STANDARD;
	}
	
	public static void refresh() {
		STANDARD = resolveStorage();
	}
	
	private final static File resolveStorage() {
		final File fldMomWorkDir = resolveStorage(PROPERTY_WORK_DIR);
		if (fldMomWorkDir != null) {
			return fldMomWorkDir;
		}
		final File fldUserHomeDynatrace = resolveStorage(PROPERTY_USER_HOME);
		if (fldUserHomeDynatrace != null) {
			return fldUserHomeDynatrace;
		}
		final File fldUserDirDynatrace = resolveStorage(PROPERTY_USER_DIR);
		if (fldUserDirDynatrace != null) {
			return fldUserDirDynatrace;
		}
		final File fldCurrentDirDynatrace = resolveStorage(new File("."));
		if (fldCurrentDirDynatrace != null) {
			return fldCurrentDirDynatrace;
		}
		final File fldTempDirDynatrace = resolveStorage(PROPERTY_TEMP_DIR);
		if (fldTempDirDynatrace != null) {
			return fldTempDirDynatrace;
		}
		throw new InternalError("Unable to find a folder to store data");
	}
	
	private final static File resolveStorage(final String property) {
		final String folderName = System.getProperty(property);
		if (folderName == null) {
			return null;
		}
		return resolveStorage(new File(folderName));
	}
	
	private static File resolveStorage(File baseFolder) {
		LOGGER.log(Level.INFO, "Trying to resolve Storage Folder " + baseFolder);
		if (baseFolder == null) {
			return null;
		}
		if (!baseFolder.exists()) {
			return null;
		}
		if (!checkWriteAccess(baseFolder)) {
			return null;
		}
		final File fldDynatrace = new File(baseFolder, FOLDER_NAME_DYNATRACE);
		if (!fldDynatrace.exists()) {
			if (!fldDynatrace.mkdirs()) {
				return null;
			}
		}
		if (!checkWriteAccess(fldDynatrace)) {
			return null;
		}
		return fldDynatrace;
	}
	
	private static boolean checkWriteAccess(final File folder) {
		final File randomFile = new File(folder, UUID.randomUUID().toString());
		try {
			if (!randomFile.createNewFile()) {
				return false;
			}
		} catch (final IOException e) {
			return false;
		} finally {
			randomFile.delete();
		}
		return true;
	}
}
