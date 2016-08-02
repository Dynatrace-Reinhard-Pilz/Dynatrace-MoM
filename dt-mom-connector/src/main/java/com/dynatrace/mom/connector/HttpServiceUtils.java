package com.dynatrace.mom.connector;

import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

public final class HttpServiceUtils {
	
	private static final Logger LOGGER =
			Logger.getLogger(HttpServiceUtils.class.getName());
	
	private HttpServiceUtils() {
		// avoid instatiation
	}

	public static void unregister(AbstractHttpServletCtx[] cs, HttpService s) {
		if ((s == null) || (cs == null)) {
			return;
		}
		for (AbstractHttpServletCtx context : cs) {
			unregister(context, s);
		}
	}
	
	public static void register(AbstractHttpServletCtx[] cs, HttpService s) {
		if ((s == null) || (cs == null)) {
			LOGGER.log(Level.WARNING, "Trying to register NOTHING to HttpService");
			return;
		}
		for (AbstractHttpServletCtx context : cs) {
			register(context, s);
		}
	}
	
	public static final void unregister(AbstractHttpServletCtx c, HttpService s) {
		if ((s == null) || (c == null)) {
			return;
		}
		String context = c.getContext();
		if (context == null) {
			LOGGER.log(
				Level.WARNING,
				"invalid context '" + context + "' - not unregistering"
			);
			return;
		}
		LOGGER.log(Level.INFO, "Unregistering " + context);
		try {
			s.unregister(context);
		} catch (Throwable t) {
			// ignore
			// LOGGER.log(Level.SEVERE, "Unable to unregister " + context, t);
		}
	}
	
	public static final void register(AbstractHttpServletCtx c, HttpService s) {
		if ((s == null) || (c == null)) {
			LOGGER.log(Level.WARNING, "Trying to register NOTHING to HttpService");
			return;
		}
		String context = c.getContext();
		if (context == null) {
			LOGGER.log(
					Level.WARNING,
					"invalid context '" + context + "' - not registering"
				);
			return;
		}
		LOGGER.log(Level.INFO, "registering context " + context);
		try {
			s.registerServlet(context, c, new Hashtable<>(), c);
		} catch (ServletException | NamespaceException e) {
			String msg = e.getMessage();
			if ((msg != null) && msg.contains("is already in use.")) {
				return;
			}
			LOGGER.log(
				Level.WARNING,
				"Unable to unregister " + context,
				e
			);
		}
	}
	
}
