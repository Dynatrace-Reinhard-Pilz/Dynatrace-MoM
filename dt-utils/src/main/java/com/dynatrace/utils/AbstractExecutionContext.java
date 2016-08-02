package com.dynatrace.utils;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 */
public abstract class AbstractExecutionContext implements ExecutionContext, ThreadFactory {
	
	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger(AbstractExecutionContext.class.getName());
	
	protected abstract Logger logger();
	
	@Override
	public void log(Level level, String message) {
		logger().log(level, message);
	}

	@Override
	public void log(Level level, String message, Throwable throwable) {
		logger().log(level, message, throwable);
	}

	@Override
	public void log(Level level, String msg, Object... params) {
		logger().log(level, msg, params);
	}
	
	
	@Override
	public <T> T getAttribute(Class<T> c) {
		return getAttribute(c.getName());
	}
	
	@Override
	public void setAttribute(Object attribute) {
		setAttribute(attribute.getClass().getName(), attribute);
	}
	
	@Override
	public <T, A extends T> void setAttribute(Class<T> c, A attribute) {
		setAttribute(c.getName(), attribute);
	}
	
	@Override
	public synchronized File getStorageFolder() {
		File storageFolder =
			getAttribute(ATTRIBUTE_STORAGE_FOLDER);
		if (storageFolder != null) {
//			LOGGER.info("AbstractExecutionContext.getStorageFolder() (1): " + storageFolder);
			return storageFolder;
		}
		
		final String webAppFolderName  = getContextPath();
		storageFolder = Storage.standard();
		final File webAppFolder = new File(storageFolder, webAppFolderName);
		webAppFolder.mkdirs();
		log(Level.INFO, "Storage Folder: " + Closeables.getAbsCanonPath(webAppFolder));
		setAttribute(
			ATTRIBUTE_STORAGE_FOLDER,
			webAppFolder
		);
//		LOGGER.info("AbstractExecutionContext.getStorageFolder() (2): " + storageFolder);
	    return webAppFolder;
	}
	
	@Override
	public File getStorageSubFolder(String attribute, String folderName, boolean delete) {
		File folder = getAttribute(attribute);
		if (folder != null) {
			return folder;
		}
		folder = new File(getStorageFolder(), folderName);
		Closeables.purge(folder);
		folder.mkdirs();
		setAttribute(attribute, folder);
		return folder;
	}
	
	@Override
	public synchronized File getStorageSubFolder(String attribute, String folderName) {
		return getStorageSubFolder(attribute, folderName, true);
	}
	
	@Override
	public Thread newThread(Runnable runnable) {
		final Thread thread = new Thread(
				runnable,
				"[" + ExecutionContext.class.getSimpleName() + "][" + runnable.getClass().getName() + "]"
		);
		thread.setDaemon(true);
		return thread;
	}
	
	@Override
	public ExecutionContext getContext(String id) {
		ExecutionContext ctx =  Unchecked.cast(
			getAttribute(ExecutionContext.class.getName() + "." + id));
		if (ctx == null) {
			return this;
		}
		return ctx;
	}
	
	@Override
	public void register(ExecutionContext ctx, String id) {
		Objects.requireNonNull(ctx);
		setAttribute(ExecutionContext.class.getName() + "." + id, ctx);
	}
	
	@Override
	public void unregister(String id) {
		removeAttribute(ExecutionContext.class.getName() + "." + id);
	}
	
}
