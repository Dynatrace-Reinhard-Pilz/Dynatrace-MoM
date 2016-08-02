package com.dynatrace.mom.connector.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dynatrace.fastpacks.FastPackUpload;
import com.dynatrace.fastpacks.FastpackBuilder;
import com.dynatrace.http.Protocol;
import com.dynatrace.http.config.ConnectionConfig;
import com.dynatrace.http.config.Credentials;
import com.dynatrace.http.config.ServerConfig;
import com.dynatrace.utils.DefaultExecutionContext;
import com.dynatrace.utils.FileSource;
import com.dynatrace.utils.Logging;
import com.dynatrace.utils.Version;
import com.dynatrace.utils.jar.Jars;


public class ConnectorClientMain {
	
	private static final Logger LOGGER =
			Logger.getLogger(ConnectorClientMain.class.getName());
	
	private static final String USER = "admin";
	private static final String PASS = "admin";

	public static void main(String[] args) {
		Logging.init();
		Credentials credentials = new Credentials(USER, PASS);
		ConnectionConfig connectionConfig =
				new ConnectionConfig(Protocol.HTTPS, "localhost", 8021);
		ServerConfig serverConfig =
				new ServerConfig(connectionConfig, credentials);
		ConnectorClient client = new ConnectorClient(serverConfig);
		Version remoteVersion = null;
		File connectorPluginFile = Jars.extractResource(ConnectorClientMain.class.getClassLoader(), "dt-mom-connector-client", "dt-mom-connector");
		if (connectorPluginFile == null) {
			LOGGER.log(Level.SEVERE, "Unable to extract the MoM Connector Plugin");
			return;
		}
		String sBundleVersion = Jars.getManifestAttribute(connectorPluginFile, "Bundle-Version");
		if (sBundleVersion == null) {
			LOGGER.log(Level.SEVERE, "Unable to determine Bundle Version of local MoM Connector Plugin");
			return;
		}
		Version bundleVersion = null;
		try {
			bundleVersion = Version.parse(sBundleVersion);
		} catch (IllegalArgumentException e) {
			LOGGER.log(Level.SEVERE, "Bundle Version '" + sBundleVersion + "' of local MoM Connector Plugin is invalid");
			return;
		}
		
		try {
			remoteVersion = client.getVersion();
			boolean needsFastPack = false;
			if (Version.UNDEFINED.equals(remoteVersion)) {
				LOGGER.log(Level.INFO, "MoM Connector Plugin not installed on the dynaTrace Server - installing it");
				needsFastPack = true;
			} else if (bundleVersion.compareTo(remoteVersion, true) > 0) {
				LOGGER.log(Level.INFO, "MoM Connector Plugin installed on the dynaTrace Server (Version " + remoteVersion + ") is outdated - updating");
				needsFastPack = true;
			}
			LOGGER.log(Level.INFO, "Local Version: " + bundleVersion);
			LOGGER.log(Level.INFO, "Remote Version: " + remoteVersion);
			if (needsFastPack) {
				String fastPackId = UUID.randomUUID().toString();
				FastpackBuilder fastPack = new FastpackBuilder(fastPackId, fastPackId);
				if (connectorPluginFile != null) {
					fastPack.addUserPlugin(FileSource.create(connectorPluginFile));
					final File tmpFile = File.createTempFile(ConnectorClient.class.getSimpleName(), ".tmp");
					tmpFile.deleteOnExit();
					try (OutputStream out = new FileOutputStream(tmpFile)) {
						fastPack.build(out);
					}
					FastPackUpload fastPackUpload = new FastPackUpload(new DefaultExecutionContext(), serverConfig) {
						@Override
						protected InputStream openStream() throws IOException {
							return new FileInputStream(tmpFile);
						}
					};
					
					if (fastPackUpload.execute()) {
						remoteVersion = client.getVersion();
					}
				}
			}
			System.out.println(remoteVersion);
		} catch (IOException e) {
			e.printStackTrace(System.err);
		}
	}

}
