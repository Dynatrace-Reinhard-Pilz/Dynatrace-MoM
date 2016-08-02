package com.dynatrace.mom.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * An implementation of {@link HttpServletResponseWrapper} that prevents content
 * from being written directly to the HTTP Client.<br />
 * <br />
 * Instead the {@link ServletOutputStream} provided upon calling
 * {@link #getOutputStream()} and the {@link PrintWriter} provided upon calling
 * {@link #getWriter()} are buffering the contents.<br />
 * <br />
 * That allows for sending all required HTTP Headers to the HTTP Client before
 * sending the actual HTTP Response Body, including the {@code Content-Length}
 * HTTP Header.<br />
 * <br />
 * The buffered data can get accessed via {@link #getContent()}, the number of
 * bytes within the buffered data can get accessed via {@link #size()}.
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 */
public final class BufferedHttpServletResponse
		extends HttpServletResponseWrapper {
	
	/**
	 * The {@link ServletOutputStream} offered for callers of
	 * {@link #getOutputStream()} will not write immmediately to the
	 * HTTP Client, but buffers the contents instead.
	 */
	private final BufferedServletOutputStream out =
			new BufferedServletOutputStream();

	/**
	 * c'tor
	 * 
	 * @param res the wrapped {@link HttpServletResponse}
	 */
	public BufferedHttpServletResponse(HttpServletResponse res) {
		super(res);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		return out;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public PrintWriter getWriter() throws IOException {
		// every caller to this method gets its own PrintWriter
		// the underlying OutputStream is however the same for all of thes
		// PrintWriters
		return new PrintWriter(out);
	}
	
	/**
	 * @return the content that has already been written via this
	 * 		{@link HttpServletResponseWrapper}
	 */
	public InputStream getContent() {
		return out.getContent();
	}
	
	/**
	 * @return the number of bytes written to this
	 * 		{@link HttpServletResponseWrapper}
	 */
	public int size() {
		return out.size();
	}
	
}
