package com.dynatrace.mom.web;

import java.io.IOException;
import java.util.Objects;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dynatrace.mom.HttpUtils;

/**
 * A Servlet Filter which handles authentication.
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 */
@WebFilter(servletNames = "jersey-servlet")
public class AuthenticationFilter implements Filter {
	
	@SuppressWarnings("unused")
	private static final Logger LOGGER =
			Logger.getLogger(AuthenticationFilter.class.getName());

	/**
	 * {@inheritDoc}
	 * 
	 * @throws NullPointerException if argument {@code config} is {@code null}
	 */
	@Override
	public final void init(final FilterConfig config) throws ServletException {
		// no filter config expected
	}
	
	private String removeLeadingSlashes(String s) {
		if (s == null) {
			return null;
		}
		String result = s;
		while (result.charAt(0) == '/') {
			result = s.substring(1);
		}
		return result;
	}

	private String removeTrailingSlashes(String s) {
		if (s == null) {
			return null;
		}
		String result = s;
		while (result.charAt(result.length() - 1) == '/') {
			result = s.substring(0, result.length() - 1);
		}
		return result;
	}
	
	private String unslash(String s) {
		if (s == null) {
			return null;
		}
		String result = removeLeadingSlashes(s);
		result = removeTrailingSlashes(result);
		return result;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void doFilter(
			ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException
	{
		
		HttpServletResponse response = (HttpServletResponse) res;
		SecureRequest request = new SecureRequest((HttpServletRequest) req);
		String uri = unslash(request.getRequestURI());
		String ctxPath = unslash(request.getContextPath());
		if (Objects.equals(uri, ctxPath)) {
			HttpUtils.sendRedirect(request, response, request.getHomeURI());
			return;
		}
//		LOGGER.log(Level.INFO, "----- " + request.getRequestURI());
//		HttpSession session = request.getSession(false);
//		String sessionId = null;
//		if (session != null) {
//			sessionId = session.getId();
//		}
//		LOGGER.log(Level.INFO, "  /-- session: " + sessionId);
//		HttpSession session = request.getSession(false);
//		LOGGER.log(Level.INFO, "----- session: " + (session == null ? "null" : session.getId()));
		
//		if (request.authenticate(response)) {
//			request.login("admin", "admin");
			chain.doFilter(request, response);
//		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void destroy() {
		// nothing to do
	}

}
