package com.dynatrace.mom.web;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import com.dynatrace.mom.HttpUtils;
import com.dynatrace.mom.MomConfig;

@WebFilter(servletNames = "jersey-servlet")
public class ConfigurationFilter implements Filter {
	
	@SuppressWarnings("unused")
	private static final Logger LOGGER =
			Logger.getLogger(ConfigurationFilter.class.getName());

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		ServletContext servletContext = request.getServletContext();
		MomConfig momConfig = (MomConfig) servletContext.getAttribute(MomConfig.ATTRIBUTE);
		File storage = momConfig.getStorage();
		if (storage == null) {
			HttpServletRequest httpRequest = (HttpServletRequest) request;
			String requestURI = httpRequest.getRequestURI();
			if (requestURI.endsWith("/config/storage")) {
				chain.doFilter(request, response);
				return;
			}
			HttpUtils.sendRedirect(request, response, "/config/storage");
			return;
		}
		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
	}
	
	@Override
	public void destroy() {
	}
	

}
