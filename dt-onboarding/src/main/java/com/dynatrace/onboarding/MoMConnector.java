package com.dynatrace.onboarding;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dynatrace.fastpacks.FastPackUpload;
import com.dynatrace.fastpacks.FastpackBuilder;
import com.dynatrace.http.Http;
import com.dynatrace.http.HttpResponse;
import com.dynatrace.http.Method;
import com.dynatrace.http.config.ServerConfig;
import com.dynatrace.http.permissions.PermissionDeniedException;
import com.dynatrace.mom.connector.client.ConnectorClient;
import com.dynatrace.mom.connector.client.ConnectorClientMain;
import com.dynatrace.onboarding.fastpacks.AbstractPluginPeer;
import com.dynatrace.utils.DefaultExecutionContext;
import com.dynatrace.utils.FileSource;
import com.dynatrace.utils.Unchecked;
import com.dynatrace.utils.Version;
import com.dynatrace.utils.jar.Jars;

public class MoMConnector extends AbstractPluginPeer {
	
	private static final Logger LOGGER =
			Logger.getLogger(MoMConnector.class.getName());
	
	private final ServerConfig serverConfig;
	
	public MoMConnector(ServerConfig srvConf) {
		this.serverConfig = srvConf;
	}
	
	@Override
	public boolean install() {
		System.out.println("installing");
		ConnectorClient client = new ConnectorClient(serverConfig);
		Version remoteVersion = null;
		File pluginFile = Jars.extractResource(
			ConnectorClientMain.class.getClassLoader(),
			"dt-mom-connector-client",
			"dt-mom-connector"
		);
		if (pluginFile == null) {
			LOGGER.log(
				Level.SEVERE,
				"Unable to extract the MoM Connector Plugin"
			);
			return false;
		}
		Version bundleVersion = Jars.getBundleVersion(pluginFile);
		try {
			remoteVersion = client.getVersion();
			boolean needsFastPack = false;
			if (Version.UNDEFINED.equals(remoteVersion)) {
				LOGGER.log(
					Level.INFO,
					"MoM Connector Plugin not installed on the dynaTrace Server - installing it"
				);
				needsFastPack = true;
			} else if (bundleVersion.compareTo(remoteVersion, true) > 0) {
				LOGGER.log(
					Level.INFO,
					"MoM Connector Plugin installed on the dynaTrace Server (Version " + remoteVersion + ") is outdated - updating"
				);
				needsFastPack = true;
			}
			if (!needsFastPack) {
				return true;
			}
			String fastPackId = UUID.randomUUID().toString();
			FastpackBuilder fastPack = new FastpackBuilder(
				fastPackId,
				fastPackId
			);
			if (pluginFile != null) {
				fastPack.addUserPlugin(FileSource.create(pluginFile));
				final File tmpFile = createTempFile();
				tmpFile.deleteOnExit();
				try (OutputStream out = new FileOutputStream(tmpFile)) {
					fastPack.build(out);
				}
				FastPackUpload fpUpload = new FastPackUpload(
					new DefaultExecutionContext(), serverConfig
				) {
					@Override
					protected InputStream openStream() throws IOException {
						return new FileInputStream(tmpFile);
					}
				};
				
				if (fpUpload.execute()) {
					remoteVersion = client.getVersion();
				}
			}
			System.out.println(remoteVersion);
		} catch (IOException e) {
			e.printStackTrace(System.err);
		}
		return true;
	}
	
	private static File createTempFile() throws IOException {
		return File.createTempFile(
			ConnectorClient.class.getSimpleName(),
			".tmp"
		);		
	}

	@Override
	public boolean isInstalled() {
		URL url = null;
		try {
			url = serverConfig.getConnectionConfig().createURL("/mom/version");
		} catch (MalformedURLException e) {
			LOGGER.log(
				Level.WARNING,
				"Unable to create URL for version check of MoMConnector",
				e
			);
		}
		HttpResponse<Version> response = null;
		try {
			response = Http.client().request(
				url,
				Method.GET,
				serverConfig.getCredentials(),
				Version.class
			);
		} catch (IOException e) {
			LOGGER.log(
				Level.INFO,
				"Unable to check for installation status of MoMConnector",
				e
			);
			return false;
		}
		Throwable exception = response.getException();
		if (exception instanceof PermissionDeniedException) {
			PermissionDeniedException pde = Unchecked.cast(exception);
			String permission = pde.getPermission();
			if (permission != null) {
				LOGGER.log(Level.SEVERE, "Missing Permission: " + permission);
				return false;
			}
		}
		boolean equals = new Version(1,0,0,0).equals(response.getData());
		System.out.println(("isInstalled: " + equals));
		return equals;
	}
	
}
