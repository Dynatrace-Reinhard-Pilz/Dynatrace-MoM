package com.dynatrace.mom.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;

/**
 * An implementation of {@link ServletOutputStream} that does not directly write
 * content to the HTTP Client, but instead buffers the content, so it can get
 * sent later alongside with the {@code Content-Length} HTTP Header.<br />
 * <br />
 * The content written to this {@link ServletOutputStream} can get accessed via
 * {@link #getContent()}. The number of bytes written to this
 * {@link ServletOutputStream} can get accessed via {@link #size()}. 
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 */
public final class BufferedServletOutputStream extends ServletOutputStream {
	
	/**
	 * Buffered content goes into a {@link ByteArrayOutputStream}, which simply
	 * buffers data in memory.
	 */
	private final ByteArrayOutputStream out = new ByteArrayOutputStream();
	// note: might become a problem when offering huge files via HTTP

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void write(int b) throws IOException {
		out.write(b);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void write(byte[] b) throws IOException {
		out.write(b);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		out.write(b, off, len);
	}
	
	/**
	 * @return the number of bytes written to this {@link ServletOutputStream}
	 */
	public int size() {
		return out.size();
	}
	
	/**
	 * @return the content that has already been written to this
	 * 		{@link ServletOutputStream}
	 */
	public InputStream getContent() {
		return new ByteArrayInputStream(out.toByteArray());
	}

	public boolean isReady() {
		return true;
	}

	public void setWriteListener(WriteListener arg0) {
	}

}
