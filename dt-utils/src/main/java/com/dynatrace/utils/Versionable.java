package com.dynatrace.utils;

/**
 * Objects implementing the {@link Versionable} are required to offer a
 * {@link Version} they are representing.
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 */
public interface Versionable extends Comparable<Versionable> {

	/**
	 * @return the {@link Version} of this implementation of {@link Versionable}
	 */
	Version getVersion();
	
	/**
	 * Checks if {@link Version} of this object includes the {@link Version}
	 * the given {@link Versionable} offers.
	 * 
	 * @param versionable the {@link Versionable} whichs {@link Version} to
	 * 		check if it is included in the {@link Version} offered by this
	 * 		implementation
	 * 
	 * @return {@code true} if the {@link Version} represented by this object
	 * 		includes the {@link Version} represented by the given
	 * 		{@link Versionable}, {@code false} otherwise
	 */
	boolean includes(Versionable versionable);
	
	void updateVersion(Version version);
	
	boolean equals(Versionable version);
	
}
