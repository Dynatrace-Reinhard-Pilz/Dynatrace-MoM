package com.dynatrace.utils;

import java.io.File;
import java.util.concurrent.Executor;
import java.util.logging.Level;

public interface ExecutionContext extends Executor {
	
	public static final String ATTRIBUTE_STORAGE_FOLDER =
			"mom.webapp.storage.file";
	public static final String ATTRIBUTE_DASHBOARDS_FOLDER =
			"mom.webapp.storage.dashboards";
	public static final String ATTRIBUTE_SERVER_HOME_FOLDER =
			"mom.webapp.storage.servers";
	public static final String ATTRIBUTE_PROFILES_FOLDER =
			"mom.webapp.storage.profiles";
	public static final String ATTRIBUTE_FIXPACKS_FOLDER =
			"mom.webapp.storage.fixpacks";
	
	void register(ExecutionContext ctx, String id);
	void unregister(String id);
	ExecutionContext getContext(String id);
	<T> T getAttribute(Class<T> c);
	<T, A extends T> void setAttribute(Class<T> c, A attribute);
	void setAttribute(Object attribute);
	<T> T getAttribute(String name);
	void setAttribute(String name, Object attribute);
	void removeAttribute(String name);
	String getContextPath();
	void log(Level level, String message);
	void log(Level level, String message, Throwable throwable);
	void log(Level level, String msg, Object...params);
	
	File getStorageFolder();
	File getStorageSubFolder(String attribute, String folderName);
	File getStorageSubFolder(String attribute, String folderName, boolean delete);

}
