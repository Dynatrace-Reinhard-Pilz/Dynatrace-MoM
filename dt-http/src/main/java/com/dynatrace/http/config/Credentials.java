package com.dynatrace.http.config;

import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.dynatrace.utils.Strings;
import static com.dynatrace.utils.Strings.isNullOrEmpty;

/**
 * A configuration object holding user credentials
 * 
 * @author Reinhard Pilz
 *
 */
@XmlRootElement(name = Credentials.TAG)
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(
		propOrder = {
				Credentials.ATTRIBUTE_USER,
				Credentials.ATTRIBUTE_PASS
		}
)
public final class Credentials {
	
	private static final String ERR_MSG_NO_USERNAME =
			"credentials do not contain a username".intern();
	private static final String ERR_MSG_NO_PASSWORD =
			"credentials do not contain a password".intern();
	
	public static final String TAG = "credentials";
	static final String ATTRIBUTE_USER = "user";
	static final String ATTRIBUTE_PASS = "pass";

	private String user = null;
	private String pass = null;
	
	/**
	 * c'tor
	 */
	public Credentials() {
	}
	
	/**
	 * c'tor
	 * 
	 * @param user the user name
	 * @param pass the password
	 */
	public Credentials(String user, String pass) {
		this.user = user;
		this.pass = pass;
	}
	
	/**
	 * @return the user name for authentication
	 */
	@XmlAttribute(name = Credentials.ATTRIBUTE_USER)
	public String getUser() {
		return user;
	}
	
	/**
	 * Sets the user name for authentication
	 * 
	 * @param user the user name for authentication
	 */
	public void setUser(String user) {
		this.user = user;
	}
	
	/**
	 * @return the plain text password for authentication
	 */
	@XmlAttribute(name = Credentials.ATTRIBUTE_PASS)
	public String getPass() {
		return pass;
	}
	
	/**
	 * Sets the plain text password for authentication
	 * 
	 * @param pass the plain text password for authentication
	 */
	public void setPass(String pass) {
		this.pass = pass;
	}
	
	/**
	 * Checks if both the configured user name and password are not {@code null}
	 * and not empty.
	 * 
	 * @return {@code true} if both, the user name and the password are not
	 * 		{@code null} and not empty, {@code false} otherwise
	 */
	@XmlTransient
	public boolean isValid() {
		if (isNullOrEmpty(user)) {
			return false;
		}
		if (isNullOrEmpty(pass)) {
			return false;
		}
		return true;
	}
	
	/**
	 * Checks if the given {@link Credentials} is not {@code null} and is
	 * valid according to the rules defined by {@link #isValid()}.
	 * 
	 * @param credentials the {@link Credentials} to check for validity
	 * 
	 * @return {@code true} if the given {@link Credentials} is not {@code null}
	 * 		and is valid, {@code false} otherwise.
	 */
	public static boolean isValid(Credentials credentials) {
		if (credentials == null) {
			return false;
		}
		return credentials.isValid();
	}
	
	/**
	 * Encodes the configured user name and password to using a BASE64 Encoder
	 * to be used for Basic Authentication (HTTP).
	 * 
	 * @return the encoded user credentials
	 * 
	 * @throws IllegalArgumentException if either the user name or the password
	 * 		are {@code null} or empty
	 */
	public String encode() {
		if (isNullOrEmpty(user)) {
			throw new IllegalArgumentException(ERR_MSG_NO_USERNAME);
		}
		if (isNullOrEmpty(pass)) {
			throw new IllegalArgumentException(ERR_MSG_NO_PASSWORD);
		}
		final String userPassword =	user + Strings.COLON + pass;
		return DatatypeConverter.printBase64Binary(userPassword.getBytes());
	}
	
}
