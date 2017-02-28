package com.dynatrace.onboarding.profiles;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import com.dynatrace.utils.Version;

public class LocalProfile implements Profile {
	
	private static final String FILE_EXTENSION = ".profile.xml";
	
	private final File file;
	private final Version version;
	
	public LocalProfile(File file, Version version) {
		Objects.requireNonNull(file);
		Objects.requireNonNull(version);
		this.file = file;
		this.version = version;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String id() {
		String filename = file.getName();
		return filename.substring(0, filename.length() - FILE_EXTENSION.length());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Version version() {
		return version;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public InputStream openStream() throws IOException {
		return new FileInputStream(file);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long length() {
		return file.length();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long lastModified() {
		return file.length();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Profile localize() throws IOException {
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProfileTemplate asTemplate() throws IOException {
		return new LocalProfileTemplate(file);
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
	public String toString() {
		return name();
	}

}
