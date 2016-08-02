package com.dynatrace.onboarding.fastpacks;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dynatrace.fastpacks.FastPackInstallStatus;
import com.dynatrace.fastpacks.FastPackUpload;
import com.dynatrace.fastpacks.FastpackBuilder;
import com.dynatrace.http.config.ServerConfig;
import com.dynatrace.http.permissions.PermissionDeniedException;
import com.dynatrace.onboarding.serverconfig.ServerProperties;
import com.dynatrace.utils.DefaultExecutionContext;
import com.dynatrace.utils.FileSource;
import com.dynatrace.utils.Version;
import com.dynatrace.utils.jar.Jars;

public class OnBoardingTaskVerifier implements InstallationVerifier {
	
	private static final Logger LOGGER =
			Logger.getLogger(OnBoardingTaskVerifier.class.getName());
	
	private final ServerConfig serverConfig;
	
	public OnBoardingTaskVerifier(ServerConfig serverConfig) {
		this.serverConfig = serverConfig;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isInstalled() {
		return !Version.UNDEFINED.equals(getInstalledVersion());
	}
	
	public Version getInstalledVersion() {
		ServerProperties serverProps = ServerProperties.load(serverConfig, false);
		return serverProps.serverConfigXml.getPermissionTaskVersion();
	}
	
	public Version getLocalVersion(File taskPluginFile) {
		if (taskPluginFile == null) {
			LOGGER.log(Level.SEVERE, "Unable to extract the MoM Connector Plugin");
			return Version.UNDEFINED;
		}
		return Jars.getBundleVersion(taskPluginFile);
	}

	public boolean install() {
		Version remoteVersion = getInstalledVersion();
		File taskPluginFile = Jars.extractResource(OnBoardingTaskVerifier.class.getClassLoader(), "dt-onboarding", "dt-ensure-user-group-task");
		Version localVersion = getLocalVersion(taskPluginFile);
		try {
			boolean needsFastPack = false;
			if (Version.UNDEFINED.equals(remoteVersion)) {
				LOGGER.log(Level.INFO, "Onboarding Task not installed on the dynaTrace Server - installing it");
				needsFastPack = true;
			} else if (localVersion.compareTo(remoteVersion, true) > 0) {
				LOGGER.log(Level.INFO, "Onboarding Task installed on the dynaTrace Server (Version " + remoteVersion + ") is outdated - updating");
				needsFastPack = true;
			}
			if (!needsFastPack) {
				return true;
			}
			String fastPackId = UUID.randomUUID().toString();
			FastpackBuilder fastPack = new FastpackBuilder(fastPackId, fastPackId);
			if (taskPluginFile != null) {
				fastPack.addUserPlugin(FileSource.create(taskPluginFile));
				final File tmpFile = File.createTempFile(OnBoardingTaskVerifier.class.getSimpleName(), ".tmp");
				tmpFile.deleteOnExit();
				try (OutputStream out = new FileOutputStream(tmpFile)) {
					fastPack.build(out);
				}
				FastPackUpload fastPackUpload = new FastPackUpload(new DefaultExecutionContext(), serverConfig) {
					@Override
					protected InputStream openStream() throws IOException {
						return new FileInputStream(tmpFile);
					}
					
					@Override
					public void onPermissionDenied(PermissionDeniedException ex) {
						if (ex == null) {
							return;
						}
						String permission = ex.getPermission();
						if (permission != null) {
							LOGGER.log(
								Level.SEVERE,
								"Missing Permission: " + permission
							);
						}
					}
					
					@Override
					public FastPackInstallStatus handlePermissionDeniedException(
						PermissionDeniedException e
					) {
						String permission = e.getPermission();
						if (!"Administrative Permission".equals(permission)) {
							return super.handlePermissionDeniedException(e);
						}
						
						if (isInstalled()) {
							return new FastPackInstallStatus();
						}
						return null;
					}
				};
				return fastPackUpload.execute();
			}
		} catch (IOException e) {
			e.printStackTrace(System.err);
			return false;
		}
		return true;
	}		

}
