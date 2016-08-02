package com.dynatrace.onboarding.profiles;

import java.io.IOException;

import com.dynatrace.onboarding.variables.DefaultVariables;
import com.dynatrace.utils.VersionedSource;
import com.dynatrace.variables.UnresolvedVariableException;

public interface ProfileTemplate extends VersionedSource<ProfileTemplate> {
	
	public static final String FILE_EXTENSION = ".profile.template.xml".intern();
	
	Profile resolve(DefaultVariables variables, Profile profile)
		throws IOException, UnresolvedVariableException, InvalidProfileNameException;
}
