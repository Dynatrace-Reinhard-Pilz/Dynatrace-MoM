package com.dynatrace.utils.files;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.dynatrace.utils.files.FileReference;
import com.dynatrace.xml.XMLUtil;

/**
 * An abstract base class for objects representing a XML configuration file
 * deployed on the dynaTrace Server.
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 */
@XmlRootElement(name = AbstractFileReference.XML_ELEMENT_FILE)
@XmlAccessorType(XmlAccessType.PROPERTY)
public abstract class AbstractFileReference implements FileReference {
	
	public static final String XML_ELEMENT_FILE	= "file";
	private static final String XML_ATTRIBUTE_ID	= "id";
	private static final String XML_ATTRIBUTE_HREF	= "href";
	private static final String XML_ATTRIBUTE_VERSION = "version";
	private static final String XML_ATTRIBUTE_LAST_MODIFIED = "lastmodified";
	private static final String XML_ATTRIBUTE_SIZE = "size";

	/**
	 * Unique identifier of the configuration file
	 */
	private String id = null;
	
	/**
	 * URL this file is 
	 */
	private String href = null;
	private String version = null;
	private long lastModified = 0;
	private long size = 0;
	
	public AbstractFileReference() {
		
	}
	
	public AbstractFileReference(String id, String href) {
		this.id = id;
		this.href = href;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@XmlAttribute(name = AbstractFileReference.XML_ATTRIBUTE_ID)
	public String getId() {
		return id;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@XmlAttribute(name = AbstractFileReference.XML_ATTRIBUTE_HREF)
	public String getHref() {
		return href;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@XmlAttribute(name = AbstractFileReference.XML_ATTRIBUTE_VERSION)
	public String getVersion() {
		return version;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@XmlAttribute(name = AbstractFileReference.XML_ATTRIBUTE_LAST_MODIFIED)
	public long getLastModified() {
		return lastModified;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@XmlAttribute(name = AbstractFileReference.XML_ATTRIBUTE_SIZE)
	public long getSize() {
		return size;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public void setHref(String href) {
		this.href = href;
	}
	
	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}
	
	public void setVersion(String version) {
		this.version = version;
	}
	
	public void setSize(long size) {
		this.size = size;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractFileReference other = (AbstractFileReference) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return XMLUtil.toString(this);
	}
	
}
