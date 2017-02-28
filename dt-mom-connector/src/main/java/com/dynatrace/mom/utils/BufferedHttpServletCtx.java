package com.dynatrace.mom.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.http.HttpContext;

import com.dynatrace.utils.Closeables;

/**
 * Base class for Servlets that want to be able to send the
 * {@code Content-Length} HTTP Header before sending any actual data to the
 * HTTP Client.<br />
 * <br />
 * Subclasses are not required to handle keep track of the number of bytes
 * written to the {@link HttpServletResponse}s {@link OutputStream}. That's
 * being taken care of within
 * {@link #doGet(HttpServletRequest, HttpServletResponse)}.  
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 */
public abstract class BufferedHttpServletCtx
		extends HttpServlet implements HttpContext {

	private static final long serialVersionUID = 1L;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void doGet(HttpServletRequest req, HttpServletResponse res)
		throws ServletException, IOException
	{
		handleSecurity(req, res);
		BufferedHttpServletResponse bRes = new BufferedHttpServletResponse(res);
		get(req, bRes);
		res.setContentLength(bRes.size());
		try (
			InputStream content = bRes.getContent();
			OutputStream out = res.getOutputStream();
		) {
			Closeables.copy(content, out);
		}
	}
	
	/**
	 * Instead of handling the {@link HttpServletRequest} directly subclasses
	 * need to implement this method for {@code GET} requests.
	 * 
	 * @param req the {@link HttpServletRequest} holding information about the
	 * 		HTTP request to be handled
	 * @param res the {@link HttpServletResponse} offering the infrastructure
	 * 		to send data back to the HTTP Client
	 * @throws ServletException if the request for the GET could not be handled
	 * @throws IOException if an input or output error is detected when the
	 * 	Servlet handles the GET request
	 */
	protected abstract void get(HttpServletRequest req, HttpServletResponse res)
		throws ServletException, IOException;

}
