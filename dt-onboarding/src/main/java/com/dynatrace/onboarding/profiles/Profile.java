package com.dynatrace.onboarding.profiles;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import org.w3c.dom.Document;

import com.dynatrace.utils.DomUtil;
import com.dynatrace.utils.Version;

public class Profile {
	
	private static final String FILE_EXTENSION = ".profile.xml";

	private final File file;
	private final Version version;
	
	public Profile(File file) throws IOException {
		Objects.requireNonNull(file);
		this.file = file;
		if (!file.getName().endsWith(FILE_EXTENSION)) {
			throw new IllegalArgumentException("This is not a System Profile: " + file.getAbsolutePath());
		}
		Document document = DomUtil.build(file);
		version = DomUtil.extractVersion(document, file.getName());
	}
	
	public Version getVersion() {
		return version;
	}
	
	public File getFile() {
		return file;
	}
	
	public String getName() {
		String filename = file.getName();
		return filename.substring(0, filename.length() - FILE_EXTENSION.length());
	}
	
}
