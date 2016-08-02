package com.dynatrace.http.config;

import org.junit.Assert;
import org.junit.Test;


public class DesEncrypterTest {
	
	private static final DesEncrypter USER_DESCRYPTER = new DesEncrypter("USER_DESCRYPTER", "useruseruseruseruseruseruseruseruseruseruser");
	private static final DesEncrypter PASS_DESCRYPTER = new DesEncrypter("PASS_DESCRYPTER", "passpasspasspasspasspasspasspasspasspasspass");

	@Test
	public void testEnDeCrypt() {
		check(USER_DESCRYPTER, "dynaTrace");
		check(PASS_DESCRYPTER, "labpass");
	}
	
	private static void check(DesEncrypter c, String decrypted) {
		String encrypted = c.encrypt(decrypted);
		Assert.assertEquals(decrypted, c.decrypt(encrypted));
	}
}
