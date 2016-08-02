package com.dynatrace.utils.files;

/**
 * Classes implementing {@link FileReference} are expected to provide meta information
 * about an XML based configuration file available on the Dynatrace Server.
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 */
public interface FileReference {

	/**
	 * @return the unique name of the configuration file <strong>without</strong>
	 * 	the file extension (e.g. {@code .profile.xml} or {@code .dashboard.xml}
	 */
	String getId();
	
	/**
	 * @return a URL that is able to provide the contents of the file
	 * 	represented by this object.
	 */
	String getHref();
	
	/**
	 * @return the last modification time stamp of the file represented by this
	 * 	object
	 */
	long getLastModified();
	
	/**
	 * @return the size of the file represented by this object
	 */
	long getSize();
	
	/**
	 * @return the dynaTrace Version of that file. This may differ from the
	 * 	version of the dynaTrace Server it is deployed at
	 */
	String getVersion();
	
}
