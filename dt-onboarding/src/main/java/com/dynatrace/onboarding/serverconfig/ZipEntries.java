package com.dynatrace.onboarding.serverconfig;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;

public class ZipEntries {
	
	private static final Logger LOGGER =
			Logger.getLogger(ZipEntries.class.getName());

	public static String getFileName(ZipEntry zipEntry) {
		if (zipEntry == null) {
			return null;
		}
		String zipEntryName = zipEntry.getName();
		if (zipEntryName == null) {
			return null;
		}
		int idx = zipEntryName.lastIndexOf('/');
		if (idx < 0) {
			return zipEntryName;
		}
		return zipEntryName.substring(idx + 1);
	}
	
	public static boolean isServerZipEntry(ZipEntry zipEntry) {
		if (zipEntry == null) {
			return false;
		}
		return isServerFile(zipEntry.getName());
	}
	
	public static boolean isServerFile(String zipEntryName) {
		if (zipEntryName == null) {
			return false;
		}
		return zipEntryName.startsWith("Server/");
	}
	
	public static boolean isDashboard(ZipEntry zipEntry) {
		if (zipEntry == null) {
			return false;
		}
		String fileName = ZipEntries.getFileName(zipEntry);
		if (fileName == null) {
			return false;
		}
		return isDashboard(fileName);
	}
	
	public static boolean isDashboard(String fileName) {
		if (fileName == null) {
			return false;
		}
		if (fileName.endsWith(".dashboard.xml")) {
			LOGGER.log(Level.FINEST, fileName + " is a dashboard");
			return true;
		} else {
			LOGGER.log(Level.FINEST, fileName + " is NOT a dashboard");
			return false;
		}
	}
	
}
