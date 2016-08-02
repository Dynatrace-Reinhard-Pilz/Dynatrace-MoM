package com.dynatrace.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileSource implements Source<FileSource> {

	private final File file;
	
	public FileSource(File file) {
		this.file = file;
	}
	
	public FileSource(String name) {
		this.file = new File(name);
	}
	
	public static FileSource create(File file) {
		return new FileSource(file);
	}
	
	public static FileSource create(String name) {
		return new FileSource(name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getId() {
		return file.getName();
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
	public FileSource localize() throws IOException {
		return this;
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
	public String getName() {
		return getId();
	}
}
