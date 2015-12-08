package com.dynatrace.fastpacks;

import java.io.IOException;
import java.io.InputStream;

import com.dynatrace.fastpacks.metadata.resources.Resource;

public interface ResourceResolver {
	
	InputStream resolve(Resource resource) throws IOException;
	
}
