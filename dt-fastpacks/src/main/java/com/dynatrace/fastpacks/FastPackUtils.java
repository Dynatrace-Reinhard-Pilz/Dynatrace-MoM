package com.dynatrace.fastpacks;

/**
 * Utilities for Fast Pack creation and validation
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 */
public class FastPackUtils {

	/**
	 * Checks if the given name is valid for a Fast Pack.<br />
	 * <br />
	 * Valid characters are alphanumeric characters, {@code -} and {@code .}
	 * 
	 * @param name the name to validate
	 * 
	 * @return {@code true} if the given {@link String} only contains valid
	 * 		characters for a Fast Pack, {@code false} otherwise
	 */
	public static boolean isValidName(String name) {
		if (name == null) {
			return false;
		}
		byte[] bytes = name.getBytes();
		for (byte b : bytes) {
			if (!Character.isAlphabetic(b) && (b != '.') && (b != '-')) {
				return false;
			}
		}
		return true;
	}
}
