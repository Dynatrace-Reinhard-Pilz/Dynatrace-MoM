package com.dynatrace.fastpacks;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.dynatrace.fastpacks.metadata.resources.Resource;

public class FileSystemResourceResolver implements ResourceResolver {
	
	private final File folder;
	
	public FileSystemResourceResolver(File folder) {
		this.folder = folder;
	}

	@Override
	public final InputStream resolve(Resource resource) throws IOException {
		return new FileInputStream(resolveFile(resource));
	}
	
	private final File resolveFile(Resource resource) {
		if (!Resource.isValid(resource)) {
			throw new IllegalArgumentException("Invalid Resource " + resource);
		}
		return new File(folder, resource.getName());
	}

}
