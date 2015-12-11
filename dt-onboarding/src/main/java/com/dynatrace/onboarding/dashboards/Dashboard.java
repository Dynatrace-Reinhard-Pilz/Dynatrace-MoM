package com.dynatrace.onboarding.dashboards;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import org.w3c.dom.Document;

import com.dynatrace.utils.DomUtil;
import com.dynatrace.utils.Version;

public class Dashboard {

	private static final String FILE_EXTENSION = ".dashboard.xml";

	private final File file;
	private final String key;
	private final Version version;
	
	public Dashboard(File file, String key) throws IOException {
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
	
	public Version getVersion() {
		return version;
	}
	
	public String getKey() {
		return key;
	}
	
	public File getFile() {
		return file;
	}
	
	public String getName() {
		String filename = file.getName();
		return filename.substring(0, filename.length() - FILE_EXTENSION.length());
	}
	
}
