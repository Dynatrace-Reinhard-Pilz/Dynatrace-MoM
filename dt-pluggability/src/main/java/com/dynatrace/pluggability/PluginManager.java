package com.dynatrace.pluggability;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import com.dynatrace.utils.Closeables;
import com.dynatrace.utils.Iterables;
import com.dynatrace.utils.TempFiles;
import com.dynatrace.utils.Unchecked;

public class PluginManager extends ClassLoader {
	
	static final File TEMP = TempFiles.getTempFolder(".dt-pluggability");
	
	private static final PluginManager NULL = new PluginManager();
	
	private static final Map<ProtectionDomain, PluginManager> MANAGERS =
			new HashMap<>();
	
	private final PluginClassLoader[] classLoaders;
	
	private PluginManager() {
		this.classLoaders =	new PluginClassLoader[0];
	}
	
	public PluginManager(ProtectionDomain protectionDomain) throws IOException {
		Objects.requireNonNull(protectionDomain);
		this.classLoaders =	getPluginClassLoaders(protectionDomain);
	}
	
	public static PluginManager get(Class<?> clazz) {
		Objects.requireNonNull(clazz);
		ProtectionDomain protectionDomain = clazz.getProtectionDomain();
		PluginManager manager = MANAGERS.get(protectionDomain);
		if (manager != null) {
			return manager;
		}
		try {
			manager = new PluginManager(protectionDomain);
		} catch (IOException e) {
			return NULL;
		}
		MANAGERS.put(protectionDomain, manager);
		return manager;
	}
	
	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		for (PluginClassLoader classLoader : classLoaders) {
			try {
				return classLoader.findClass(name);
			} catch (ClassNotFoundException e) {
				// ignore
			}
		}
		return super.findClass(name);
	}
	
	public <T> Class<? extends T>[] getImplementors(Class<T> clazz) {
		final Collection<Class<? extends T>> classes = new ArrayList<>();
		for (PluginClassLoader classLoader : classLoaders) {
			Class<? extends T>[] implementors = classLoader.getImplementors(clazz);
			if ((implementors == null) || (implementors.length == 0)) {
				continue;
			}
			for (Class<? extends T> implementor : implementors) {
				if (implementor == null) {
					continue;
				}
				classes.add(implementor);
			}
		}
		Class<? extends T>[] classArray =
				Unchecked.cast(new Class<?>[classes.size()]);
		return classes.toArray(classArray);
	}	

	private static PluginClassLoader[] getPluginClassLoaders(ProtectionDomain protectionDomain) throws IOException {
		Objects.requireNonNull(protectionDomain);
		final URL[] urls = resolveURLs(protectionDomain);
		if (Iterables.isNullOrEmpty(urls)) {
			return new PluginClassLoader[0];
		}
		return getPluginClassLoaders(protectionDomain.getClassLoader(), urls);
	}
	
	static PluginClassLoader[] getPluginClassLoaders(ClassLoader parent, URL[] urls) throws IOException {
		if (Iterables.isNullOrEmpty(urls)) {
			return new PluginClassLoader[0];
		}
		final Collection<PluginClassLoader> classLoaders = new ArrayList<>();
		for (URL url : urls) {
			if (url == null) {
				continue;
			}
			classLoaders.add(new PluginClassLoader(parent, url));
		}
		return classLoaders.toArray(new PluginClassLoader[classLoaders.size()]);
	}
	
	private static URL[] resolveURLs(ProtectionDomain protectionDomain) throws IOException {
		try {
			return resolveURLs(protectionDomain.getCodeSource().getLocation().toURI());
		} catch (URISyntaxException e) {
			throw new InternalError(e.getMessage());
		}
	}
	
	static URL[] resolveURLs(URI uri) throws IOException {
		File pluginFolder = Files.createTempDirectory(TEMP.toPath(), "plugins-").toFile();
		pluginFolder.deleteOnExit();
		File jarFile = new File(uri.getPath());
		Collection<URL> urls = new ArrayList<>();
		try (
			InputStream fis = new FileInputStream(jarFile);
			JarInputStream in = new JarInputStream(fis)
		) {
			JarEntry entry = null;
			while ((entry = in.getNextJarEntry()) != null) {
				String entryName = entry.getName();
				if (!entryName.endsWith(".jar")) {
					continue;
				}
				String jarName = null;
				if (entryName.startsWith("plugins/")) {
					jarName = entryName.substring("plugins/".length());
				} else if (entryName.startsWith("lib/")) {
					jarName = entryName.substring("lib/".length());
				} else if (entryName.startsWith("variables/")) {
					jarName = entryName.substring("variables/".length());
				} else if (entryName.startsWith("authentication/")) {
					jarName = entryName.substring("authentication/".length());
				}
				if (jarName == null) {
					continue;
				}
				if (jarName.indexOf('/') >= 0) {
					continue;
				}
				File pluginJarFile = new File(pluginFolder, jarName);
				pluginJarFile.deleteOnExit();
				try (OutputStream out = new FileOutputStream(pluginJarFile)) {
					Closeables.copy(in, out);
				}
				urls.add(pluginJarFile.toURI().toURL());
			}
			return urls.toArray(new URL[urls.size()]);
		} catch (FileNotFoundException e) {
			throw new InternalError(e.getMessage());
		} catch (IOException e) {
			throw new InternalError(e.getMessage());
		}
	}
	
}
