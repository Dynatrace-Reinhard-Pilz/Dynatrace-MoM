package com.dynatrace.onboarding.dashboards;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import org.w3c.dom.Document;

import com.dynatrace.utils.DomUtil;
import com.dynatrace.utils.Version;

public class LocalDashboard implements Dashboard {

	private static final String FILE_EXTENSION = ".dashboard.xml";

	private final File file;
	private final String key;
	private final Version version;
	
	public LocalDashboard(File file, String key) throws IOException {
		Objects.requireNonNull(file);
		Objects.requireNonNull(key);
		this.file = file;
		this.key = key;
		if (!file.getName().endsWith(FILE_EXTENSION)) {
			throw new IllegalArgumentException("This is not a Dashboard: " + file.getAbsolutePath());
		}
		Document document = DomUtil.build(file);
		version = DomUtil.extractVersion(document, file.getName());
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
	public String getKey() {
		return key;
	}
	
	public File getFile() {
		return file;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String name() {
		return file.getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String id() {
		String name = name();
		return name.substring(0, name.length() - FILE_EXTENSION.length());
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
		return file.lastModified();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Dashboard localize() throws IOException {
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DashboardTemplate asTemplate() throws IOException {
		return new LocalDashboardTemplate(file, getKey());
	}
	
}
