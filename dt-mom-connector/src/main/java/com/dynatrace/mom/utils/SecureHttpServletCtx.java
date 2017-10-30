package com.dynatrace.mom.utils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dynatrace.diagnostics.sdk.security.LoginCredentials;
import com.dynatrace.diagnostics.sdk.security.Permission;
import com.dynatrace.diagnostics.sdk.sessions.SessionReference;
import com.dynatrace.diagnostics.server.security.AbstractSessionContext;
import com.dynatrace.diagnostics.server.security.PermissionManager;
import com.dynatrace.diagnostics.server.security.UserSessionContext;
import com.dynatrace.diagnostics.server.shared.security.UserPermissionInfo;
import com.dynatrace.diagnostics.webservices.UserAccessGate;
import com.dynatrace.mom.connector.ServerAccess;

/**
 * Offers {@code Basic Authentication} free of charge for subclasses.<br />
 * <br />
 * Because it is derived from {@link BufferedHttpServletCtx} it also takes care
 * of setting the {@code Content-Length} HTTP Header before sending any data
 * to the HTTP Client.<br />
 * <br />
 * Authentication is being performed against the configured users within the
 * dynaTrace Server. It does however just check for valid user credentials.
 * Permission checks (e.g. whether the user is actually allowed to access the
 * Web Interface) are not present.
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 */
public abstract class SecureHttpServletCtx extends BufferedHttpServletCtx {

	private static final long serialVersionUID = 1L;
	
	private static final Logger LOGGER =
			Logger.getLogger(SecureHttpServletCtx.class.getName());
	
	/**
	 * The {@code WWW-Authenticate} HTTP Response Header for requests that don't
	 * contain valid user credentials
	 */
	private static final String HEADER_WWW_AUTHENTICATE =
			"WWW-Authenticate".intern();
	
	/**
	 * The value containing the realm for the {@code WWW-Authenticate} HTTP
	 * Response header.
	 */
	private static final String HEADER_VALUE_WWW_AUTHENTICATE =
			"Basic realm=\"dynaTrace Server\"".intern();

	private final ServerAccess serverAccess;
	
	public SecureHttpServletCtx(ServerAccess serverAccess) {
		this.serverAccess = serverAccess;
	}
	
	private LoginCredentials checkLoginCredentials(
		HttpServletRequest request,
		HttpServletResponse response
	)
			throws IOException
	{
		LoginCredentials credentials = UserAccessGate.getLoginCredentials(
			request
		);
		if (credentials != null) {
			return credentials;
		}
		setAuthenticationHeader(response);
		sendError(
			response,
			HttpServletResponse.SC_UNAUTHORIZED,
			"Authorization information missing"
		);
		return null;
	}
	
	private UserPermissionInfo checkUserPermissionInfo(
		LoginCredentials credentials,
		HttpServletResponse response
	)
			throws IOException
	{
		UserPermissionInfo login = serverAccess.login(credentials);
		if (login != null) {
			return login;
		}
		setAuthenticationHeader(response);
    	sendError(
    		response,
    		HttpServletResponse.SC_UNAUTHORIZED,
    		"User '" + credentials.getUsername() + "' does not exist or password incorrect"
    	);
    	return null;				
	}
	
	private static Method getLoginMethod(String name) {
    	try {
    		return PermissionManager.class.getDeclaredMethod(name, new Class<?>[] { AbstractSessionContext.class });
    	} catch (Throwable t) {
    		return null;
    	}
	}
	
	private UserSessionContext checkUserSessionContext(
		UserSessionContext userContext,
		HttpServletResponse response
	)
			throws IOException
	{
    	Method loginMethod = getLoginMethod("synchronizedLogin");
    	if (loginMethod == null) {
    		loginMethod = getLoginMethod("login");
    	}
        try {
        	loginMethod.invoke(null, new Object[] { userContext });
        } catch (Exception ite) {
        	setAuthenticationHeader(response);
        	sendError(
        		response,
        		HttpServletResponse.SC_UNAUTHORIZED,
        		"User '" + userContext.getUserId() + "' does not exist or password incorrect"
        	);
        	return null;				
        }
        return userContext;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean handleSecurity(
		HttpServletRequest request,
		HttpServletResponse response
	) throws IOException {
		try {
			LoginCredentials cred = checkLoginCredentials(request, response);
			if (cred == null) {
				return false;				
			}
			
			UserPermissionInfo upi = checkUserPermissionInfo(cred, response);
			if (upi == null) {
	        	return false;				
			}
			
			UserSessionContext sctx = checkUserSessionContext(
				new UserSessionContext(cred),
				response
			);
			
			if (sctx == null) {
				return false;
			}
			
			if (!checkWebServiceAccess(upi, response)) {
				return false;
			}
			request.setAttribute(UserPermissionInfo.class.getName(), upi);
			return true;
		} catch (Throwable t) {
			LOGGER.log(Level.SEVERE, "Unable to handle security", t);
			return false;
		}
	}
	
	private void setAuthenticationHeader(HttpServletResponse response) {
		response.setHeader(
			HEADER_WWW_AUTHENTICATE,
			HEADER_VALUE_WWW_AUTHENTICATE
		);
	}
	
	protected void sendError(
		HttpServletResponse response,
		int code,
		String message
	)
			throws IOException
	{
		if (response == null) {
			return;
		}
		response.setStatus(code);
		if (message == null) {
			response.sendError(code);
		} else {
			response.sendError(code, message);
		}
	}
	
	protected boolean checkPermission(
		UserPermissionInfo login,
		SessionReference sessionReference,
		Permission...permission
	) {
		if (login == null) {
			return true;
		}
		login.hasSystemPermission(sessionReference, permission);
		return true;
	}
	
	protected boolean checkPermissions(
		UserPermissionInfo login,
		HttpServletResponse response,
		Permission...permissions
	)
			throws IOException
	{
		if (permissions == null) {
			return true;
		}
		for (Permission permission : permissions) {
			if (permission == null) {
				continue;
			}
			if (login.hasPermission(permission)) {
				continue;
			}
			sendError(
				response,
				HttpServletResponse.SC_FORBIDDEN,
				"User '" + login.getUserId() + "' requires permission '" + permission.getId() + "'"
			);
			return false;
		}
		return true;
	}
	
	private boolean checkWebServiceAccess(
		UserPermissionInfo login,
		HttpServletResponse response
	)
			throws IOException
	{
		return checkPermissions(login, response, Permission.WebServiceAccess);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public URL getResource(String resource) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getMimeType(String resource) {
		return null;
	}

}
