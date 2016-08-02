/*****************************************************
 *  dynaTrace diagnostics (c) dynaTrace software GmbH
 *
 * @file: DesEncrypter.java
 * @date: 01.06.2006
 * @author: hackl
 *
 */
package com.dynatrace.http.config;

import java.security.spec.KeySpec;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.xml.bind.DatatypeConverter;

import com.dynatrace.utils.Base64;

/**
 * @author hackl
 *
 */
public class DesEncrypter {
	private static final String BLANK = "";
	private Cipher ecipher;
	private Cipher dcipher;
//	private static final String PASSPHRASE = "pretty nice passphrase $007";
	private static final Logger log = Logger.getLogger(DesEncrypter.class.getName());
	private final String id;

	public DesEncrypter(String id, String passPhrase) {
		this.id = id;
		try {
			// Create the key
			// KeySpec keySpec = new DESKeySpec((PASSPHRASE+ passPhrase).getBytes());
			KeySpec keySpec = new DESKeySpec((passPhrase).getBytes());
			SecretKey key = SecretKeyFactory.getInstance("DES").generateSecret(keySpec);
			ecipher = Cipher.getInstance(key.getAlgorithm());
			dcipher = Cipher.getInstance(key.getAlgorithm());

			// Create the ciphers
			ecipher.init(Cipher.ENCRYPT_MODE, key);
			dcipher.init(Cipher.DECRYPT_MODE, key);

		} catch (Exception e) {
			if (log.isLoggable(Level.WARNING)) log.log(Level.WARNING, "cannot instantiate encrypter", e);
		}
	}
	

	/**
	 * @param str
	 * @return may return null if encrypt fails
	 * @author hackl
	 */
	public String encrypt (String str) {
		if (str == null) {
			return null;
		}
		if (str.isEmpty()) {
			return BLANK;
		}
		String decrypted = str;
		try {
            // Encode the string into bytes using utf-8
            byte[] utf8 = str.getBytes(Base64.UTF8);

            // Encrypt
            byte[] enc = ecipher.doFinal(utf8);
            String encrypted = DatatypeConverter.printBase64Binary(enc);
            // log.info(" ---- [" + id + "] ENCRYPTED '" + decrypted + "' to '" + encrypted + "'");
            return encrypted;
		}
		catch (Exception e) {
			if (log.isLoggable(Level.WARNING)) log.log(Level.WARNING, "[" + id + "] cannot encrypt", e);
			return null;
		}
	}

	/**
	 * Encrypt the given byte-array and return the resulting encrypted block.
	 *
	 * @param bytes An array of bytes to encrypt.
	 * @return The encrypted array of bytes or null if an error occurs while encrypting.
	 */
	public byte[] encrypt (byte[] bytes) {
		try {
			return ecipher.doFinal(bytes);
		} catch (Exception e) {
			if (log.isLoggable(Level.WARNING)) log.log(Level.WARNING, "[" + id + "] cannot encrypt", e);
		}
		return null;
	}

	/**
	 * @param str
	 * @return may return null if decrypt fails
	 * @author hackl
	 */
	public String decrypt(String str) {
		if (str == null) {
			return null;
		}
		if (str.isEmpty()) {
			return BLANK;
		}
		String encrypted = str;
		int blockSize = 0;
		byte[] dec = null;
		try {
            // Decode base64 to get bytes
			dec = DatatypeConverter.parseBase64Binary(str);

            blockSize = dcipher.getBlockSize();
            if (blockSize > 0) {
            	if (dec.length % blockSize != 0) {
            		if (log.isLoggable(Level.WARNING))
            			log.log(Level.WARNING, "[" + id + "] cannot decrypt '" + str + "' due to invalid block size.", new Exception());
            		
            		return null;
            	}
            }

            // Decrypt
            byte[] utf8 = dcipher.doFinal(dec);

            // Decode using utf-8
            String decrypted = new String(utf8, "UTF8");
            // log.info(" ---- [" + id + "] DECRYPTED '" + encrypted + "' to '" + decrypted + "'");
            return decrypted;
		}
		catch (Exception e) {
			if (log.isLoggable(Level.WARNING))
				log.log(Level.WARNING, "[" + id + "] cannot decrypt", e);
			if (log.isLoggable(Level.INFO))
				log.log(Level.INFO, "[" + id + "] value '" + str + "' blocksize: " + blockSize + " input length: " + (dec == null ? "-" : dec.length));
		}
		return null;
	}

	/**
	 * Decrypt the given byte-array and return the resulting plain-text block.
	 *
	 * @param bytes An array of encrypted bytes.
	 * @return The array of plain-text bytes or null if an error occurs while decrypting.
	 */
	public byte[] decrypt(byte[] bytes) {
		try {
            return dcipher.doFinal(bytes);
		} catch (Exception e) {
			if (log.isLoggable(Level.WARNING)) log.log(Level.WARNING, "cannot decrypt", e);
		}
		return null;
	}
}
