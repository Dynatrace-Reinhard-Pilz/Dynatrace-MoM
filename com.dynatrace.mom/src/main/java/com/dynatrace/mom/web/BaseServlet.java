package com.dynatrace.mom.web;

import javax.servlet.http.HttpServlet;

public class BaseServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	public String foo() {
		return "helo";
	}

}
