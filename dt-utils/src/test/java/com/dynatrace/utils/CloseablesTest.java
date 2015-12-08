package com.dynatrace.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.dynatrace.Coverage;

/**
 * Tests for class {@link Closeables}
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 */
public class CloseablesTest extends Coverage<Closeables> {
	
	public static class MessedUpDirectory extends File {
		
		private static final long serialVersionUID = 1L;

		public MessedUpDirectory(File file) {
			super(file.getParentFile(), file.getName());
		}
		
		@Override
		public File[] listFiles() {
			return File.listRoots();
		}
	}
	
	public static class MockCloseable implements Closeable {
		private final IOException ioe;
		private final RuntimeException re;
		private final Error e;
		private final AtomicBoolean hasBeenCalled = new AtomicBoolean(false);
		
		public MockCloseable() {
			this.re = null;
			this.ioe = null;
			this.e = null;
		}
		
		public MockCloseable(IOException ioe) {
			this.ioe = ioe;
			this.re = null;
			this.e = null;
		}
		
		public MockCloseable(RuntimeException re) {
			this.re = re;
			this.ioe = null;
			this.e = null;
		}
		
		public MockCloseable(Error e) {
			this.e = e;
			this.ioe = null;
			this.re = null;
		}
		
		public boolean hasBeenCalled() {
			return hasBeenCalled.get();
		}
		
		@Override
		public void close() throws IOException {
			hasBeenCalled.set(true);
			if (ioe != null) {
				throw ioe;
			}
			if (e != null) {
				throw e;
			}
			if (re != null) {
				throw re;
			}
		}
	}
	
	private static byte[] SHORT_ARR;
	private static byte[] MED_ARR;
	private static byte[] LONG_ARR;
	private static byte[] TEXT_ARR;
	
	@BeforeClass
	public static void setup() {
		SHORT_ARR = genArray(Closeables.DEFAULT_BUFFER_SIZE / 2);
		MED_ARR = genArray(Closeables.DEFAULT_BUFFER_SIZE);
		LONG_ARR = genArray(Closeables.DEFAULT_BUFFER_SIZE * 3 / 2);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintWriter pw = new PrintWriter(baos);
		pw.println(UUID.randomUUID().toString());
		pw.println(UUID.randomUUID().toString());
		pw.close();
		TEXT_ARR = baos.toByteArray();
	}
	
	private static byte[] genArray(int size) {
		byte[] bytes = new byte[size];
		for (int i = 0; i < size; i++) {
			bytes[i] = (byte) (i % size);
		}
		return bytes;
	}

	@Test
	public void testCloseOnNull() throws IOException {
		Closeables.close(null);
	}
	
	@Test
	public void testClose() throws IOException {
		MockCloseable closeable = new MockCloseable();
		Closeables.close(closeable);
		Assert.assertTrue(closeable.hasBeenCalled());
	}
	
	@Test(expected = IOException.class)
	public void testCloseWithIOException() throws IOException {
		Closeables.close(new MockCloseable(new IOException()));
	}
	
	@Test(expected = RuntimeException.class)
	public void testCloseWithRuntimeException() throws IOException {
		Closeables.close(new MockCloseable(new RuntimeException()));
	}
	
	@Test(expected = InternalError.class)
	public void testCloseWithInternalError() throws IOException {
		Closeables.close(new MockCloseable(new InternalError()));
	}
	
	@Test
	public void testCloseQuietlyOnNull() throws IOException {
		Closeables.closeQuietly(null);
	}
	
	@Test
	public void testCloseQuietly() throws IOException {
		MockCloseable closeable = new MockCloseable();
		Closeables.closeQuietly(closeable);
		Assert.assertTrue(closeable.hasBeenCalled());
	}
	
	@Test
	public void testCloseQuietlyWithIOException() throws IOException {
		MockCloseable closeable = new MockCloseable(new IOException());
		Closeables.closeQuietly(closeable);
		Assert.assertTrue(closeable.hasBeenCalled());
	}
	
	@Test(expected = RuntimeException.class)
	public void testCloseQuietlyWithRuntimeException() throws IOException {
		Closeables.closeQuietly(new MockCloseable(new RuntimeException()));
	}
	
	@Test(expected = InternalError.class)
	public void testCloseQuietlyWithInternalError() throws IOException {
		Closeables.closeQuietly(new MockCloseable(new InternalError()));
	}
	
	@Test
	public void testCopyIsOs() throws IOException {
		{
			InputStream in = new ByteArrayInputStream(SHORT_ARR);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			Closeables.copy(in, out);
			Assert.assertTrue(Arrays.equals(SHORT_ARR, out.toByteArray()));
		}
		
		{
			InputStream in = new ByteArrayInputStream(MED_ARR);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			Closeables.copy(in, out);
			Assert.assertTrue(Arrays.equals(MED_ARR, out.toByteArray()));
		}
		
		{
			InputStream in = new ByteArrayInputStream(LONG_ARR);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			Closeables.copy(in, out);
			Assert.assertTrue(Arrays.equals(LONG_ARR, out.toByteArray()));
		}
	}
	
	@Test
	public void testCopyLinesIsOs() throws IOException {
		InputStream in = new ByteArrayInputStream(TEXT_ARR);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Closeables.copyLines(in, out);
		Assert.assertTrue(Arrays.equals(TEXT_ARR, out.toByteArray()));
	}

	@Test(expected = IOException.class)
	public void testCopyLinesIsOsExceptions() throws IOException {
		InputStream in = new ByteArrayInputStream(TEXT_ARR);
		OutputStream out = new OutputStream() {

			@Override
			public void write(int b) throws IOException {
				throw new IOException();
			}
			
			@Override
			public void write(byte[] b) throws IOException {
				throw new IOException();
			}
			
			@Override
			public void write(byte[] b, int off, int len) throws IOException {
				throw new IOException();
			}
			
			@Override
			public void flush() throws IOException {
				throw new IOException();
			}
			
		};
		Closeables.copyLines(in, out);
	}
	
	
	@Test
	public void testCopyLinesIsPs() throws IOException {
		InputStream in = new ByteArrayInputStream(TEXT_ARR);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(out);
		Closeables.copyLines(in, ps);
		ps.close();
		Assert.assertTrue(Arrays.equals(TEXT_ARR, out.toByteArray()));
	}
	
	@Test(expected = IOException.class)
	public void testCopyLinesIsPsExceptions() throws IOException {
		InputStream in = new InputStream() {
			@Override
			public synchronized int read() throws IOException {
				throw new IOException();
			}
		};
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(out);
		Closeables.copyLines(in, ps);
		ps.close();
		Assert.assertTrue(Arrays.equals(TEXT_ARR, out.toByteArray()));
	}
	
	@Test
	public void testCopyIsOsLimited() throws IOException {
		{
			InputStream in = new ByteArrayInputStream(SHORT_ARR);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			Closeables.copy(in, out, SHORT_ARR.length);
			Assert.assertTrue(Arrays.equals(SHORT_ARR, out.toByteArray()));
		}
		
		{
			InputStream in = new ByteArrayInputStream(SHORT_ARR);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			Closeables.copy(in, out, SHORT_ARR.length / 2);
			Assert.assertTrue(Arrays.equals(Arrays.copyOf(SHORT_ARR, SHORT_ARR.length / 2), out.toByteArray()));
		}
		
		{
			InputStream in = new ByteArrayInputStream(MED_ARR);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			Closeables.copy(in, out, MED_ARR.length);
			Assert.assertTrue(Arrays.equals(MED_ARR, out.toByteArray()));
		}
		
		{
			InputStream in = new ByteArrayInputStream(MED_ARR);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			Closeables.copy(in, out, MED_ARR.length / 2);
			Assert.assertTrue(Arrays.equals(Arrays.copyOf(MED_ARR, MED_ARR.length / 2), out.toByteArray()));
		}
		
		
		{
			InputStream in = new ByteArrayInputStream(LONG_ARR);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			Closeables.copy(in, out);
			Assert.assertTrue(Arrays.equals(LONG_ARR, out.toByteArray()));
		}
		
		{
			InputStream in = new ByteArrayInputStream(LONG_ARR);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			Closeables.copy(in, out, LONG_ARR.length / 2);
			Assert.assertTrue(Arrays.equals(Arrays.copyOf(LONG_ARR, LONG_ARR.length / 2), out.toByteArray()));
		}
		
	}
	
	@Test
	public void testDelete() throws IOException {
		Closeables.delete(null);
		Closeables.delete(new File(UUID.randomUUID().toString()));
		Closeables.delete(File.listRoots()[0]);
		File tempFile = File.createTempFile(
			UUID.randomUUID().toString(),
			UUID.randomUUID().toString()
		);
		try {
			Closeables.delete(tempFile);
			Assert.assertFalse(tempFile.exists());
		} finally {
			if (tempFile.exists()) {
				tempFile.deleteOnExit();
			}
		}
	}
	
	@Test
	public void testDeleteFail() throws IOException {
		Closeables.delete(null, true);
		Closeables.delete(null, false);
		Closeables.delete(new File(UUID.randomUUID().toString()), false);
		Closeables.delete(new File(UUID.randomUUID().toString()), true);
		Closeables.delete(File.listRoots()[0], false);
		File tempFile = File.createTempFile(
			UUID.randomUUID().toString(),
			UUID.randomUUID().toString()
		);
		try {
			Closeables.delete(tempFile, true);
			Assert.assertFalse(tempFile.exists());
		} finally {
			if (tempFile.exists()) {
				tempFile.deleteOnExit();
			}
		}
	}
	
	@Test(expected = IOException.class)
	public void testDeleteFailing() throws IOException {
		Closeables.delete(File.listRoots()[0], true);
	}
	
	@Test
	public void testIsRoot() throws IOException {
		Assert.assertFalse(Closeables.isRoot(null));
		Assert.assertFalse(
			Closeables.isRoot(new File(UUID.randomUUID().toString()))
		);
		File tempFile = File.createTempFile(
			UUID.randomUUID().toString(),
			UUID.randomUUID().toString()
		);
		try {
			Assert.assertFalse(Closeables.isRoot(tempFile));
		} finally {
			if (tempFile.exists()) {
				tempFile.delete();
			}
		}
		Assert.assertTrue(Closeables.isRoot(File.listRoots()[0]));
	}
	
	@Test
	public void testPurge() throws IOException {
		Assert.assertTrue(Closeables.purge((File[])null));
		Assert.assertTrue(Closeables.purge(new File[0]));
		File tempFile = File.createTempFile(
			UUID.randomUUID().toString(),
			UUID.randomUUID().toString()
		);
		try {
			Assert.assertTrue(Closeables.purge(tempFile, tempFile));
		} finally {
			if (tempFile.exists()) {
				tempFile.delete();
			}
		}
		try {
			Assert.assertFalse(Closeables.purge(tempFile, File.listRoots()[0]));
		} finally {
			if (tempFile.exists()) {
				tempFile.delete();
			}
		}
		File tmpFolder = createTempFolder();
		File file = new File(tmpFolder, UUID.randomUUID().toString());
		try {
			Assert.assertTrue(file.createNewFile());
			Assert.assertTrue(Closeables.purge(tmpFolder));
		} finally {
			if (tmpFolder.exists()) {
				tmpFolder.delete();
			}
		}
		
		tmpFolder = createTempFolder();
		File messedUpFolder = new MessedUpDirectory(tmpFolder);
		try {
			Assert.assertFalse(Closeables.purge(messedUpFolder));
		} finally {
			if (tmpFolder.exists()) {
				tmpFolder.delete();
			}
		}
		
		
		
	}
	
	@Test
	public void testExists() throws IOException {
		Assert.assertFalse(Closeables.exists(null));
		Assert.assertFalse(
			Closeables.exists(new File(UUID.randomUUID().toString()))
		);
		File tempFile = File.createTempFile(
			UUID.randomUUID().toString(),
			UUID.randomUUID().toString()
		);
		try {
			Assert.assertTrue(Closeables.exists(tempFile));
		} finally {
			Closeables.delete(tempFile);
		}
		File tmpFolder = createTempFolder();
		try {
			Assert.assertTrue(Closeables.exists(tmpFolder));
		} finally {
			Closeables.delete(tmpFolder);
		}
	}
	
	@Test
	public void testExistsFile() throws IOException {
		Assert.assertFalse(Closeables.existsFile(null));
		Assert.assertFalse(
			Closeables.existsFile(new File(UUID.randomUUID().toString()))
		);
		File tempFile = File.createTempFile(
			UUID.randomUUID().toString(),
			UUID.randomUUID().toString()
		);
		try {
			Assert.assertTrue(Closeables.existsFile(tempFile));
		} finally {
			Closeables.delete(tempFile);
		}
		File tmpFolder = createTempFolder();
		try {
			Assert.assertFalse(Closeables.existsFile(tmpFolder));
		} finally {
			Closeables.delete(tmpFolder);
		}
	}
	
	@Test
	public void testExistsFolder() throws IOException {
		Assert.assertFalse(Closeables.existsFolder(null));
		Assert.assertFalse(
			Closeables.existsFolder(new File(UUID.randomUUID().toString()))
		);
		File tempFile = File.createTempFile(
			UUID.randomUUID().toString(),
			UUID.randomUUID().toString()
		);
		try {
			Assert.assertFalse(Closeables.existsFolder(tempFile));
		} finally {
			Closeables.delete(tempFile);
		}
		File tmpFolder = createTempFolder();
		try {
			Assert.assertTrue(Closeables.existsFolder(tmpFolder));
		} finally {
			Closeables.delete(tmpFolder);
		}
	}
	
	@Override
	protected Class<Closeables> getCoverageClass() {
		return Closeables.class;
	}
	
	private static File createTempFolder() throws IOException {
		Path path = Files.createTempDirectory(UUID.randomUUID().toString());
		return path.toFile();
	}

}
