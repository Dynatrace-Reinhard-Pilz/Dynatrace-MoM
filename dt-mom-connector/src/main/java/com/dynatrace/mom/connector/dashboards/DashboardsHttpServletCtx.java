package com.dynatrace.mom.connector.dashboards;

import java.io.File;
import java.util.UUID;

import com.dynatrace.diagnostics.server.shared.security.UserPermissionInfo;
import com.dynatrace.mom.connector.ServerAccess;
import com.dynatrace.mom.connector.XmlFileHttpServletCtx;
import com.dynatrace.mom.connector.model.dashboards.DashboardReference;
import com.dynatrace.mom.connector.model.dashboards.DashboardReferences;
import com.dynatrace.utils.files.AbstractFileReferences;

public class DashboardsHttpServletCtx extends XmlFileHttpServletCtx<DashboardReference> {
	
	private static final long serialVersionUID = 1L;

	private static final File FLD_PROFILES =
			new File(new File("conf"), "dashboards");
	
	public static final String HTTP_CONTEXT = "/mom/dashboards";
	private static final String EXT_DASHBOARD_XML = ".dashboard.xml";
	
	public DashboardsHttpServletCtx(ServerAccess serverAccess) {
		super(serverAccess);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getContext() {
		return HTTP_CONTEXT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected File getRootFolder() {
		return FLD_PROFILES;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbstractFileReferences<DashboardReference> createContainer() {
		return new DashboardReferences();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DashboardReference createContainerElement() {
		DashboardReference reference = new DashboardReference();
		reference.setKey(UUID.randomUUID().toString());
		return reference;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] getExtensions() {
		return new String[] { EXT_DASHBOARD_XML };
	}
	

	@Override
	public boolean checkPermission(UserPermissionInfo info, String id) {
		return true;
	}

}
