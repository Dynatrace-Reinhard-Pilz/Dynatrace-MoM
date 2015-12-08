package com.dynatrace.mom.users;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;


@XmlRootElement(name = "userrole")
@XmlAccessorType(XmlAccessType.FIELD)
public final class UserRole {

	@XmlValue
	private final String id;
	
	public UserRole(final String id) {
		this.id = id;
	}
	
	public final String getId() {
		return id;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		final UserRole other = (UserRole) obj;
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
	public final String toString() {
		return id;
	}
	
}
