package com.dynatrace.utils.files;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.dynatrace.utils.files.FileReferences;
import com.dynatrace.xml.XMLUtil;

@XmlRootElement(name = AbstractFileReferences.XML_ELEMENT_FILE)
@XmlAccessorType(XmlAccessType.PROPERTY)
public abstract class AbstractFileReferences<T extends AbstractFileReference> implements FileReferences<T> {
	
	public static final String XML_ELEMENT_FILE = "files";
	private static final String XML_ATTRIBUTE_HREF = "href";
	
	private String href = null;

	private Collection<T> files = new ArrayList<T>(5);

	/**
	 * {@inheritDoc}
	 */
	@Override
	@XmlAttribute(name = AbstractFileReferences.XML_ATTRIBUTE_HREF)
	public String getHref() {
		return href;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@XmlTransient
	public Collection<T> getFiles() {
		return files;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public void setFiles(Collection<T> files) {
		this.files = files;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Iterator<T> iterator() {
		return files.iterator();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int size() {
		return files.size();
	}

	public final void add(T xmlFile) {
		if (xmlFile == null) {
			return;
		}
		files.add(xmlFile);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((href == null) ? 0 : href.hashCode());
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("rawtypes")
		AbstractFileReferences other = (AbstractFileReferences) obj;
		if (href == null) {
			if (other.href != null)
				return false;
		} else if (!href.equals(other.href))
			return false;
		return true;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {
		return XMLUtil.toString(this);
	}

}
