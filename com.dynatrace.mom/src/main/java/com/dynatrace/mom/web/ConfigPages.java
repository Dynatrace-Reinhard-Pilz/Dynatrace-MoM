package com.dynatrace.mom.web;

import static javax.ws.rs.core.MediaType.TEXT_HTML;

import java.io.File;
import java.util.Enumeration;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.dynatrace.mom.MomConfig;
import com.dynatrace.mom.MomWebAppInit;
import com.dynatrace.mom.web.breadcrumbs.BreadCrumbs;
import com.dynatrace.utils.ExecutionContext;
import com.dynatrace.utils.Storage;
import com.sun.jersey.api.view.Viewable;

@Path("config")
public class ConfigPages extends PagesBase {
	
	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger(ConfigPages.class.getName());
	
	@GET
	@Path("storage")
	@Produces(TEXT_HTML)
	public Viewable getStorage() throws Exception {
		BreadCrumbs breadCrumbs = BreadCrumbs.storage();
		setAttribute(breadCrumbs);
		return new Viewable("/jsp/config/storage.jsp", null);
	}

	@POST
	@Path("storage")
	@Produces(TEXT_HTML)
	public Viewable setStorage(@FormParam("storage") String storage) throws Exception {
		MomConfig momConfig = findAttribute(MomConfig.ATTRIBUTE);
		momConfig.setStorage(new File(storage));
		context.setAttribute(ExecutionContext.ATTRIBUTE_STORAGE_FOLDER, new File(new File(new File(storage), ".dynaTrace"), context.getContextPath()));
		System.setProperty(Storage.PROPERTY_WORK_DIR, momConfig.getStorage().getAbsolutePath());
		Storage.refresh();
		MomWebAppInit momWebAppInit = new MomWebAppInit();
		ServletContextEvent evt = new ServletContextEvent(context);
		momWebAppInit.contextDestroyed(evt);
		momWebAppInit.contextInitialized(evt);
		BreadCrumbs breadCrumbs = BreadCrumbs.storage();
		setAttribute(breadCrumbs);
		return new Viewable("/jsp/config/storage.jsp", null);
	}
	
}