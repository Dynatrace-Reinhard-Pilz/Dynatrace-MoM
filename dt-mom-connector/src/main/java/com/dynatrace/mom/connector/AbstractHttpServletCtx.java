package com.dynatrace.mom.connector;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.http.HttpContext;

import com.dynatrace.mom.utils.SecureHttpServletCtx;
import com.dynatrace.utils.Strings;

public abstract class AbstractHttpServletCtx extends SecureHttpServletCtx {
	
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER =
			Logger.getLogger(AbstractHttpServletCtx.class.getName());
	
	private static final char SLASH = '/';
	private static final char SEMICOLON = ';';
	private static final char EQ = '=';
	private static final String HEADER_CONTENT_DISPOSITION =
			"Content-Disposition";
	private static final String INLINE = "inline";
	private static final String FILENAME = "filename";
	
	public AbstractHttpServletCtx(ServerAccess serverAccess) {
		super(serverAccess);
	}

	/**
	 * @return a non {@code null} string URLs have to start with in order to
	 * 		be taken care of by this implementation of {@link HttpContext}.
	 */
	public abstract String getContext();
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean handleSecurity(
		HttpServletRequest req,
		HttpServletResponse res
	) throws IOException {
		boolean isAuthenticated = super.handleSecurity(req, res);
		if (isAuthenticated) {
			setHeaders(
					res,
					getResource(removeContext(req.getRequestURI()))
				);
		}
		return isAuthenticated;
	}
	
	private void setHeaders(HttpServletResponse res, URL resource) {
		if ((res == null) || (resource == null)) {
			return;
		}
		String fileName = getFileName(resource.getPath());
		setContentDisposition(res, fileName);
	}
	
	private static void setContentDisposition(
		HttpServletResponse res,
		String fileName
	) {
		if ((res == null) || (fileName == null)) {
			return;
		}
		res.setHeader(
			HEADER_CONTENT_DISPOSITION,
			new StringBuilder().append(INLINE).append(SEMICOLON)
				.append(FILENAME).append(EQ).append(fileName).toString()
		);
	}
	
	
	protected String removeContext(String s) {
		if (!Strings.startsWith(s, getContext())) {
			return s;
		}
		try {
			return URLDecoder.decode(
				s.substring(getContext().length()),
				StandardCharsets.UTF_8.name()
			);
		} catch (UnsupportedEncodingException e) {
			throw new InternalError(e.getMessage());
		}
	}
	
	protected static String getFileName(String s) {
		if (s == null) {
			return null;
		}
		int idx = s.lastIndexOf(SLASH);
		if (idx < 0) {
			return s;
		}
		return s.substring(idx + 1);
	}
	
	protected URL toURL(File file) {
		if (file == null) {
			return null;
		}
		try {
			return file.toURI().toURL();
		} catch (MalformedURLException e) {
			LOGGER.log(
				Level.FINE,
				"Unable to deliver resource " + file.getName()
			);
			return null;
		}
	}
	
}
