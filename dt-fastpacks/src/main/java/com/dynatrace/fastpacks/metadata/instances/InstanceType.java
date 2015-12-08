package com.dynatrace.fastpacks.metadata.instances;

import com.dynatrace.fastpacks.metadata.resources.Resource;

/**
 * Resources are supposed to be deployed into at least one instance within a
 * Dynatrace deployment.
 * 
 * @author reinhard.pilz@dynatrace.com
 * 
 * @see Resource
 *
 */
public enum InstanceType {
	
	/**
	 * Indicates that a resource is required to be deployed into Dynatrace
	 * Clients.
	 */
	client,	
	
	/**
	 * Indicates that a resource is required to be deployed into the
	 * Dynatrace Server.
	 */
	server,
	
	/**
	 * Indicates that a resource is required to be deployed into Dynatrace
	 * Collectors.
	 */
	collector;
	
	/**
	 * Checks if the given {@link InstanceType} is valid regarding the
	 * requirement that 
	 * @param instanceType
	 * @return
	 */
	public static boolean isValid(InstanceType instanceType) {
		return (instanceType != null);
	}
	
	
	public static void validate(InstanceType instanceType) {
		if (!isValid(instanceType)) {
			throw new IllegalArgumentException(
					"InstallerType must not be null or undefined"
			);
		}
	}
	
}
