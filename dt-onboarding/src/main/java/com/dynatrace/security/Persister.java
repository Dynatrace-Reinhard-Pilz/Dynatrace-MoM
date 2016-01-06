package com.dynatrace.security;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import com.dynatrace.utils.Closeables;

public class Persister {
	
	private static final String PREFIX = "--encrypt--".intern();
	
	public void persist() {
		try {
			File jarFile = new File(Persister.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
			String jarName = jarFile.getName();
			File jarFolder = jarFile.getParentFile();
			if (jarName.startsWith(PREFIX)) {
				File modifiedJarFile = new File(jarFolder, jarName.substring(PREFIX.length()));
				modifyJar(modifiedJarFile);
				launchJar(modifiedJarFile);
				System.exit(0);
			}
			String encrypterJarName = PREFIX + jarName;
			File encrypterJarFile = new File(jarFolder, encrypterJarName);
			
			if (encrypterJarFile.exists()) {
				encrypterJarFile.delete();
				System.exit(0);
			}
			
			try (
				InputStream in = new FileInputStream(jarFile);
				OutputStream out = new FileOutputStream(encrypterJarFile);
			) {
				Closeables.copy(in, out);
			}
			launchJar(encrypterJarFile);
		} catch (Throwable t) {
			t.printStackTrace(System.err);
		}
	}
	
	private static void println(String s) {
		System.out.println(s);
		System.out.flush();
	}
	
	private void launchJar(File jarFile) throws IOException {
		File java = getJavaExecutable();
		ProcessBuilder pb = new ProcessBuilder().inheritIO();
		pb.command("\"" + java + "\"", "-jar", "\"" + jarFile.getAbsolutePath() + "\"", "encrypt");
//		pb.redirectOutput(Redirect.INHERIT);
//		pb.redirectError(Redirect.INHERIT);
//		pb.redirectInput(Redirect.INHERIT);
		pb.start();
	}
	
	private void modifyJar(File jarFile) throws IOException {
		println("modifying " + jarFile);
		File tempFile = File.createTempFile("encrypt", "dt-onboarding");
		tempFile.deleteOnExit();
		try (
			JarInputStream jis = new JarInputStream(new FileInputStream(jarFile));
			JarOutputStream jos = new JarOutputStream(new FileOutputStream(tempFile));
		)
		{
			Manifest manifest = jis.getManifest();
			byte[] manifestBytes = null;
			try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
				manifest.write(out);
				manifestBytes = out.toByteArray();
			}
			JarEntry jarEntry = new JarEntry("META-INF/MANIFEST.MF");
			jarEntry.setTime(System.currentTimeMillis());
			jarEntry.setSize(manifestBytes.length);
			jos.putNextEntry(jarEntry);
			try (InputStream in = new ByteArrayInputStream(manifestBytes)) {
				Closeables.copy(in, jos);
			}
			jos.closeEntry();
			jos.flush();
			JarEntry srcJarEntry = null;
			while ((srcJarEntry = jis.getNextJarEntry()) != null) {
				jos.putNextEntry(srcJarEntry);
				Closeables.copy(jis, jos);
				jos.closeEntry();
			}
			byte[] contents = "reini".getBytes();
			jarEntry = new JarEntry("reini");
			jarEntry.setTime(System.currentTimeMillis());
			jarEntry.setSize(contents.length);
			jos.putNextEntry(jarEntry);
			try (InputStream in = new ByteArrayInputStream(contents)) {
				Closeables.copy(in, jos);
			}
			jos.closeEntry();
			jos.flush();
		}
		jarFile.delete();
		try (
			InputStream in = new FileInputStream(tempFile);
			OutputStream out = new FileOutputStream(jarFile);
		) {
			Closeables.copy(in, out);
		}
	}
	
	private File getJavaExecutable() {
		File fldJavaHome = new File(System.getProperty("java.home"));
		File fldJavaHomeBin = new File(fldJavaHome, "bin");
		if (!fldJavaHomeBin.exists()) {
			return null;
		}
		File javaExe = new File(fldJavaHomeBin, "java.exe");
		if (javaExe.exists() && javaExe.isFile()) {
			return javaExe;
		}
		File java = new File(fldJavaHomeBin, "java");
		if (java.exists() && java.isFile()) {
			return java;
		}
		return null;
	}

}
