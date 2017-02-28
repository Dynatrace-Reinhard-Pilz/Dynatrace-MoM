package com.dynatrace.mom.connector.templates;

import java.io.File;

import com.dynatrace.diagnostics.sdk.security.Permission;
import com.dynatrace.diagnostics.sdk.sessions.SessionReference;
import com.dynatrace.diagnostics.sdk.sessions.SessionType;
import com.dynatrace.diagnostics.server.shared.security.UserPermissionInfo;
import com.dynatrace.mom.connector.ServerAccess;
import com.dynatrace.mom.connector.XmlFileHttpServletCtx;
import com.dynatrace.mom.connector.model.profiletemplates.ProfileTemplateReference;
import com.dynatrace.mom.connector.model.profiletemplates.ProfileTemplateReferences;
import com.dynatrace.utils.files.AbstractFileReferences;

public class TemplatesHttpServletCtx extends XmlFileHttpServletCtx<ProfileTemplateReference> {
	
	private static final long serialVersionUID = 1L;
	
	private static final File FLD_PROFILE_TEMPLATES =
			new File(new File(new File("conf"), "profiles"), "templates");
	
	public static final String HTTP_CONTEXT = "/mom/profiletemplates";
	private static final String EXT_PROFILE_TEMPLATE_XML = ".profile.template.xml";
	
	public TemplatesHttpServletCtx(ServerAccess serverAccess) {
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
		return FLD_PROFILE_TEMPLATES;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbstractFileReferences<ProfileTemplateReference> createContainer() {
		return new ProfileTemplateReferences();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProfileTemplateReference createContainerElement() {
		return new ProfileTemplateReference();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] getExtensions() {
		return new String[] { EXT_PROFILE_TEMPLATE_XML };
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
