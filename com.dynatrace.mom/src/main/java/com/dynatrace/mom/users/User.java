package com.dynatrace.mom.users;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.dynatrace.utils.ToString;

@XmlRootElement(name = "user")
@XmlAccessorType(XmlAccessType.FIELD)
public final class User {

	@XmlAttribute(name = "login")
	private final String login;
	
	@XmlAttribute(name = "pass")
	private final String pass;
	
	@XmlElementWrapper(name = "userroles")
	@XmlElementRef(type = UserRole.class, name = "role", required = false)
	private Collection<UserRole> roles = new ArrayList<UserRole>(1);
	
	public User(final String login, final String pass) {
		this.login = login;
		this.pass = pass;
	}
	
	public User() {
		this.login = null;
		this.pass = null;
	}
	
	public final String getLogin() {
		return login;
	}
	
	public final String getPass() {
		return pass;
	}
	
	public final Collection<UserRole> getRoles() {
		synchronized (roles) {
			return new ArrayList<UserRole>(roles);
		}
	}
	
	public final void addRole(final UserRole role) {
		synchronized (roles) {
			if (!roles.contains(role)) {
				roles.add(role);
			}
		}
	}
	
	public final boolean removeRole(final UserRole role) {
		synchronized (roles) {
			return roles.remove(role);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {
		return new ToString(this).append("login", login).toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((login == null) ? 0 : login.hashCode());
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
		final User other = (User) obj;
		if (login == null) {
			if (other.login != null)
				return false;
		} else if (!login.equals(other.login))
			return false;
		return true;
	}
	
}
