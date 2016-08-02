package com.dynatrace.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Extract {

	public static String extract(InputStream in, String prefix, String postfix) throws IOException {
		if (in == null) {
			return null;
		}
		byte[] preFixBytes = getBytes(prefix);
		byte[] postFixBytes = getBytes(postfix);
		int preFixIdx = 0;
		int read = 0;
		while (preFixIdx != preFixBytes.length) {
			read = in.read();
			System.out.print((char) read + ",");
			if (read == -1) {
				return null;
			}
			if (preFixBytes[preFixIdx] == read) {
				preFixIdx++;
			}
		}
		read = in.read();
		if (read == -1) {
			return null;
		}
		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			int postFixIdx = 0;
			while (postFixIdx != postFixBytes.length) {
				out.write(read);
				if (postFixBytes[postFixIdx] == read) {
					postFixIdx++;
				} else {
					postFixIdx = 0;
				}
				read = in.read();
				if (read == -1) {
					break;
				}
			}
			if (postFixIdx != postFixBytes.length) {
				return null;
			}
			byte[] bytes = out.toByteArray();
			return new String(bytes, 0, bytes.length - postFixBytes.length);
		}
	}
	
	private static byte[] getBytes(String s) {
		if (s == null) {
			return new byte[0];
		}
		return s.getBytes();
	}
}
