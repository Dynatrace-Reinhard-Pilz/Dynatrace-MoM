package com.dynatrace.mom.users;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "users")
@XmlAccessorType(XmlAccessType.FIELD)
public class UserManager {

	@XmlElement(type = User.class, name = "user")
	private final Collection<User> users;
	
	public UserManager() {
		 users = new ArrayList<User>(50);
	}
	
	public UserManager(final UserManager m) {
		this.users = m.users;
	}
	
	public final Collection<User> getUsers() {
		synchronized (users) {
			return new ArrayList<User>(users);
		}
	}
	
	public final void addUser(final User user) {
		synchronized (users) {
			if (!users.contains(user)) {
				users.add(user);
			}
		}
	}
	
}
