package com.dynatrace.web.base;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;

import com.dynatrace.utils.ExecutionContext;
import com.dynatrace.utils.Unchecked;
import com.sun.jersey.api.core.ExtendedUriInfo;

public abstract class CorePagesBase {
	
	@SuppressWarnings("unused")
	private static final Logger LOGGER =
			Logger.getLogger(CorePagesBase.class.getName());
	
	@Context
	protected ServletContext context;
	
	@Context
	protected HttpServletRequest request;
	
	@Context
	protected HttpServletResponse response;
	
	@Context
	protected ExtendedUriInfo uriInfo;
	
	
	protected ExecutionContext getContext(String id) {
		return new ServletExecutionContext(context).getContext(id);
	}
	
	protected <T> T findAttribute(String key) {
		Objects.requireNonNull(key);
		Object attribute = request.getAttribute(key);
		if (attribute != null) {
			return Unchecked.cast(attribute);
		}
		return Unchecked.cast(context.getAttribute(key));
	}
	
	protected <T> T getAttribute(Class<T> c) {
		Objects.requireNonNull(c);
		return Unchecked.cast(request.getAttribute(c.getName()));
	}
	
	protected void setAttribute(Object o) {
		if (o == null) {
			return;
		}
		Class<?> c = o.getClass();
		while (c != null) {
			setAttribute(o, c);
			c = c.getSuperclass();
		}
		c = o.getClass();
		Class<?>[] interfaces = c.getInterfaces();
		if (interfaces != null) {
			for (Class<?> iface : interfaces) {
				setAttribute(o, iface);
			}
		}
	}
	
	protected void setAttribute(Object o, Class<?> c) {
		if (o == null) {
			return;
		}
		if (c == null) {
			return;
		}
		final String className = c.getName();
		if (className.startsWith("java.")) {
			return;
		}
//		context.log("setAttribute(\"" + className + "\", " + o + ")");
		request.setAttribute(c.getName(), o);
		if (c.equals(ModelBase.class)) {
			request.setAttribute("model", o);
		}
		Class<?>[] interfaces = c.getInterfaces();
		if (interfaces != null) {
			for (Class<?> iface : interfaces) {
				setAttribute(o, iface);
			}
		}
	}
	
	protected void log(String msg) {
		context.log(msg);
	}
	
	protected void log(String msg, Throwable t) {
		context.log(msg, t);
	}
	
	protected URI toURI(URL url) {
		try {
			return url.toURI();
		} catch (URISyntaxException e) {
			return null;
		}
	}
	
	protected URL toURL(String url) {
		try {
			return new URL(url);
		} catch (MalformedURLException e) {
			return null;
		}
	}

}
