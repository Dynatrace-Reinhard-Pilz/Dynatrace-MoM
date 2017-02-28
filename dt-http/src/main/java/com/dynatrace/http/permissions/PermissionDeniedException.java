package com.dynatrace.http.permissions;

import java.io.IOException;

public class PermissionDeniedException extends IOException {

	private static final long serialVersionUID = 1L;
	private static final String MSG_MISSING = "Missing Permission: %s";
	
	private final String permission;
	
	public PermissionDeniedException(String permission) {
		this(permission, null);
	}
	
	public PermissionDeniedException(String permission, Throwable t) {
		super(String.format(MSG_MISSING, permission), t);
		this.permission = permission;
	}
	
	public String getPermission() {
		return permission;
	}
	
}
