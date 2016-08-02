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
import com.dynatrace.onboarding.variables.DefaultVariables;
import com.dynatrace.utils.Closeables;
import com.dynatrace.utils.Version;
import com.dynatrace.variables.UnresolvedVariableException;

public class RemoteDashboardTemplate implements DashboardTemplate {
	
	private static final Logger LOGGER =
			Logger.getLogger(RemoteDashboardTemplate.class.getName());
	
	private final DashboardReference reference;
	private final ServerConfig serverConfig;
	
	public RemoteDashboardTemplate(DashboardReference reference, ServerConfig serverConfig) {
		this.serverConfig = serverConfig;
		this.reference = reference;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Version getVersion() {
		return Version.parse(reference.getVersion());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getId() {
		return reference.getId();
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
		LOGGER.log(Level.FINER, "Opening remote stream to '" + getId() + "'");
		ConnectorClient client = new ConnectorClient(serverConfig);
		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			client.getDashboard(reference, out);
			return new ByteArrayInputStream(out.toByteArray());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long length() {
		return reference.getSize();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long lastModified() {
		return reference.getLastModified();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DashboardTemplate localize() throws IOException {
		LOGGER.log(Level.FINER, "Localizing Dashboard '" + getId() + "'");
		File tempFolder = Config.temp();
		File hostFolder = new File(tempFolder, serverConfig.getHost());
		File portFolder = new File(hostFolder, String.valueOf(serverConfig.getPort()));
		portFolder.mkdirs();
		LOGGER.log(Level.INFO, "TODO: ensure that temporary Dashboard Name does not created troubles with file name");
		File profileFile = new File(portFolder, getName());
		try (
			OutputStream out = new FileOutputStream(profileFile);
			InputStream in = openStream();
		) {
			Closeables.copy(in, out);
		}
		return new LocalDashboardTemplate(profileFile, getKey());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getKey() {
		return reference.getKey();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Dashboard resolve(DefaultVariables variables)
			throws IOException, UnresolvedVariableException
	{
		return localize().resolve(variables);
	}

}
