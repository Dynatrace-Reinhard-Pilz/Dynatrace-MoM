package com.dynatrace.utils.files;

import java.util.Collection;

import com.dynatrace.utils.SizedIterable;

/**
 * Classes implementing {@link FileReferences} are offering a typed list of
 * {@link FileReference}s.
 *  
 * @author reinhard.pilz@dynatrace.com
 *
 * @param <T> a class extending / implementing {@link FileReference}
 */
public interface FileReferences<T extends FileReference> extends SizedIterable<T> {

	/**
	 * @return a URL offering a XML representation of this object
	 */
	String getHref();
	
	/**
	 * @return the {@link FileReference}s representing the list of XML configuration
	 * 	files within this {@link FileReferences}
	 */
	Collection<T> getFiles();
	
}
