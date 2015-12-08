package com.dynatrace.http;

import java.util.UUID;

import javax.xml.bind.DatatypeConverter;

import org.junit.Assert;
import org.junit.Test;

import com.dynatrace.http.config.Credentials;
import com.dynatrace.utils.Strings;

/**
 * Tests for class {@link Credentials}
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 */
public class CredentialsTest {
	
	@Test
	public void testDefaultCtor() {
		Credentials credentials = new Credentials();
		Assert.assertNull(credentials.getUser());
		Assert.assertNull(credentials.getPass());
	}

	@Test
	public void testNonEmptyCtor() {
		String user = UUID.randomUUID().toString();
		String pass = UUID.randomUUID().toString();
		Credentials credentials = new Credentials(user, pass);
		Assert.assertEquals(user, credentials.getUser());
		Assert.assertEquals(pass, credentials.getPass());
	}
	
	@Test
	public void testSetUser() {
		Credentials credentials = new Credentials();
		String user = UUID.randomUUID().toString();
		credentials.setUser(user);
		Assert.assertEquals(user, credentials.getUser());
		credentials.setUser(null);
		Assert.assertNull(credentials.getUser());
	}
	
	@Test
	public void testSetPass() {
		Credentials credentials = new Credentials();
		String pass = UUID.randomUUID().toString();
		credentials.setPass(pass);
		Assert.assertEquals(pass, credentials.getPass());
		credentials.setPass(null);
		Assert.assertNull(credentials.getPass());
	}
	
	@Test
	public void testIsValid() {
		Assert.assertFalse(Credentials.isValid(null));
		
		Credentials credentials = new Credentials();
		Assert.assertFalse(credentials.isValid());
		Assert.assertFalse(Credentials.isValid(credentials));
		
		credentials.setUser(Strings.EMPTY);
		Assert.assertFalse(credentials.isValid());
		Assert.assertFalse(Credentials.isValid(credentials));
		
		credentials.setPass(Strings.EMPTY);
		Assert.assertFalse(credentials.isValid());
		Assert.assertFalse(Credentials.isValid(credentials));

		credentials.setUser(null);
		Assert.assertFalse(credentials.isValid());
		Assert.assertFalse(Credentials.isValid(credentials));

		credentials.setUser(UUID.randomUUID().toString());
		credentials.setPass(null);
		Assert.assertFalse(credentials.isValid());
		Assert.assertFalse(Credentials.isValid(credentials));

		credentials.setUser(null);
		credentials.setPass(UUID.randomUUID().toString());
		Assert.assertFalse(credentials.isValid());
		Assert.assertFalse(Credentials.isValid(credentials));

		credentials.setUser(UUID.randomUUID().toString());
		credentials.setPass(Strings.EMPTY);
		Assert.assertFalse(credentials.isValid());
		Assert.assertFalse(Credentials.isValid(credentials));

		credentials.setUser(Strings.EMPTY);
		credentials.setPass(UUID.randomUUID().toString());
		Assert.assertFalse(credentials.isValid());
		Assert.assertFalse(Credentials.isValid(credentials));

		credentials.setUser(UUID.randomUUID().toString());
		credentials.setPass(UUID.randomUUID().toString());
		Assert.assertTrue(credentials.isValid());
		Assert.assertTrue(Credentials.isValid(credentials));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testEncodeInvalidUserAndPass() {
		new Credentials(null, null).encode();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testEncodeInvalidUser() {
		new Credentials(null, UUID.randomUUID().toString()).encode();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testEncodeInvalidPass() {
		new Credentials(UUID.randomUUID().toString(), null).encode();
	}
	
	@Test
	public void testEncode() {
		String user = UUID.randomUUID().toString();
		String pass = UUID.randomUUID().toString();
		Credentials credentials = new Credentials(user, pass);
		String encoded = credentials.encode();
		String userPass = user + Strings.COLON + pass;
		String expected = DatatypeConverter.printBase64Binary(
			userPass.getBytes()
		); 
		Assert.assertNotNull(encoded);
		Assert.assertEquals(expected, encoded);
	}
}
