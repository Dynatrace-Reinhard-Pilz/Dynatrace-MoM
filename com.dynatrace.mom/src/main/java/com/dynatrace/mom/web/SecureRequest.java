package com.dynatrace.mom.web;

import java.io.IOException;
import java.security.Principal;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Path;
import javax.ws.rs.core.HttpHeaders;

import com.dynatrace.mom.HttpUtils;
import com.dynatrace.utils.Strings;
import com.dynatrace.utils.ToString;

public final class SecureRequest extends HttpServletRequestWrapper {
	
	private static final Logger LOGGER =
			Logger.getLogger(SecureRequest.class.getName());

	private static final String PARAM_USER = "login".intern();
	private static final String PARAM_PASS = "password".intern();
	
	private static final boolean AUTOLOGIN = true;

	private static final String LOGIN_PATH =
			Strings.SLASH +
			AuthenticationPages.class.getAnnotation(Path.class).value();
	
	
	/**
	 * <p>
	 * The session attribute holding the name of the authenticated user.
	 * </p><p>
	 * If this attribute is found within the current HTTP Session the
	 * HTTP client is considered to be authenticated.
	 * </p>
	 */
	private static final String ATTR_USER =
			SecureRequest.class.getName() + PARAM_USER;
	
	/**
	 * <p>
	 * In case the user has not been authenticated yet this session attribute
	 * holds the HTTP Request URI of the page the user wanted to visit initially
	 * </p><p>
	 * Upon successful authentication the HTTP client will be redirected to
	 * this URI
	 * </p> 
	 */
	private static final String ATTR_LOCATION =
			AuthenticationFilter.class.getName() + HttpHeaders.LOCATION;
	
	
	public SecureRequest(final HttpServletRequest request) {
		super(request);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void login(
			final String username,
			final String password
	) throws ServletException {
		setLoggedInUser(username);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean isUserInRole(final String role) {
		// we are not checking security roles for the moment
		return true;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String getRemoteUser() {
		final UserPrincipal principal = getUserPrincipal();
		if (principal == null) {
			return null;
		}
		return principal.getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final UserPrincipal getUserPrincipal() {
		synchronized (ATTR_USER) {
			final HttpSession session = getSession(true);
			if (session == null) {
				LOGGER.log(Level.FINER, "   >> session: " + session);
				return null;
			}
			LOGGER.log(Level.FINER, "   >> sessionid: " + session.getId());
			return (UserPrincipal) session.getAttribute(ATTR_USER);
		}
	}
	
	@Override
	public String getParameter(String name) {
		if (AUTOLOGIN && PARAM_USER.equals(name)) {
			return "admin";
		}
		if (AUTOLOGIN && PARAM_PASS.equals(name)) {
			return "admin";
		}
		return super.getParameter(name);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean authenticate(HttpServletResponse response)
		throws IOException, ServletException
	{
		if (isLoginPage()) {
			response.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache");
			final String user = getParameter(PARAM_USER);
			final String pass = getParameter(PARAM_PASS);
			if (isLoggedIn()) {
				if ((user == null) || (pass == null)) {
					clearLoggedInUser();
					return true;
				}
			} else {
				if ((user != null) && (pass != null)) {
					login(user, pass);
//					LOGGER.log(Level.INFO, "response.sendRedirect(" + getLoginSuccessRedirectURI() + ");");
					HttpUtils.sendRedirect(this, response, getLoginSuccessRedirectURI());
					clearLocation();
					return false;
				}
			}
		} else if(!isLoggedIn()) {
//			LOGGER.log(Level.INFO, "!isLoggedIn()");
			setLocation();
			HttpUtils.sendRedirect(this, response, getLoginURI());
			return false;
		}
		return true;
	}
	
	private final boolean isLoggedIn() {
		boolean isLoggedIn = (getUserPrincipal() != null);
		LOGGER.log(Level.FINER, "...isLoggedIn(): " + isLoggedIn);
		return isLoggedIn;
	}
	
	private final void setLoggedInUser(final String user) {
//		LOGGER.log(Level.INFO, "...setLoggedInUser(" + user + ")");
		synchronized (ATTR_USER) {
			if (user == null) {
				clearLoggedInUser();
				return;
			}
			if (isLoggedIn()) {
				LOGGER.log(Level.FINER, "  ...already logged in");
				return;
			}
			HttpSession session = getSession(true);
//			LOGGER.log(Level.INFO, "   >> sessionid: " + session.getId());
			session.setAttribute(ATTR_USER, new UserPrincipal(user));
		}
	}
	
	@Override
	public HttpSession getSession(boolean create) {
		HttpSession session = super.getSession(false);
		if (session != null) {
			return session;
		}
		return super.getSession(create);
	}
	
	private final void clearLoggedInUser() {
		synchronized (ATTR_USER) {
//			LOGGER.log(Level.INFO, "...clearLoggedInUser()");
			final HttpSession session = getSession(true);
			if (session == null) {
				return;
			}
			session.removeAttribute(ATTR_USER);
		}
	}
	
	/**
	 * Queries within the current HTTP Session for the initial HTTP Request URI
	 * the HTTP Client requested before having been been redirected to the login
	 * page.
	 * 
	 * @return the URI of the initial URI requested by the HTTP client before
	 * 		having been redirected to the login page or {@code null} if that
	 * 		URI never has been stored in the current HTTP Session
	 * 
	 * @throws NullPointerException if argument {@code request} is {@code null}
	 * 
	 * @see #ATTR_LOCATION
	 * @see #setLocation()
	 * @see #clearLocation()
	 */
	private final String getLocation() {
		LOGGER.log(Level.FINER, "...getLocation()");
		synchronized (ATTR_LOCATION) {
			final HttpSession session = getSession(true);
			if (session == null) {
				LOGGER.log(Level.FINER, "  ... location: null");
				return null;
			}
			LOGGER.log(Level.FINER, "  ... location: " + session.getAttribute(ATTR_LOCATION));
			return (String) session.getAttribute(ATTR_LOCATION);
		}
	}
	
	/**
	 * Stores the current HTTP Request URI within the current HTTP Session
	 * unless it does not already contain that information.
	 * 
	 * @param request the current HTTP Request
	 * 
	 * @throws NullPointerException if argument {@code request} is {@code null}
	 * 
	 * @see #ATTR_LOCATION
	 * @see #getLocation()
	 * @see #clearLocation()
	 */
	private final void setLocation() {
//		LOGGER.log(Level.INFO, "...setLocation()");
		synchronized (ATTR_LOCATION) {
			if (getLocation() != null) {
				LOGGER.log(Level.FINER, "  ...already defined as " + getLocation());
				return;
			}
			final String location = Strings.join(
					Strings.QUERY,
					getRequestURI(),
					getQueryString()
			);
//			LOGGER.log(Level.INFO, "  ... setting to " + location);
			getSession(true).setAttribute(ATTR_LOCATION, location);
		}
	}
	
	/**
	 * Removes any previously stored information within the current HTTP Session
	 * about the initial HTTP Request URI the HTTP Client requested before
	 * it was redirected to the login page.
	 *  
	 * @param request the current HTTP Request
	 * 
	 * @throws NullPointerException if argument {@code request} is {@code null}
	 * 
	 * @see #ATTR_LOCATION
	 * @see #getLocation(HttpServletRequest)
	 * @see #setLocation(HttpServletRequest)
	 */
	private final void clearLocation() {
		LOGGER.log(Level.FINER, "...clearLocation()");
		final HttpSession session = getSession(true);
		if (session == null) {
			return;
		}
		synchronized (ATTR_LOCATION) {
			session.removeAttribute(ATTR_LOCATION);
		}
	}
	
	/**
	 * Constructs the HTTP Request URI of the login page
	 * 
	 * @return the HTTP Request URI of the login page
	 */
	private final String getLoginURI() {
		return LOGIN_PATH;
	}
	
	/**
	 * Constructs the HTTP Request URI of recommended landing page of the
	 * Web Application.
	 * 
	 * @return the HTTP Request URI of the recommended landing page of the
	 * Web Application.
	 */
	public final String getHomeURI() {
		return "/servers";
	}
	
	/**
	 * Checks if the HTTP Client is requesting the login page.
	 * 
	 * @return {@code true} if the currently requested page is the login page,
	 * 		{@code false} otherwise.
	 * 
	 * @throws NullPointerException if argument {@code request} is {@code null}
	 */
	public final boolean isLoginPage() {
		return isLoginPage(getRequestURI());
	}
	
	/**
	 * Checks if the given HTTP Request URI is identical to the login page
	 * 
	 * @param requestURI the URI to check if it is the login page
	 * 
	 * @return {@code true} if the given HTTP Request URI represents the login
	 * 		page, {@code false} otherwise.
	 */
	private final boolean isLoginPage(final String requestURI) {
		LOGGER.log(Level.INFO, "...isLoginPage(" + requestURI + ")");
		if (requestURI == null) {
			LOGGER.log(Level.INFO, "  ...false");
			return false;
		}
		boolean result = Strings.equals(requestURI, getLoginURI());
		LOGGER.log(Level.INFO, "  ..." + result);
		return result;
	}
	
	private final String getLoginSuccessRedirectURI() {
		LOGGER.log(Level.INFO, "...getLoginSuccessRedirectURI()");
		final String location = getLocation();
		if ((location == null) || isLoginPage(location)) {
			LOGGER.log(Level.INFO, "  ..." + getHomeURI());
			return getHomeURI();
		}
		LOGGER.log(Level.INFO, "  ..." + location);
		return location;
	}
	
	
	@SuppressWarnings("unused")
	private void log(final String msg) {
		if (msg == null) {
			return;
		}
		getServletContext().log(msg);
	}
	
	private static class UserPrincipal implements Principal {
		
		private final String name;
		
		public UserPrincipal(final String name) {
			this.name = name;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getName() {
			return name;
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return new ToString(this).append(name).toString();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
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
			final UserPrincipal other = (UserPrincipal) obj;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}
		
	}
}
