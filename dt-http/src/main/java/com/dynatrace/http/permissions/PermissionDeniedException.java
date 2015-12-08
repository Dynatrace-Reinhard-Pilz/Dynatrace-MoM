package com.dynatrace.http.permissions;

import java.io.IOException;

public class PermissionDeniedException extends IOException {

	private static final long serialVersionUID = 1L;
	
	private final String permission;
	
	public PermissionDeniedException(String permission) {
		this(permission, null);
	}
	
	public PermissionDeniedException(String permission, Throwable t) {
		super("Missing Permission: " + permission, t);
		this.permission = permission;
	}
	
	public String getPermission() {
		return permission;
	}
	
}
