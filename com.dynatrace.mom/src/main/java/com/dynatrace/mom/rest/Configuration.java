package com.dynatrace.mom.rest;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import javax.servlet.ServletContext;

import com.dynatrace.mom.MomInit;
import com.dynatrace.mom.runtime.ServerRepository;
import com.dynatrace.utils.ExecutionContext;
import com.dynatrace.utils.Storage;

/**
 * 
 * @author Reinhard Pilz
 *
 */
public final class Configuration {
	
	@SuppressWarnings("unused")
	private static final String MSG_PERSISTING = "Persisting Server Repository to {0}";
	
	@SuppressWarnings("unused")
	private static final String ATT_TEMP_DIR = ServletContext.TEMPDIR;
	@SuppressWarnings("unused")
	private static final String ATT_CMDLINE = MomInit.class.getName();
	@SuppressWarnings("unused")
	private static final String ATT_REPO = ServerRepository.class.getName();
	
	/**
	 * 
	 * @param file
	 * @return
	 */
	private static final String getAbsCanonPath(final File file) {
		if (file == null) {
			return null;
		}
		try {
			return file.getCanonicalFile().getAbsolutePath();
		} catch (final IOException e) {
			return file.getAbsolutePath();
		}
	}
	
	public synchronized File getStorageFolder(ExecutionContext ctx) {
		final File registeredWebAppFolder = ctx.getAttribute("mom.webapp.storage.file");
		if (registeredWebAppFolder != null) {
			return registeredWebAppFolder;
		}
		String contextPath = ctx.getContextPath();
		if (contextPath.startsWith("_")) {
			contextPath = contextPath.substring(1);
		}
		final String webAppFolderName  = "webapp-" + contextPath;
		final File storageFolder = Storage.standard();
		final File webAppFolder = new File(storageFolder, webAppFolderName);
		webAppFolder.mkdirs();
		ctx.log(Level.INFO, "Storage Folder: " + getAbsCanonPath(webAppFolder));
		ctx.setAttribute("mom.webapp.storage", getAbsCanonPath(webAppFolder));
		ctx.setAttribute("mom.webapp.storage.file", webAppFolder);
	    return webAppFolder;
	}
	
	public synchronized File getFixPacksFolder(ExecutionContext ctx) {
		File fldFixPacks = ctx.getAttribute("mom.webapp.storage.fixpacks");
		if (fldFixPacks != null) {
			return fldFixPacks;
		}
		fldFixPacks = new File(getStorageFolder(ctx), "fixpacks");
		fldFixPacks.mkdirs();
		ctx.setAttribute("mom.webapp.storage.fixpacks", fldFixPacks);
		return fldFixPacks;
	}

	public synchronized File getProfilesFolder(ExecutionContext ctx) {
		File fld = ctx.getAttribute("mom.webapp.storage.profiles");
		if (fld != null) {
			return fld;
		}
		fld = new File(getStorageFolder(ctx), "profiles");
		fld.mkdirs();
		ctx.setAttribute("mom.webapp.storage.profiles", fld);
		return fld;
	}
	
	public synchronized File getServerHomeFolder(ExecutionContext ctx) {
		File fld = ctx.getAttribute(ContextConstants.FLD_SERVERS_HOME);
		if (fld != null) {
			return fld;
		}
		fld = new File(getStorageFolder(ctx), "servers");
		fld.mkdirs();
		ctx.setAttribute(ContextConstants.FLD_SERVERS_HOME, fld);
		return fld;
	}
	
	public synchronized File getDashboardsFolder(ExecutionContext ctx) {
		File fld = ctx.getAttribute("mom.webapp.storage.dashboards");
		if (fld != null) {
			return fld;
		}
		fld = new File(getStorageFolder(ctx), "dashboards");
		fld.mkdirs();
		ctx.setAttribute("mom.webapp.storage.dashboards", fld);
		return fld;
	}
	
	/**
	 * 
	 * @param ctx
	 * @return
	 */
	public final File getXmlFile(final ExecutionContext ctx) {
	    return new File(getStorageFolder(ctx), ServerRepository.STORAGE_FILE_NAME);
	}
	
}
