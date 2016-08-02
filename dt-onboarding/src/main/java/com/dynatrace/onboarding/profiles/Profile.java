package com.dynatrace.onboarding.profiles;

import java.io.IOException;

import com.dynatrace.utils.VersionedSource;

public interface Profile extends VersionedSource<Profile> {
	
	public static final String FILE_EXTENSION = ".profile.xml".intern();

	ProfileTemplate asTemplate() throws IOException;
	
}
