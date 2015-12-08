package com.dynatrace.mom;

import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HttpUtils {
	
	private static final Logger LOGGER =
			Logger.getLogger(HttpUtils.class.getName());

	public static void sendRedirect(
		ServletRequest request,
		ServletResponse response,
		String location
	) throws IOException {
		sendRedirect(
			(HttpServletRequest) request,
			(HttpServletResponse) response,
			location
		);
	}
	
	private static String adjustLocation(HttpServletRequest request, String location) {
		Objects.requireNonNull(request);
		Objects.requireNonNull(location);
		String contextPath = request.getContextPath();
		if (location.startsWith(contextPath)) {
			
		}
		String loc = location;
		if (loc.startsWith("/")) {
			loc = loc.substring(1);
		}
		if (contextPath.endsWith("/")) {
			contextPath = contextPath.substring(0, contextPath.length() - 1);
		}
		if (loc.startsWith(contextPath)) {
		}
		return null;
	}
	
	private static boolean isAbsolute(String location) {
		Objects.requireNonNull(location);
		return location.startsWith("http");
	}
	
	private static boolean isContextAbsolute(String location) {
		Objects.requireNonNull(location);
		return location.startsWith("/");
	}
	
	public static void sendRedirect(
		HttpServletRequest request,
		HttpServletResponse response,
		String location
	) throws IOException {
		Objects.requireNonNull(request);
		Objects.requireNonNull(response);
		Objects.requireNonNull(location);
		if (isAbsolute(location)) {
			sendRedirect(response, location);
			return;
		}
		int port = request.getServerPort();
		String host = request.getServerName();
		String protocol = request.isSecure() ? "https" : "http";
		if (isContextAbsolute(location)) {
			sendRedirect(response, protocol + "://" + host + ":" + port + request.getContextPath() + location);
			return;
		}
		sendRedirect(response, protocol + "://" + host + ":" + port + request.getRequestURI() + "/" + location);
	}
	
	private static void sendRedirect(
		HttpServletResponse response,
		String location
	) throws IOException {
		LOGGER.log(Level.INFO, "redirecting to " + location);
		response.sendRedirect(location);
	}
}
