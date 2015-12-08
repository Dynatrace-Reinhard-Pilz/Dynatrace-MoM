package com.dynatrace.fastpacks.metadata;


public enum InstallerType {

	plugin,
	patch,
	languagepack,
	resourcepack;
	
	public static boolean isValid(InstallerType installerType) {
		return (installerType != null);
	}
	
	public static void validate(InstallerType installerType) {
		if (!isValid(installerType)) {
			throw new IllegalArgumentException(
					"InstallerType must not be null or undefined"
			);
		}
	}
	
}
