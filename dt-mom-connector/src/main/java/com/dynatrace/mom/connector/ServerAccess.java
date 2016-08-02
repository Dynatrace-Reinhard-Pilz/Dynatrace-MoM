package com.dynatrace.mom.connector;

import java.util.logging.Logger;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import com.dynatrace.diagnostics.sdk.security.LoginCredentials;
import com.dynatrace.diagnostics.server.interfaces.ServerAccessProvider;
import com.dynatrace.diagnostics.server.interfaces.ServerInterface;
import com.dynatrace.diagnostics.server.shared.security.UserPermissionInfo;

public class ServerAccess implements ServiceTrackerCustomizer<ServerAccessProvider, ServerAccessProvider> {
	
	@SuppressWarnings("unused")
	private static final Logger LOGGER =
			Logger.getLogger(ServerAccess.class.getName());
	
	private final BundleContext ctx;
	private ServerAccessProvider serverAccessProvider = null;
	
	public ServerAccess(BundleContext ctx) {
		this.ctx = ctx;
	}
	
	public UserPermissionInfo login(LoginCredentials loginCredentials) {
		synchronized (ctx) {
			if (serverAccessProvider == null) {
				try {
					ctx.wait();
				} catch (InterruptedException e) {
					return null;
				}
			}
		}
		ServerInterface server = serverAccessProvider.getServerInterface();
		server.login(loginCredentials);
		return server.getUserPermissionInfo();
	}

	@Override
	public ServerAccessProvider addingService(ServiceReference<ServerAccessProvider> reference) {
		synchronized (ctx) {
			serverAccessProvider = ctx.getService(reference);
			ctx.notifyAll();
		}
		return serverAccessProvider;
	}

	@Override
	public void modifiedService(ServiceReference<ServerAccessProvider> reference, ServerAccessProvider service) {
	}

	@Override
	public void removedService(ServiceReference<ServerAccessProvider> reference, ServerAccessProvider service) {
	}

}
