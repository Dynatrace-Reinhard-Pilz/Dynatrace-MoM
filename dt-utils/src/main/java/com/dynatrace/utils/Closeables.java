package com.dynatrace.utils;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for handling I/O related operations, dealing with
 * {@link InputStream}s, {@link OutputStream}s, {@link Closeable}s, etc.
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 */
public class Closeables {
	
	private static final Logger LOGGER =
			Logger.getLogger(Closeables.class.getName());
	
	public static final int DEFAULT_BUFFER_SIZE = 2 * 1024;
	
	private static final File[] ROOTS = File.listRoots();

	/**
	 * Invokes {@link Closeable#close()} for the given object unless it is
	 * {@code null}.
	 * 
	 * @param closeable the {@link Closeable} to invoke
	 * 		{@link Closeable#close()} for
	 * 
	 * @throws IOException in case closing the resource leads to an error
	 */
	public static final void close(Closeable closeable) throws IOException {
		if (closeable == null) {
			return;
		}
		closeable.close();
	}

	/**
	 * Invokes {@link Closeable#close()} for the given object unless it is
	 * {@code null} and ignores a potential {@link IOException}.
	 * 
	 * @param closeable the {@link Closeable} to invoke
	 * 		{@link Closeable#close()} for
	 */
	public static final void closeQuietly(Closeable closeable) {
		if (closeable == null) {
			return;
		}
		try {
			closeable.close();
		} catch (final IOException e) {
			// ignore
		}
	}
	
	/**
	 * Copies the available contents of the given {@link InputStream} to the
	 * given {@link OutputStream}.
	 * 
	 * @param in the {@link InputStream} to copy the data from
	 * @param out the {@link OutputStream} to copy the data into
	 * 
	 * @return the number of bytes copied
	 * 
	 * @throws IOException if either accessing the {@link InputStream} or the
	 * 		{@link OutputStream} leads to an error
	 */
	public static long copy(InputStream in, OutputStream out)
		throws IOException
	{
		return copy(DEFAULT_BUFFER_SIZE, in, out);
	}
	
	public static long copy(int bufferSize, InputStream in, OutputStream out)
			throws IOException
		{
			Objects.requireNonNull(in);
			Objects.requireNonNull(out);
			long len = 0;
			final byte buffer[] = new byte[bufferSize];
			int read = in.read(buffer, 0, buffer.length);
			while (read > 0) {
				len = len + read;
				out.write(buffer, 0, read);
				read = in.read(buffer);
			}
			return len;
		}
	
	public static long copy(InputStream in, OutputStream out, int len) throws IOException {
		return copy(DEFAULT_BUFFER_SIZE, in, out, len);
	}
	
	public static long copy(URL url, OutputStream out) throws IOException {
		try (InputStream in = url.openStream()) {
			return copy(in, out);
		}
	}
	
	public static long copy(URL url, File dest) throws IOException {
		try (
			InputStream in = url.openStream();
			OutputStream out = new FileOutputStream(dest)) {
			return copy(in, out);
		}
	}
	
	/**
	 * Copies up to the given amount of bytes contents from the given
	 * {@link InputStream} to the given {@link OutputStream}.
	 *  
	 * @param in the {@link InputStream} to copy data from
	 * @param out the {@link OutputStream} to copy data into
	 * @param len the maximum amount of bytes to copy
	 * 
	 * @return the number of bytes copied
	 * 
	 * @throws IOException if either accessing the {@link InputStream} or the
	 * 		{@link OutputStream} leads to an error
	 */
	public static long copy(int bufferSize, InputStream in, OutputStream out, int len)
		throws IOException
	{
		Objects.requireNonNull(in);
		Objects.requireNonNull(out);
		long numCopied = 0;
		final byte buffer[] = new byte[DEFAULT_BUFFER_SIZE];
		int readSum = 0;
		int read = 0;
		int bytesToRead = Math.min(len - readSum, buffer.length);
		read = in.read(buffer, 0, bytesToRead);
		while (read > 0) {
			readSum += read;
			numCopied = numCopied + read;
			out.write(buffer, 0, read);
			bytesToRead = Math.min(len - readSum, buffer.length);
			read = in.read(buffer, 0, bytesToRead);
		}
		return numCopied;
	}
	
	/**
	 * Copies the contents of the given {@link InputStream} into the given
	 * {@link OutputStream} line by line using the default system encoding, both
	 * for reading from the {@link InputStream} and writing into the
	 * {@link OutputStream}.
	 * 
	 * @param in the {@link InputStream} to read lines from
	 * @param out the {@link OutputStream} to write lines into
	 * 
	 * @throws IOException if either accessing the {@link InputStream} or the
	 * 		{@link OutputStream} leads to an error
	 */
	public static void copyLines(InputStream in, OutputStream out)
		throws IOException
	{
		Objects.requireNonNull(in);
		Objects.requireNonNull(out);
		PrintStream ps = null;
		try {
			ps = new PrintStream(out);
			copyLines(in, ps);
		} finally {
			close(ps);
		}
	}
	
	/**
	 * Copies the contents of the given {@link InputStream} line by line into
	 * the given {@link PrintStream} using the default system encoding for
	 * reading from the {@link InputStream}.
	 * 
	 * @param in the {@link InputStream} to read lines from
	 * @param out the {@link PrintStream} to write lines into
	 * 
	 * @throws IOException if either accessing the {@link InputStream} or the
	 * 		{@link PrintStream} leads to an error
	 */
	public static void copyLines(InputStream in, PrintStream out)
		throws IOException
	{
		Objects.requireNonNull(in);
		Objects.requireNonNull(out);
		InputStreamReader isr = null;
		try {
			isr = new InputStreamReader(in);
			copyLines(isr, out);
		} finally {
			close(isr);
		}
	}
	
	/**
	 * Copies the contends of the given {@link InputStreamReader} line by line
	 * into the given {@link PrintStream}.
	 * 
	 * @param in the {@link InputStreamReader} to read lines from
	 * @param out the {@link PrintStream} to write lines into
	 * 
	 * @throws IOException if either accessing the {@link InputStreamReader}
	 * 		or the {@link PrintStream} leads to an error
	 */
	public static void copyLines(InputStreamReader in, PrintStream out)
		throws IOException
	{
		Objects.requireNonNull(in);
		Objects.requireNonNull(out);
		BufferedReader br = null;
		try {
			br = new BufferedReader(in);
			String line = br.readLine();
			while (line != null) {
				out.println(line);
				line = br.readLine();
			}
			if (out.checkError()) {
				throw new IOException();
			}
		} finally {
			close(br);
		}
	}
	
	/**
	 * Deletes the given {@link File} unless the parameter is {@code null} or
	 * the given file does not exist.<br />
	 * <br />
	 * In case the {@link File} DOES exist but could not get deleted an
	 * {@link IOException} will be thrown unless parameter {@code fail} is set
	 * to {@code false}.
	 * 
	 * @param file the {@link File} to delete
	 * @param fail if {@code true}, ensures that an {@link IOException} will be
	 * 		thrown in case the given {@link File} could not get deleted
	 * 
	 * @throws IOException in case the given {@link File} could not get deleted
	 * 		and the argument {@code fail} is set to {@code true}. 
	 */
	public static void delete(File file, boolean fail) throws IOException {
		if ((file == null) || !file.exists()) {
			return;
		}
		if (fail && !file.delete()) {
			throw new IOException(
				file.getAbsolutePath() + " could not be deleted"
			);
		}
	}
	
	/**
	 * Quietly deletes the given {@link File} unless the parameter is
	 * {@code null} or the {@link File} does not exist.
	 * 
	 * @param file the {@link File} to delete
	 */
	public static void delete(File file) {
		if ((file == null) || !file.exists()) {
			return;
		}
		if (!file.delete()) {
			LOGGER.log(
				Level.FINEST,
				file.getAbsolutePath() + " could not be deleted"
			);
		}
	}
	
	/**
	 * Checks if the given {@link File} exists after performing the necessary
	 * {@code null} check.
	 * 
	 * @param file the {@link File} to check whether it exists
	 * 
	 * @return {@code true} if the given {@link File} is not {@code null} and
	 * 		exists, {@code false} otherwise
	 */
	public static boolean exists(File file) {
		return (file != null) && file.exists();
	}
	
	/**
	 * Checks if the given {@link File} exists and is actually a file and not
	 * e.g. a directory after performing the necessary {@code null} check.
	 * 
	 * @param file the {@link File} to check whether it exists and is an actual
	 * 		file.
	 * 
	 * @return {@code true} if the given {@link File} is not {@code null} and is
	 * 		an existing file and not e.g. a directory
	 */
	public static boolean existsFile(File file) {
		return (file != null) && file.exists() && file.isFile();
	}
	
	/**
	 * Checks if the given {@link File} exists and is actually a directory and
	 * not a file after performing the necessary {@code null} check.
	 * 
	 * @param file the {@link File} to check whether it exists and is actually
	 * 		a directory
	 * 
	 * @return {@code true} if the given {@link File} is not {@code null} and is
	 * 		an existing directory and not a file.
	 */
	public static boolean existsFolder(File file) {
		return (file != null) && file.exists() && file.isDirectory();
	}
	
	/**
	 * Deletes the given {@link File} which may be either a directory or an
	 * actual file.<br />
	 * <br />
	 * If the given {@link File} is a directory, also the files contained within
	 * this directory will be deleted.
	 * 
	 * @param file the file or directory to delete
	 * 
	 * @return {@code true} if the given {@link File} does not anymore exist
	 * 		after the invocation of this method or has not existed at all or if
	 * 		the given {@link File} was {@code null}, {@code false} otherwise.
	 */
	public static boolean purge(File file) {
		if (!exists(file)) {
			return true;
		}
		if (isRoot(file)) {
			return false;
		}
		if (file.isDirectory()) {
			if (!purge(file.listFiles())) {
				return false;
			}
		}
		return file.delete();
	}
	
	/**
	 * Deletes the given {@link File}s, which either may be actual files or
	 * directories.<br />
	 * <br />
	 * For any entry within the given {@link File}s which are directories also 
	 * the files contained within these directories will be deleted.<br />
	 * <br />
	 * Entries which are {@code null} are treated as non existent files but do
	 * not lead to a {@link NullPointerException}.
	 * 
	 * @param files the {@link File}s to delete
	 * 
	 * @return {@code true} if none of the given {@link File}s exist anymore
	 * 		after the invocation of this method, {@code false} otherwise.
	 */
	public static boolean purge(File... files) {
		if ((files == null) || (files.length == 0)) {
			return true;
		}
		for (File file : files) {
			if (!purge(file)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Checks if the given {@link File} is a root directory according to the
	 * current file system.
	 * 
	 * @param file the {@link File} to check if it is a root of the current
	 * 		file system.
	 * 
	 * @return {@code true} if the given {@link File} is not {@code null} and
	 * 		is a root of the current file system, {@code false} otherwise.
	 */
	public static boolean isRoot(File file) {
		if ((file == null) || !file.exists()) {
			return false;
		}
		for (File root : ROOTS) {
			if (file.equals(root)) {
				return true;
			}
		}
		return false;
	}
	
	public static String getAbsCanonPath(File file) {
		if (file == null) {
			return null;
		}
		try {
			return file.getCanonicalFile().getAbsolutePath();
		} catch (IOException e) {
			return file.getAbsolutePath();
		}
	}
	
	public static InputStream getResourceAsStream(String name) {
		return Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
	}
	
	/**
	 * TODO: move this method
	 * @param url
	 * @return
	 */
	public static String getFilename(URL url) {
		if (url == null) {
			return null;
		}
		try {
			String s = url.toURI().toString();
			int idx = s.lastIndexOf('/');
			if (idx >= 0) {
				s = s.substring(idx + 1);
			}
			return Strings.urlDecode(s);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
	
}
