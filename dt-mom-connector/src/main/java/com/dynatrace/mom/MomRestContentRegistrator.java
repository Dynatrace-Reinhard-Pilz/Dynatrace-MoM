package com.dynatrace.mom;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.security.Constraint;

import com.dynatrace.diagnostics.server.interfaces.ServerAccessProvider;
import com.dynatrace.diagnostics.webservices.registry.PluginContentRegistrator;
import com.dynatrace.mom.connector.AbstractHttpServletCtx;
import com.dynatrace.mom.connector.MoMConnector;

/**
 * Extendend ContentRegistrator which handles REST specific tasks.
 *
 * @author martin.wurzinger
 */
public class MomRestContentRegistrator extends PluginContentRegistrator {
	
	private static final Logger LOGGER = Logger.getLogger(MomRestContentRegistrator.class.getName());

	/**
	 * @param serverAccessProvider a {@link ServerAccessProvider} that must not be null
	 */
	public MomRestContentRegistrator(ServerAccessProvider serverAccessProvider) {
		super(serverAccessProvider);
	}

    @Override
    public void register(ServletContextHandler defaultContextHandler, List<ConstraintMapping> constraints) {

        Map<String, String> initParams = new HashMap<>();
        
        registerServlet(defaultContextHandler, "/mom/version", MomRestActivator.version,
                initParams);

        ConstraintMapping constraint = new ConstraintMapping();
        constraint.setPathSpec("/mom/version");
        constraint.setConstraint(new Constraint());
        constraints.add(constraint);
        
        for (AbstractHttpServletCtx c : MoMConnector.SERVLETS) {
        	String context = c.getContext();
            registerServlet(defaultContextHandler, context + "/*", c, initParams);
            constraint = new ConstraintMapping();
            constraint.setPathSpec(context + "/*");
            constraint.setConstraint(new Constraint());
            constraints.add(constraint);
        }

        LOGGER.info("************** registered!!!!!!!!!!!!! ********************");
    }

}
