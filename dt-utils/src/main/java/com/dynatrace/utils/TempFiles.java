package com.dynatrace.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TempFiles {
	
	private static final Logger LOGGER =
			Logger.getLogger(TempFiles.class.getName());
	
	private static final File TEMP =
			new File(System.getProperty("java.io.tmpdir", "."));
	
	public static final String PROPERTY_TMP_DIR_NAME = "dt-utils.io.tmpdir";

	public static synchronized final File getTempFolder(String name) {
		String subFolderName =
				System.getProperty(PROPERTY_TMP_DIR_NAME, ".dt-utils");
		File dtTempFilesFolder = new File(TEMP, subFolderName);
		File dtPluggability = new File(dtTempFilesFolder, name);
		if (!dtPluggability.exists() && !dtPluggability.mkdirs()) {
			throw new InternalError("Unable to create root temp directory");
		}
		purgeOldTempFolders(dtPluggability);
		File processTmpFolder = new File(
			dtPluggability,
			UUID.randomUUID().toString()
		);
		if (!processTmpFolder.mkdirs()) {
			throw new InternalError("Unable to create parent temp directory");
		}
		processTmpFolder.deleteOnExit();
		File lockFile = new File(processTmpFolder, ".lock");
		lockFile.deleteOnExit();
		try (
			OutputStream out = new FileOutputStream(lockFile);
			InputStream in = new ByteArrayInputStream(
				processTmpFolder.getName().getBytes()
			);
		) {
			Closeables.copy(in, out);
		} catch (IOException e) {
			throw new InternalError("Unable to create lock file");
		}
		LOGGER.log(
			Level.FINEST,
			"Pluggability Temp Folder: " + processTmpFolder.getAbsolutePath()
		);
		return processTmpFolder;
	}
	
	private static synchronized void purgeOldTempFolders(File folder) {
		final File[] folders = folder.listFiles();
		for (File child : folders) {
			if (!Closeables.existsFolder(child)) {
				continue;
			}
			File lockFile = new File(child, ".lock");
			if (!lockFile.exists()) {
				Closeables.purge(child);
			}
		}
	}
	
}
