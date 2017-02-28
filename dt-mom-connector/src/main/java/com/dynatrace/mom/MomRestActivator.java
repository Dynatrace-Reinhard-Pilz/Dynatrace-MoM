package com.dynatrace.mom;

import java.util.logging.Logger;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IExecutableExtensionFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import com.dynatrace.diagnostics.server.interfaces.ServerAccessProvider;
import com.dynatrace.mom.connector.version.VersionServlet;

/**
 * Bundle activator and factory for plugin initialization.
 *
 * @author stefan.ecker
 */
public class MomRestActivator implements BundleActivator, IExecutableExtensionFactory {

    private final static Logger logger = Logger.getLogger(MomRestActivator.class.getName());
    // rawtype until osgi version upgrade
    private static ServiceTracker accessProviderTracker;
    private static BundleContext bundleContext;
    
    public static VersionServlet version;

    @Override
	public void start(BundleContext bundleContext) throws Exception {
        accessProviderTracker = new ServiceTracker(bundleContext, ServerAccessProvider.class, null);
        accessProviderTracker.open();
        MomRestActivator.bundleContext = bundleContext;
	}

    @Override
	public void stop(BundleContext bundleContext) throws Exception {
        accessProviderTracker.close();
        accessProviderTracker = null;
	}

    /*
     * note: in the current implementation of the webservices project, this method is called once and the returned object cached.
     * At config changes, the logic in the content registrator is used to unsubsribe/resubsribe from/to httpservice.
     * Any osgi property changes need to be handled there.
     */
    @Override
    public Object create() throws CoreException {
        ServerAccessProvider serverAccessProvider = (ServerAccessProvider) accessProviderTracker.getService();
        MomRestContentRegistrator contentRegistrator = new MomRestContentRegistrator(serverAccessProvider);
        
        logger.info("MOM REST plugin initialized"); //$NON-NLS-1$

        return contentRegistrator;
    }

}
