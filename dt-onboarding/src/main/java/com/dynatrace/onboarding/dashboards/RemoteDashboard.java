package com.dynatrace.onboarding.dashboards;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dynatrace.http.config.ServerConfig;
import com.dynatrace.mom.connector.client.ConnectorClient;
import com.dynatrace.mom.connector.model.dashboards.DashboardReference;
import com.dynatrace.onboarding.config.Config;
import com.dynatrace.utils.Closeables;
import com.dynatrace.utils.Version;

public class RemoteDashboard implements Dashboard {
	
	private static final Logger LOGGER =
			Logger.getLogger(RemoteDashboard.class.getName());
	
	private final DashboardReference xmlProfile;
	private final ServerConfig serverConfig;
	
	public RemoteDashboard(DashboardReference xmlProfile, ServerConfig serverConfig) {
		this.xmlProfile = xmlProfile;
		this.serverConfig = serverConfig;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DashboardTemplate asTemplate() throws IOException {
		return new RemoteDashboardTemplate(xmlProfile, serverConfig);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Version getVersion() {
		return Version.parse(xmlProfile.getVersion());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getId() {
		return xmlProfile.getId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return getId() + Dashboard.FILE_EXTENSION;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public InputStream openStream() throws IOException {
		LOGGER.log(Level.INFO, "Opening remote stream to '" + getId() + "'");
		ConnectorClient client = new ConnectorClient(serverConfig);
		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			client.getDashboard(xmlProfile, out);
			return new ByteArrayInputStream(out.toByteArray());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long length() {
		return xmlProfile.getSize();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long lastModified() {
		return xmlProfile.getLastModified();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Dashboard localize() throws IOException {
		LOGGER.log(Level.INFO, "Localizing Dashboard '" + getId() + "'");
		File tempFolder = Config.temp();
		File hostFolder = new File(tempFolder, serverConfig.getHost());
		File portFolder = new File(hostFolder, String.valueOf(serverConfig.getPort()));
		portFolder.mkdirs();
		LOGGER.log(Level.INFO, "TODO: ensure that temporary System Profile Name does not created troubles with file name");
		File profileFile = new File(portFolder, getName());
		try (
			OutputStream out = new FileOutputStream(profileFile);
			InputStream in = openStream();
		) {
			Closeables.copy(in, out);
		}
		return new LocalDashboard(profileFile, getKey());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getKey() {
		return xmlProfile.getKey();
	}

}
