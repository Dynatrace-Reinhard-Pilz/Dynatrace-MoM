package com.dynatrace.mom.web;

import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.view.Viewable;

@Path("login")
public class AuthenticationPages extends PagesBase {
	
	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger(AuthenticationPages.class.getName());
	
	@POST
	@Produces(MediaType.TEXT_HTML)
	public Viewable loginPOST() throws Exception {
		return new Viewable("/jsp/auth/login.jsp", null);
	}
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	public Viewable loginGET() throws Exception {
		return new Viewable("/jsp/auth/login.jsp", null);
	}
}