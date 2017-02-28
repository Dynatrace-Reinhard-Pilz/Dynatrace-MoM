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
	private final ServerConfig srvConf;
	
	public AbstractRemoteProfile(SystemProfileReference ref, ServerConfig srvConf) {
		Objects.requireNonNull(ref);
		Objects.requireNonNull(srvConf);
		this.reference = ref;
		this.srvConf = srvConf;
	}
	
	public final ServerConfig getServerConfig() {
		return srvConf;
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
		LOGGER.log(Level.FINER, "Localizing System Profile '" + id() + "'");
		File tempFolder = Config.temp();
		File hostFolder = new File(tempFolder, getServerConfig().getHost());
		File portFolder = new File(hostFolder, String.valueOf(getServerConfig().getPort()));
		portFolder.mkdirs();
		// LOGGER.log(Level.INFO, "TODO: ensure that temporary System Profile Name does not created troubles with file name");
		File profileFile = new File(portFolder, name());
		try (
			OutputStream out = new FileOutputStream(profileFile);
			InputStream in = openStream();
		) {
			Closeables.copy(in, out);
		}
		return createLocalizedInstance(profileFile, version());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String id() {
		return reference.getId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String name() {
		return id() + Profile.FILE_EXTENSION;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public InputStream openStream() throws IOException {
		LOGGER.log(Level.FINER, "Opening remote stream to '" + id() + "'");
		ConnectorClient client = new ConnectorClient(srvConf);
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
	public Version version() {
		return Version.parse(reference.getVersion());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return name();
	}

}
