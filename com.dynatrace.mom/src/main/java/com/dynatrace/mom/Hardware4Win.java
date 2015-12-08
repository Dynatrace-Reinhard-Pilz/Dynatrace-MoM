package com.dynatrace.mom;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

import com.dynatrace.utils.Closeables;


public class Hardware4Win {
	
	private static String sn = null;
	
	public static final String getSerialNumber() {
		if (sn != null) {
			return sn;
		}
		OutputStream os = null;
		InputStream is = null;
		
		Runtime runtime = Runtime.getRuntime();
		Process process = null;
		try {
			process = runtime.exec(new String[] { "wmic", "bios", "get", "serialnumber" });
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		os = process.getOutputStream();
		is = process.getInputStream();
		
		Closeables.closeQuietly(os);
		
		Scanner sc = null;
		try {
			sc = new Scanner(is);
			while (sc.hasNext()) {
				String next = sc.next();
				if ("SerialNumber".equals(next)) {
					sn = sc.next().trim();
					break;
				}
			}
		} finally {
			Closeables.closeQuietly(sc);
			Closeables.closeQuietly(is);
		}
		
		if (sn == null) {
			throw new RuntimeException("Cannot find computer SN");
		}
		
		return sn;
	}
}