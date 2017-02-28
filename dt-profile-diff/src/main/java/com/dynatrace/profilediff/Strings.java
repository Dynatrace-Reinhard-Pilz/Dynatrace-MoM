package com.dynatrace.profilediff;

public class Strings {
	
	public static boolean equals(String a, String b) {
		boolean result = equals0(a, b);
		// Log.info("Strings.equals(" + toString(a, b) + "): " + result);
		return result;
	}

	public static boolean equals0(String a, String b) {
		if (a == null) {
			return b == null;
		}
		if (b == null) {
			return false;
		}
		return a.equals(b);
	}
	
	public static String toString(Object... objects) {
		if (objects == null) {
			return null;
		}
		if (objects.length == 0) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		append(sb, objects[0]);
		for (int i = 1; i < objects.length; i++) {
			sb.append(", ");
			append(sb, objects[i]);
		}
		return sb.toString();
	}
	
	private static void append(StringBuilder sb, Object o) {
		if (o == null) {
			sb.append("null");
		} else if (o instanceof String) {
			sb.append("\"").append((String) o).append("\"");
		} else {
			sb.append(o.toString());
		}
	}
}
