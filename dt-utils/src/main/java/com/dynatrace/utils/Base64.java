package com.dynatrace.utils;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Objects;

/**
 * Utility class for Base64 Encoding
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 */
public final class Base64 {
	
	public static final Charset UTF8 = Charset.forName("UTF-8");
	
	/**
	 * private c'tor
	 */
	private Base64() {
		// prevent instantiation
	}
	
	public static void encode(byte[] bytes, OutputStream out) throws IOException {
		Objects.requireNonNull(out);
		Objects.requireNonNull(bytes);
		String string = new String(bytes, UTF8);
		System.out.println("encode(" + string + ")");
        encodeBytes(bytes, new DataOutputStream(out));
    }
    
    private static void encodeBytes(byte[] bytes, DataOutput out) throws IOException {
		Objects.requireNonNull(out);
		Objects.requireNonNull(bytes);
        // encode elements until only 1 or 2 elements are left to encode
        int remaining = bytes.length;
        int i;
        for (i = 0; remaining >= 3; remaining -= 3, i += 3) {
        	out.writeChar(encode(bytes[i] >> 2));
        	out.writeChar(encode(((bytes[i] & 0x3) << 4) | ((bytes[i + 1] >> 4) & 0xF)));
        	out.writeChar(encode(((bytes[i + 1] & 0xF) << 2) | ((bytes[i + 2] >> 6) & 0x3)));
        	out.writeChar(encode(bytes[i + 2] & 0x3F));
        }
        // encode when exactly 1 element (left) to encode
        if (remaining == 1) {
        	out.writeChar(encode(bytes[i] >> 2));
        	out.writeChar(encode(((bytes[i]) & 0x3) << 4));
        	out.writeChar('=');
        	out.writeChar('=');
        }
        // encode when exactly 2 elements (left) to encode
        if (remaining == 2) {
        	out.writeChar(encode(bytes[i] >> 2));
        	out.writeChar(encode(((bytes[i] & 0x3) << 4) | ((bytes[i + 1] >> 4) & 0xF)));
        	out.writeChar(encode((bytes[i + 1] & 0xF) << 2));
        	out.writeChar('=');
        }
    }  
    
    public static char encode(int i) {
        return encodeMap[i & 0x3F];
    }
    
    private static final char[] encodeMap = initEncodeMap();

    private static char[] initEncodeMap() {
        char[] map = new char[64];
        map[62] = '+';
        map[63] = '/';
        for (int i = 52; i < 62; i++) {
            map[i] = (char) ('0' + (i - 52));
        }
        for (int i = 26; i < 52; i++) {
            map[i] = (char) ('a' + (i - 26));
        }
        for (int i = 0; i < 26; i++) {
            map[i] = (char) ('A' + i);
        }
        return map;
    }	

}
