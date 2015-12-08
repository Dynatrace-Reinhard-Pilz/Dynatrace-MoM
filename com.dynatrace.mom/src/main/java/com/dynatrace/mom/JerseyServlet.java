package com.dynatrace.mom;

import javax.servlet.FilterConfig;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.ws.rs.core.Application;

import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.spi.container.servlet.ServletContainer;
import com.sun.jersey.spi.container.servlet.WebConfig;

@WebServlet(
	name = "jersey-servlet",
	initParams = {
		@WebInitParam(
			name = PackagesResourceConfig.PROPERTY_PACKAGES,
			// value = "com.dynatrace.optional.mom.rest;com.dynatrace.optional.mom.web"
			value = "com.dynatrace"
		)
	},
	urlPatterns = {
		"/*"	
	}
)
public class JerseyServlet extends ServletContainer {

	private static final long serialVersionUID = 1L;

	public JerseyServlet() {
    }

    public JerseyServlet(Class<? extends Application> appClass) {
    	super(appClass);
    }

    public JerseyServlet(Application app) {
    	super(app);
    }
    
    @Override
    public void init() throws ServletException {
    	super.init();
    }
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    	super.init(filterConfig);
    }
    
    @Override
    public void init(ServletConfig config) throws ServletException {
    	super.init(config);
    }
    
    @Override
    protected void init(WebConfig webConfig) throws ServletException {
    	super.init(webConfig);
    }

}
