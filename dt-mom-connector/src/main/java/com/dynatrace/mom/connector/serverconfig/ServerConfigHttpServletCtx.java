package com.dynatrace.mom.connector.serverconfig;

import java.io.File;

import com.dynatrace.diagnostics.sdk.security.Permission;
import com.dynatrace.diagnostics.sdk.sessions.SessionReference;
import com.dynatrace.diagnostics.sdk.sessions.SessionType;
import com.dynatrace.diagnostics.server.shared.security.UserPermissionInfo;
import com.dynatrace.mom.connector.ServerAccess;
import com.dynatrace.mom.connector.XmlFileHttpServletCtx;
import com.dynatrace.mom.connector.model.profiles.SystemProfileReference;
import com.dynatrace.mom.connector.model.profiles.SystemProfileReferences;
import com.dynatrace.utils.files.AbstractFileReferences;

public class ServerConfigHttpServletCtx extends XmlFileHttpServletCtx<SystemProfileReference> {
	
	private static final long serialVersionUID = 1L;
	
	private static final File FLD_CONF = new File("conf");
	
	public static final String HTTP_CONTEXT = "/mom/config";
	private static final String EXT_SERVER_CONFIG_XML = "server.config.xml";
	
	public ServerConfigHttpServletCtx(ServerAccess serverAccess) {
		super(serverAccess);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getContext() {
		return HTTP_CONTEXT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected File getRootFolder() {
		return FLD_CONF;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbstractFileReferences<SystemProfileReference> createContainer() {
		return new SystemProfileReferences();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SystemProfileReference createContainerElement() {
		return new SystemProfileReference();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] getExtensions() {
		return new String[] { EXT_SERVER_CONFIG_XML };
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean checkPermission(UserPermissionInfo info, String id) {
		SessionReference sessionReference =
				SessionReference.createSessionReference(SessionType.live, id);
		return info.hasSystemPermission(
			sessionReference,
			Permission.ExportApplicationSystemProfile
		);
	}

}
