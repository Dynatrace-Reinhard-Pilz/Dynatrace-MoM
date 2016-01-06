package com.dynatrace.classloader;

import java.net.URL;
import java.net.URLClassLoader;

public class EmbeddedClassLoader extends URLClassLoader {
	
	public EmbeddedClassLoader(URL[] urls, ClassLoader parent) {
		super(urls, parent);
	}

}
