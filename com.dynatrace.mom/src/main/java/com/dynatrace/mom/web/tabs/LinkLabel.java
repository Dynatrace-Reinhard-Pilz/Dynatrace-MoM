package com.dynatrace.mom.web.tabs;

import java.util.Objects;

/**
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 */
public final class LinkLabel implements Cloneable {
	
	public static final LinkLabel ADD_SERVER =
			new LinkLabel("New Server", "servers/<new>", null);
	public static final LinkLabel SERVERS =
			new LinkLabel("Servers", "servers", null);
	
	public static final LinkLabel SETTINGS =
			new LinkLabel("Settings", "settings", null);

	public static final LinkLabel USERS =
			new LinkLabel("Users", "settings/users", null);

	public static final LinkLabel MODULES =
			new LinkLabel("Modules", "settings/modules", null);
	
	private String label = null;
	private String link = null;
	private String cssClass = null;
	
	public LinkLabel() {
		
	}
	
	public LinkLabel(final String link) {
		this(null, link, null);
	}
	
	public LinkLabel(final String label, final String link, final String cssClass) {
		Objects.requireNonNull(link);
		this.label = label;
		this.link = link;
		this.cssClass = cssClass;
	}
	
	public final LinkLabel build(final String part) {
		return this.clone().append(part);
	}
	
	private LinkLabel append(final String part) {
		link = new StringBuilder(link).append("/").append(part).toString();
		return this;
	}
	
	public final LinkLabel cssClass(final String cssClass) {
		this.cssClass = cssClass;
		return this;
	}
	
	public final String getCssClass() {
		return cssClass;
	}
	
	public final LinkLabel label(final String label) {
		this.label = label;
		return this;
	}
	
	public final String getLabel() {
		if (label == null) {
			return link;
		}
		return label;
	}
	
	public final void setLink(final String link) {
		this.link = link;
	}
	
	public final String getLink() {
		return link;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {
		return new StringBuilder(LinkLabel.class.getSimpleName())
			.append("[").append("label").append("=").append(label)
			.append(", ").append("link=").append(link).append("]").toString();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final LinkLabel clone() {
		try {
			return (LinkLabel) super.clone();
		} catch (final CloneNotSupportedException e) {
			throw new InternalError(e.getMessage());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LinkLabel other = (LinkLabel) obj;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		return true;
	}
	
	
}
