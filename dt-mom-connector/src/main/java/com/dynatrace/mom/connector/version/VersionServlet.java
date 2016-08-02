package com.dynatrace.mom.connector.version;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dynatrace.diagnostics.server.shared.security.UserPermissionInfo;
import com.dynatrace.mom.connector.ServerAccess;
import com.dynatrace.mom.utils.SecureHttpServletCtx;

public final class VersionServlet extends SecureHttpServletCtx {
	
	private static final long serialVersionUID = 1L;
	
	private final String version;
	
	public VersionServlet(String version, ServerAccess serverAccess) {
		super(serverAccess);
		this.version = version;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void get(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException
	{
		UserPermissionInfo upi = (UserPermissionInfo) req.getAttribute(UserPermissionInfo.class.getName());
		String userId = null;
		if (upi != null) {
			userId = upi.getUserId();
		}
		resp.setHeader("Content-Type", "application/xml");
		resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
		resp.setHeader("Pragma", "no-cache"); // HTTP 1.0
		resp.setDateHeader("Expires", 0); // Proxies.		
		try (PrintWriter pw = resp.getWriter()) {
			pw.println(version + "<!-- userId: " + userId + " -->");
		}
	}

}
