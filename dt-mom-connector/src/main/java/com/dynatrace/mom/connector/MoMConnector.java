package com.dynatrace.mom.connector;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import com.dynatrace.diagnostics.server.interfaces.ServerAccessProvider;
import com.dynatrace.diagnostics.server.startup.ServerStartupHandler;
import com.dynatrace.mom.connector.dashboards.DashboardsHttpServletCtx;
import com.dynatrace.mom.connector.profiles.ProfilesHttpServletCtx;
import com.dynatrace.mom.connector.serverconfig.ServerConfigHttpServletCtx;
import com.dynatrace.mom.connector.templates.TemplatesHttpServletCtx;
import com.dynatrace.mom.connector.version.VersionServlet;
import com.dynatrace.xml.XMLUtil;

/**
 * This class acts as mediator for multiple APIs within the dynaTrace Server.
 * <ul>
 * 	<li>
 * 		As a {@link ServerStartupHandler} it is able to receive notifications
 * 		about the dynaTrace Server having been fully started up. Although at
 * 		this point no activity happens explicitly it ensures that at least one
 * 		of the classes within this OSGI Bundle is getting loaded and therefore
 * 		this class's role as {@link BundleActivator} comes into play
 * 	</li>
 * 	<li>
 * 		As a {@link BundleActivator} it is able to get access to the OSGI API,
 * 		specifically it is possible to query for existing OSGI Services.
 * 		The OSGI Services of interest are {@link HttpService}s. One of them is
 * 		the Jetty Server launched internally by the dynaTrace Server.
 * 	</li>
 * 	<li>
 * 		As a {@link ServiceTrackerCustomizer} it is able to get notified about
 * 		any {@link HttpService} that is getting started, stopped or modified.
 * 		That allows for adding additional {@link HttpContext}s to the
 * 		{@link HttpService}s.
 * 	</li> 
 * </ul>
 * @author reinhard.pilz@dynatrace.com
 *
 */
public class MoMConnector implements
	BundleActivator, ServiceTrackerCustomizer<HttpService, HttpService>,
	ServerStartupHandler
{
	
	private static final Logger LOGGER =
			Logger.getLogger(MoMConnector.class.getName());
	
	private static final String DT_SERVER_JETTY_ID =
			"com.dynatrace.diagnostics.server.jetty";
	private static final String JETTY_SERVICE_PROPERTY =
			"other.info";
	
	
	private static final AbstractHttpServletCtx[] SERVLETS =
		new AbstractHttpServletCtx[] { null, null, null, null };
	
	private ServiceTracker<?,?> httpServiceTracker = null;
	private BundleContext ctx = null;
	private Collection<HttpService> httpServices =
			Collections.synchronizedCollection(new ArrayList<HttpService>());
	private ServiceTracker<?,?> serverAccessProviderTracker = null;
	private ServerAccess serverAccess = null;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void start(final BundleContext ctx) throws Exception {
		try {
			this.ctx = ctx;
			serverAccess = new ServerAccess(ctx);
			SERVLETS[0] = new ProfilesHttpServletCtx(serverAccess);
			SERVLETS[1] = new DashboardsHttpServletCtx(serverAccess);
			SERVLETS[2] = new TemplatesHttpServletCtx(serverAccess);
			SERVLETS[3] = new ServerConfigHttpServletCtx(serverAccess);
			
			Collection<ServiceReference<HttpService>> refs =
					ctx.getServiceReferences(HttpService.class, null);
			if (refs != null) {
				for (ServiceReference<HttpService> ref : refs) {
					addingService(ref);
				}
			}
			httpServiceTracker = new ServiceTracker<HttpService, HttpService>(
				ctx,
				HttpService.class.getName(),
				this
			);
			
			serverAccessProviderTracker = new ServiceTracker<ServerAccessProvider, ServerAccessProvider>(
				ctx,
				ServerAccessProvider.class.getName(),
				serverAccess
			);
			serverAccessProviderTracker.open();
			httpServiceTracker.open();
		} catch (Throwable t) {
			LOGGER.log(Level.SEVERE, "Unable to start MomConnector Bundle", t);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stop(BundleContext ctx) throws Exception {
		this.ctx = null;
		ensureHttpServiceTrackerClosed();
		for (HttpService httpService : httpServices) {
			HttpServiceUtils.unregister(SERVLETS, httpService);
			try {
				httpService.unregister("/mom/version");
			} catch (Throwable t) {
				// ignore
			}
		}
		ensureServerAccessProviderServiceTrackerClosed();
		httpServices.clear();
	}
	
	private void ensureServerAccessProviderServiceTrackerClosed() {
		if (serverAccessProviderTracker == null) {
			return;
		}
		try {
			serverAccessProviderTracker.close();
		} catch (Throwable t) {
			LOGGER.log(
				Level.WARNING,
				"Failed to close ServerAccessProviderTracker",
				t
			);
		}
	}
	
	private void ensureHttpServiceTrackerClosed() {
		if (httpServiceTracker == null) {
			return;
		}
		try {
			httpServiceTracker.close();
		} catch (Throwable t) {
			LOGGER.log(
				Level.WARNING,
				"Failed to close HttpServiceTracker",
				t
			);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HttpService addingService(ServiceReference<HttpService> ref) {
		synchronized (this.ctx) {
			if (ref == null) {
				return null;
			}
			HttpService httpService = isJettyService(ref);
			if (httpService == null) {
				return null;
			}
			LOGGER.log(Level.INFO, "addingService");
			synchronized (httpServices) {
				if (httpServices.contains(httpService)) {
					LOGGER.log(Level.INFO, "httpServices already contains " + httpServices);
					return httpService;
				}
				httpServices.add(httpService);
			}
			HttpServiceUtils.register(SERVLETS, httpService);
			
			Version version = ctx.getBundle().getVersion();
			int major = version.getMajor();
			int minor = version.getMinor();
			int micro = version.getMicro();
			String sQualifier = version.getQualifier();
			int qualifier = 0;
			try {
				qualifier = Integer.parseInt(sQualifier);
			} catch (Throwable t) {
				t.printStackTrace(System.err);
			}
			com.dynatrace.utils.Version utilsVersion = new com.dynatrace.utils.Version(major, minor, micro, qualifier);
			String sVersion = "1.0.0.0";
			try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
				XMLUtil.serialize(utilsVersion, out);
				sVersion = new String(out.toByteArray());
			} catch (IOException e) {
				LOGGER.log(Level.WARNING, "Unable to serialize MoM Connector Version", e);
			}
			VersionServlet versionServlet = new VersionServlet(sVersion, serverAccess);
			try {
				httpService.registerServlet("/mom/version", versionServlet, new Hashtable<>(), versionServlet);
			} catch (ServletException | NamespaceException e) {
				LOGGER.log(Level.WARNING, "Unable to register Version Servlet", e);
			}
			
			return httpService;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void modifiedService(
		ServiceReference<HttpService> ref,
		HttpService service
	) {
		LOGGER.severe("HTTP SERVICE MODIFIED!!!!!!!!!!!!!!!!!!");
		// nothing to do
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removedService(
		ServiceReference<HttpService> ref,
		HttpService service
	) {
		// nothing to do
	}
	
	private HttpService isJettyService(ServiceReference<HttpService> ref) {
		if (ref == null) {
			return null;
		}
		if (DT_SERVER_JETTY_ID.equals(
			ref.getProperty(JETTY_SERVICE_PROPERTY)
		)) {
			return ctx.getService(ref);
		}
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean startup() {
		return true;
	}

}
