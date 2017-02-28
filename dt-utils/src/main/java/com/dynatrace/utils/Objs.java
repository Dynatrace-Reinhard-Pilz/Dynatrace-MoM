package com.dynatrace.utils;

public class Objs {

	public static boolean isNull(Object o) {
		return o == null;
	}
	
	public static boolean isEitherNull(Object a, Object b) {
		return (a == null) || (b == null);
	}
	
	public static boolean isEitherNull(Object a, Object b, Object c) {
		return (a == null) || (b == null) || (c == null);
	}
	
}
