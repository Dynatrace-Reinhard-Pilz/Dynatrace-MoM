package com.dynatrace.utils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;


public final class Base64Output implements AutoCloseable {
	
	private final DataOutputStream out;
	private final byte[] buf = new byte[3];
	private byte ptr = -1;
	
	public Base64Output(OutputStream out) {
		Objects.requireNonNull(out);
		this.out = new DataOutputStream(out);
	}

	public void write(byte b) throws IOException {
		ptr++;
		buf[ptr] = b;
		if (ptr == 2) {
			encode3();
			ptr = -1;
		} 
	}
	
	public void write(InputStream in, int len) throws IOException {
		for (int i = 0; i < len; i++) {
			ptr++;
			buf[ptr] = (byte) in.read();
			if (ptr == 2) {
				encode3();
				ptr = -1;
			} 
		}
		if (ptr == 0) {
			encode1();
		} else if (ptr == 1) {
			encode2();
		}
		out.flush();
	}
	
	private void encode3() throws IOException {
    	out.writeChar(Base64.encode(buf[0] >> 2));
    	out.writeChar(Base64.encode(((buf[0] & 0x3) << 4) | ((buf[1] >> 4) & 0xF)));
    	out.writeChar(Base64.encode(((buf[1] & 0xF) << 2) | ((buf[2] >> 6) & 0x3)));
    	out.writeChar(Base64.encode(buf[2] & 0x3F));
	}
	
	private void encode2() throws IOException {
    	out.writeChar(Base64.encode(buf[0] >> 2));
    	out.writeChar(Base64.encode(((buf[0] & 0x3) << 4) | ((buf[1] >> 4) & 0xF)));
    	out.writeChar(Base64.encode((buf[1] & 0xF) << 2));
    	out.writeChar('=');
	}
	
	private void encode1() throws IOException {
    	out.writeChar(Base64.encode(buf[0] >> 2));
    	out.writeChar(Base64.encode(((buf[0]) & 0x3) << 4));
    	out.writeChar('=');
    	out.writeChar('=');
	}

	@Override
	public void close() throws IOException {
		if (ptr == 0) {
			encode1();
		} else if (ptr == 1) {
			encode2();
		}
		out.flush();
	}
}
