package com.dynatrace.mom.runtime;

import java.io.InputStream;

public abstract class ResourceLoader {
	
	private static ResourceLoader loader = new DefaultResourceLoader();
	
	public static void setDefault(final ResourceLoader loader) {
		synchronized (ResourceLoader.class) {
			ResourceLoader.loader = loader;
		}
	}
	
	public static ResourceLoader getDefault() {
		synchronized (ResourceLoader.class) {
			return ResourceLoader.loader;
		}
	}

	public abstract InputStream getResourceAsStream(String name);
	
	private static class DefaultResourceLoader extends ResourceLoader {

		@Override
		public InputStream getResourceAsStream(String name) {
			return this.getClass().getClassLoader().getResourceAsStream(name);
		}
		
	}
}
