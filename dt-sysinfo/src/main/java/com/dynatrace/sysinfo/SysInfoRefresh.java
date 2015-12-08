package com.dynatrace.sysinfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.dynatrace.http.ServerOperation;
import com.dynatrace.http.config.ServerConfig;
import com.dynatrace.http.request.Request;
import com.dynatrace.profiles.SystemProfile;
import com.dynatrace.utils.Closeables;
import com.dynatrace.utils.ExecutionContext;
import com.dynatrace.utils.Strings;
import com.dynatrace.utils.Version;

public class SysInfoRefresh extends ServerOperation<SysInfoResult> {
	
	private static final Logger LOGGER =
			Logger.getLogger(SysInfoRefresh.class.getName());
	
	private final String[] DEFAULT_FILE_TYPES = new String[] {
			"configfiles",
			"dashboards",
			"profiles",
			"agentrecords",
			"licensefile",
			"componentproperties"	
		};
	
	public SysInfoRefresh(ExecutionContext ctx, ServerConfig scfg) {
		super(ctx, scfg);
	}
	
	public void onSystemProfile(SystemProfile systemProfile) {
		// subclasses may override
	}

	private boolean extractProfile(
		File targetFolder,
		String name,
		InputStream in,
		SysInfoResult sysInfoResult
	) {
		String profileName = SystemProfile.getProfileName(name);
		if (profileName == null) {
			return false;
		}
		File localFile = new File(
			targetFolder,
			name.substring(name.lastIndexOf('/') + 1)
		);
		try (OutputStream fos = new FileOutputStream(localFile)) {
			Closeables.copy(in, fos);
			SystemProfile systemProfile = new SystemProfile(localFile);
			sysInfoResult.addProfile(systemProfile);
			onSystemProfile(systemProfile);
		} catch (IOException e) {
			LOGGER.log(
				Level.WARNING,
				"Unable to exctract zip entry '" + name + "' to '" +
				targetFolder.getAbsolutePath() + "' "
			);
		}
		return true;
	}

	@Override
	protected void handleResult(SysInfoResult sysInfoResult) {
		if (Thread.currentThread().isInterrupted()) {
			return;
		}
		if (sysInfoResult == null) {
			return;
		}
		File sysInfoZipFile = sysInfoResult.getFile();
		if (sysInfoZipFile == null) {
			return;
		}
		File fldProfiles = getStorageSubFolder(
			ExecutionContext.ATTRIBUTE_PROFILES_FOLDER,
			"profiles"
		);
//		File fldDashboards = ctx.getStorageSubFolder(
//			ExecutionContext.ATTRIBUTE_DASHBOARDS_FOLDER,
//			"dashboards"
//		);
		try (
			FileInputStream fis = new FileInputStream(sysInfoZipFile);
			ZipInputStream zis = new ZipInputStream(fis);
		) {
			HashMap<ComponentHashKey, ComponentProperties> props =
					new HashMap<ComponentHashKey, ComponentProperties>();
			ZipEntry zipEntry = zis.getNextEntry();
			while (zipEntry != null) {
				String entryName = zipEntry.getName();
				// unable to figure out what component this entry belongs to ==> ignore it
				ComponentProperties componentProperties =
						ComponentProperties.resolveProperties(props, entryName);
				if (componentProperties == null) {
					LOGGER.log(
						Level.FINEST,
						"Ignoring entry '" + zipEntry.getName() +
						"' because no component identified for it");
					zipEntry = zis.getNextEntry();
					continue;
				}
				if (entryName.endsWith(".profile.xml")) {
					if (componentProperties.getType() == ComponentType.server) {
						extractProfile(
							fldProfiles,
							zipEntry.getName(),
							zis,
							sysInfoResult
						);
					}
//				} else if (entryName.endsWith(".dashboard.xml")) {
//					if (componentProperties.getType() == ComponentType.server) {
//						extractZipEntry(
//							serverRecord,
//							fldDashboards,
//							zipEntry.getName(),
//							zis
//						);
//					}
				} else if (entryName.endsWith("component.properties")) {
					ComponentProperties.handleComponentProperties(
						componentProperties,
						zipEntry,
						zis
					);
				} else if (entryName.endsWith("collector.config.xml")) {
					ComponentProperties.handleCollectorConfigXml(
						componentProperties,
						zis
					);
				} else if (entryName.endsWith("server.config.xml")) {
					ComponentProperties.handleServerConfigXml(
						componentProperties,
						zis
					);
				} else {
					LOGGER.log(Level.FINE, "zip entry '" + zipEntry.getName() + "' has not been handled. Please check if this file contains valuable information.");
				}
				zipEntry = zis.getNextEntry();
			}
			for (ComponentHashKey chk : props.keySet()) {
				ComponentProperties componentProperties = props.get(chk);
				if (componentProperties.getType() == ComponentType.collector) {
					evalCollectorProperties(componentProperties);
				} else if (componentProperties.getType() == ComponentType.server) {
					evalServerProperties(componentProperties);
				}
			}
			sysInfoZipFile.delete();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void evalServerProperties(ComponentProperties properties) {
		if (properties.getType() != ComponentType.server) {
			return;
		}
		String name = properties.getName();
		if (name != null) {
			onServerFQDN(name);
		}
	}
	
	public void evalCollectorProperties(ComponentProperties properties) {
		if (properties.getType() != ComponentType.collector) {
			return;
		}
		String name = properties.getName();
		String host = properties.getHost();
		if (Strings.isNotEmpty(name)) {
			Version version = properties.getVersion();
			if (Version.isValid(version)) {
				onCollectorVersion(name, host, version);
			}
			onCollectorStartupTime(name, host, properties.getStartupTime());
		}
	}
	
	public void onServerFQDN(String fqdn) {
		// subclasses may override
	}
	
	public void onCollectorStartupTime(String name, String host, long time) {
		// subclasses may override
	}
	
	public void onCollectorVersion(String name, String host, Version version) {
		// subclasses may override
	}
	
	protected String[] getSupportedFileTypes() {
		return DEFAULT_FILE_TYPES;
	}
	
	@Override
	public Request<SysInfoResult> createRequest() {
		return new SysInfoRequest() {
			@Override
			protected String[] getSupportedFileTypes() {
				return SysInfoRefresh.this.getSupportedFileTypes();
			}
		};
	}
	
	@Override
	protected Logger logger() {
		return LOGGER;
	}

}
