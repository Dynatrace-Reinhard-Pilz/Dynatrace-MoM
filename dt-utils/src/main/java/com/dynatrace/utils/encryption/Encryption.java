package com.dynatrace.utils.encryption;

public final class Encryption {

	private Encryption() {
		// prevent instantiation
	}
	
	public static void encrypt(Encryptable e) {
		if (e == null) {
			return;
		}
		synchronized (e) {
			if (e.isEncrypted()) {
				return;
			}
			e.encrypt();
		}
	}
	
	public static void decrypt(Encryptable e) {
		if (e == null) {
			return;
		}
		synchronized (e) {
			if (!e.isEncrypted()) {
				return;
			}
			e.decrypt();
		}
	}
	
	public static boolean isEncrypted(Encryptable... e) {
		if (e == null) {
			return false;
		}
		for (Encryptable encryptable : e) {
			synchronized (encryptable) {
				if (!encryptable.isEncrypted()) {
					return false;
				}
			}
		}
		return true;
	}
}
