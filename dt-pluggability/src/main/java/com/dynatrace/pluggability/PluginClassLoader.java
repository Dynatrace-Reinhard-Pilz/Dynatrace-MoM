package com.dynatrace.pluggability;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dynatrace.utils.Closeables;
import com.dynatrace.utils.Iterables;
import com.dynatrace.utils.Strings;
import com.dynatrace.utils.Unchecked;

final class PluginClassLoader extends URLClassLoader {
	
	private static final Logger LOGGER =
			Logger.getLogger(PluginClassLoader.class.getName());
	
	private final PluginClassLoader[] classLoaders;
	
	
	PluginClassLoader(ClassLoader parent, URL url) throws IOException {
		super(new URL[] { url }, parent);
		Collection<PluginClassLoader> classPathClassLoaders = new ArrayList<>();
		try {
			URI uri = url.toURI();
			File file = new File(uri);
			try (JarFile jarFile = new JarFile(file)) {
				Manifest manifest = jarFile.getManifest();
				Attributes attributes = manifest.getMainAttributes();
				String bundleClassPath = attributes.getValue("Bundle-ClassPath");
				if (Strings.isNotEmpty(bundleClassPath)) {
					StringTokenizer strTok = new StringTokenizer(bundleClassPath, ",");
					while (strTok.hasMoreTokens()) {
						String classPathEntry = strTok.nextToken().trim();
						if (Strings.isNotEmpty(classPathEntry) && classPathEntry.endsWith(".jar")) {
							File pluginFolder = Files.createTempDirectory(PluginManager.TEMP.toPath(), "plugins-").toFile();
							pluginFolder.deleteOnExit();
							File extractedFile = new File(pluginFolder, getFileName(classPathEntry));
							try (
								InputStream in = super.getResourceAsStream(classPathEntry);
								OutputStream out = new FileOutputStream(extractedFile)
							) {
								Closeables.copy(in, out);
							}
							classPathClassLoaders.add(
								new PluginClassLoader(
									this,
									extractedFile.toURI().toURL()
								)
							);
						}
					}
				}
			}
		} catch (URISyntaxException e) {
			throw new InternalError(e.getMessage());
		}
		this.classLoaders = classPathClassLoaders.toArray(
			new PluginClassLoader[classPathClassLoaders.size()]
		);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public URL findResource(String name) {
		if (LOGGER.isLoggable(Level.FINEST))
			LOGGER.log(Level.FINEST, "findResource [" + this + "] (" +
				name + ")");
		return super.findResource(name);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Class<?> loadClass(String name, boolean resolve)
		throws ClassNotFoundException
	{
		if (LOGGER.isLoggable(Level.FINEST))
			LOGGER.log(Level.FINEST, "loadClass [" + this + "] (" +
				name + ", " + resolve + ")");
		return super.loadClass(name, resolve);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		if (LOGGER.isLoggable(Level.FINEST))
			LOGGER.log(Level.FINEST, "loadClass [" + this + "] (" + name + ")");
		return super.loadClass(name);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		if (LOGGER.isLoggable(Level.FINEST))
			LOGGER.log(Level.FINEST, "findClass [" + this + "] (" +
				name + ")");
		Class<?> loadedClass = findLoadedClass(name);
		if (loadedClass != null) {
			return loadedClass;
		}
		try {
			return super.findClass(name);
		} catch (ClassNotFoundException e) {
			for (PluginClassLoader classLoader : classLoaders) {
				try {
					return classLoader.findClass(name);
				} catch (ClassNotFoundException e2) {
					// ignore
				}
			}
			throw new ClassNotFoundException(name);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Enumeration<URL> findResources(String name) throws IOException {
		if (LOGGER.isLoggable(Level.FINEST))
			LOGGER.log(Level.FINEST, "findResources [" + this + "] (" +
				name + ")");
		Collection<URL> resources = new HashSet<>();
		Iterables.addAll(resources, super.findResources(name));
		for (PluginClassLoader classLoader : classLoaders) {
			Iterables.addAll(resources, classLoader.findResources(name));
		}
		return Collections.enumeration(resources);
	}
	
	@SuppressWarnings("unchecked")
	<T> Class<? extends T>[] getImplementors(Class<T> clazz) {
		final Collection<Class<? extends T>> classes = new ArrayList<>();
		URL[] urls = getURLs();
		for (URL url : urls) {
			if (url == null) {
				continue;
			}
			Class<? extends T>[] implementors = getImplementors(clazz, url);
			if (Iterables.isNullOrEmpty(implementors)) {
				continue;
			}
			for (Class<? extends T> implementor : implementors) {
				if (implementor == null) {
					continue;
				}
				classes.add(implementor);
			}
		}
		for (PluginClassLoader classLoader : classLoaders) {
			Iterables.addAll(classes, classLoader.getImplementors(clazz));
		}
		return classes.toArray(new Class[classes.size()]);
	}
	
	@SuppressWarnings("unchecked")
	<T> Class<? extends T>[] getImplementors(Class<T> clazz, URL url) {
		Objects.requireNonNull(clazz);
		Objects.requireNonNull(url);
		if (LOGGER.isLoggable(Level.FINEST))
			LOGGER.log(
				Level.FINEST,
				"Searching for implementing classes of " +
				clazz.getSimpleName() + " within " + url
			);
		final Collection<Class<? extends T>> classes = new ArrayList<>();
		try (
			InputStream uis = url.openStream();
			JarInputStream in = new JarInputStream(uis)
		) {
			JarEntry entry = null;
			while ((entry = in.getNextJarEntry()) != null) {
				String entryName = entry.getName();
				if (!entryName.endsWith(".class")) {
					continue;
				}
				entryName = entryName.replace('/', '.');
				entryName = entryName.substring(
					0,
					entryName.length() - ".class".length()
				);
				Thread currenThread = Thread.currentThread();
				ClassLoader ccl = currenThread.getContextClassLoader();
				currenThread.setContextClassLoader(this);
				try {
					if (entryName.contains("DriverManager")) {
						Thread.dumpStack();
					}
					Class<?> loadedClass = loadClass(entryName, false);
					if (clazz.isAssignableFrom(loadedClass)) {
						Class<? extends T> matchingClass =
								Unchecked.cast(loadedClass);
						classes.add(matchingClass);
					}
				} catch (NoClassDefFoundError e) {
					if (LOGGER.isLoggable(Level.FINEST))
						LOGGER.log(
							Level.FINEST,
							"Unable to load class '" + entryName + "'",
							e
						);
					continue;
				} catch (ClassNotFoundException e) {
					if (LOGGER.isLoggable(Level.FINEST))
						LOGGER.log(
							Level.FINEST,
							"Unable to load class '" + entryName + "'",
							e
						);
					continue;
				} finally {
					currenThread.setContextClassLoader(ccl);
				}
			}
		} catch (IOException e) {
			LOGGER.log(
				Level.WARNING,
				"Unable to resolve implementors from '" + url.toString() + "'"
			);
		}
		return classes.toArray(new Class[classes.size()]);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getClass().getSimpleName());
		sb.append("[").append(getFileName(getURLs()[0])).append("]");
		return sb.toString();
	}
	
	private static String getFileName(URL url) {
		Objects.requireNonNull(url);
		return getFileName(url.toString());
	}
	
	private static String getFileName(String classPathEntry) {
		if (classPathEntry == null) {
			return null;
		}
		int idx = classPathEntry.lastIndexOf('/');
		if (idx < 0) {
			return classPathEntry;
		}
		return classPathEntry.substring(idx + 1);
	}

}
