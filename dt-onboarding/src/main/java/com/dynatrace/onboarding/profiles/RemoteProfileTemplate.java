package com.dynatrace.onboarding.profiles;

import java.io.File;
import java.io.IOException;

import com.dynatrace.http.config.ServerConfig;
import com.dynatrace.mom.connector.model.profiles.SystemProfileReference;
import com.dynatrace.onboarding.variables.DefaultVariables;
import com.dynatrace.utils.Version;
import com.dynatrace.variables.UnresolvedVariableException;

public class RemoteProfileTemplate extends AbstractRemoteProfile<ProfileTemplate> implements ProfileTemplate {
	
	public RemoteProfileTemplate(SystemProfileReference xmlProfile, ServerConfig serverConfig) {
		super(xmlProfile, serverConfig);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Profile resolve(DefaultVariables variables, Profile profile)
			throws IOException, UnresolvedVariableException, InvalidProfileNameException {
		return localize().resolve(variables, profile);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProfileTemplate createLocalizedInstance(File file, Version version) throws IOException {
		return new LocalProfileTemplate(file);
	}

}
