package com.dynatrace.utils.encryption;

public interface Encryptable {

	void encrypt();
	void decrypt();
	boolean isEncrypted();
}
