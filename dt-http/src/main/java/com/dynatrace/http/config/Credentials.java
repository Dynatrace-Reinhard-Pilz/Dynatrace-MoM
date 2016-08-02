package com.dynatrace.http.config;

import static com.dynatrace.utils.Strings.isNullOrEmpty;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.dynatrace.authentication.Authenticator;
import com.dynatrace.pluggability.PluginManager;
import com.dynatrace.utils.Base64;
import com.dynatrace.utils.Base64Output;
import com.dynatrace.utils.Strings;
import com.dynatrace.utils.encryption.Encryptable;

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
				Credentials.ATTRIBUTE_PASS,
				Credentials.ATTRIBUTE_ENCRYPTED
		}
)
public final class Credentials implements Authenticator, Cloneable, Encryptable {
	
	
	
	private static final Logger LOGGER =
			Logger.getLogger(Credentials.class.getName());
	
	private static final String ERR_MSG_NO_USERNAME =
			"credentials do not contain a username".intern();
	private static final String ERR_MSG_NO_PASSWORD =
			"credentials do not contain a password".intern();
	
	public static final String TAG = "credentials";
	static final String ATTRIBUTE_USER = "user";
	static final String ATTRIBUTE_PASS = "pass";
	static final String ATTRIBUTE_ENCRYPTED = "encrypted";

	private String user = "username";
	private String pass = "password";

	private volatile boolean isEncrypted = false;
	
	private static final DesEncrypter USER_DESCRYPTER = new DesEncrypter("USER_DESCRYPTER", "useruseruseruseruseruseruseruseruseruseruser");
	private static final DesEncrypter PASS_DESCRYPTER = new DesEncrypter("PASS_DESCRYPTER", "passpasspasspasspasspasspasspasspasspasspass");
	
	public void setEncrypted(boolean isEncrypted) {
		this.isEncrypted = isEncrypted;
	}
	
	@XmlAttribute(name = Credentials.ATTRIBUTE_ENCRYPTED, required = false)
	public boolean isEncrypted() {
		return isEncrypted;
	}
	
	@Override
	public void encrypt() {
		synchronized (this) {
			if (isEncrypted) {
				return;
			}
			this.user = USER_DESCRYPTER.encrypt(this.user);
			this.pass = PASS_DESCRYPTER.encrypt(this.pass);
			isEncrypted = true;
		}
	}
	
	@Override
	public void decrypt() {
		synchronized (this) {
			if (!isEncrypted) {
				return;
			}
			this.user = USER_DESCRYPTER.decrypt(this.user);
			this.pass = PASS_DESCRYPTER.decrypt(this.pass);
			isEncrypted = false;
		}
	}
	
	
	
	private final static PluginManager PLUGINMGR = PluginManager.get(
		Credentials.class
	);
	
	private final static Iterable<Authenticator> AUTHS = findAuths();
	
	private static Iterable<Authenticator> findAuths() {
		Class<? extends Authenticator>[] classes =
				PLUGINMGR.getImplementors(Authenticator.class);
		Collection<Authenticator> auths = new ArrayList<>();
		for (Class<? extends Authenticator> clazz : classes) {
			Authenticator auth = createAuth(clazz);
			if (auth != null) {
				LOGGER.log(
					Level.INFO,
					"Registering " + auth.getClass().getName()
				);
				auths.add(auth);
			}
		}
		return auths;
	}
	
	private static Authenticator createAuth(
		Class<? extends Authenticator> clazz
	) {
		if (clazz == null) {
			return null;
		}
		int modifiers = clazz.getModifiers();
		if (Modifier.isAbstract(modifiers)) {
			return null;
		}
		if (Modifier.isInterface(modifiers)) {
			return null;
		}
		try {
			Constructor<? extends Authenticator> ctor =
				clazz.getDeclaredConstructor(new Class<?>[0]);
			ctor.setAccessible(true);
			return ctor.newInstance();
		} catch (Throwable t) {
			LOGGER.log(
				Level.WARNING,
				"Unable to create instance of " + clazz.getName()
			);
			return null;
		}
	}
	
	
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
	 * {@inheritDoc}
	 */
	@Override
	public boolean encode(URL url, OutputStream out) throws IOException {
		for (Authenticator auth : AUTHS) {
			if (auth.encode(url, out)) {
				LOGGER.log(Level.FINEST, "Successfully authenticated via " + auth);
				return true;
			}
		}
		encode0(out);
		LOGGER.log(Level.FINEST, "Fallback authenticated via " + user + "/" + pass);
		return true;
	}
	
	private void encode0(OutputStream out) throws IOException {
		if (isNullOrEmpty(user)) {
			throw new IllegalArgumentException(ERR_MSG_NO_USERNAME);
		}
		if (isNullOrEmpty(pass)) {
			throw new IllegalArgumentException(ERR_MSG_NO_PASSWORD);
		}
//		LOGGER.log(Level.INFO, "user: " + user);
//		LOGGER.log(Level.INFO, "pass: " + pass);
		final String userPassword =	user + Strings.COLON + pass;
		try (
			Base64Output base64Out = new Base64Output(out);
			InputStream in = new ByteArrayInputStream(
				userPassword.getBytes(Base64.UTF8)
			);
		) {
			base64Out.write(in, in.available());
		}
	}
	
	@Override
	public Credentials clone() {
		try {
			return (Credentials) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError(e.getMessage());
		}
	}
	
}
