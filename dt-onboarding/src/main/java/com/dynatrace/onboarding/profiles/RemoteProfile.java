package com.dynatrace.onboarding.profiles;

import java.io.File;
import java.io.IOException;

import com.dynatrace.http.config.ServerConfig;
import com.dynatrace.mom.connector.model.profiles.SystemProfileReference;
import com.dynatrace.utils.Version;

public class RemoteProfile extends AbstractRemoteProfile<Profile> implements Profile {
	
	public RemoteProfile(SystemProfileReference reference, ServerConfig serverConfig) {
		super(reference, serverConfig);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Profile createLocalizedInstance(File file, Version version) throws IOException {
		return new LocalProfile(file, getVersion());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProfileTemplate asTemplate() throws IOException {
		return new RemoteProfileTemplate(getReference(), getServerConfig());
	}

}
