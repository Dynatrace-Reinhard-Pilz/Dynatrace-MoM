package com.dynatrace.onboarding.profiles;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dynatrace.http.config.ServerConfig;
import com.dynatrace.mom.connector.client.ConnectorClient;
import com.dynatrace.mom.connector.model.profiles.SystemProfileReference;
import com.dynatrace.onboarding.config.Config;
import com.dynatrace.utils.Closeables;
import com.dynatrace.utils.Version;
import com.dynatrace.utils.VersionedSource;

public abstract class AbstractRemoteProfile<T extends VersionedSource<T>> implements VersionedSource<T> {
	
	private static final Logger LOGGER =
			Logger.getLogger(AbstractRemoteProfile.class.getName());
	
	private final SystemProfileReference reference;
	private final ServerConfig serverConfig;
	
	public AbstractRemoteProfile(SystemProfileReference reference, ServerConfig serverConfig) {
		Objects.requireNonNull(reference);
		Objects.requireNonNull(serverConfig);
		this.reference = reference;
		this.serverConfig = serverConfig;
	}
	
	public final ServerConfig getServerConfig() {
		return serverConfig;
	}
	
	public final SystemProfileReference getReference() {
		return reference;
	}
	
	public abstract T createLocalizedInstance(File file, Version version) throws IOException;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public T localize() throws IOException {
		LOGGER.log(Level.FINER, "Localizing System Profile '" + getId() + "'");
		File tempFolder = Config.temp();
		File hostFolder = new File(tempFolder, getServerConfig().getHost());
		File portFolder = new File(hostFolder, String.valueOf(getServerConfig().getPort()));
		portFolder.mkdirs();
		LOGGER.log(Level.INFO, "TODO: ensure that temporary System Profile Name does not created troubles with file name");
		File profileFile = new File(portFolder, getName());
		try (
			OutputStream out = new FileOutputStream(profileFile);
			InputStream in = openStream();
		) {
			Closeables.copy(in, out);
		}
		return createLocalizedInstance(profileFile, getVersion());
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
		return getId() + Profile.FILE_EXTENSION;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public InputStream openStream() throws IOException {
		LOGGER.log(Level.FINER, "Opening remote stream to '" + getId() + "'");
		ConnectorClient client = new ConnectorClient(serverConfig);
		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			client.getProfile(reference, out);
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
	public Version getVersion() {
		return Version.parse(reference.getVersion());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return getName();
	}

}
