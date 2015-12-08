package com.dynatrace.tasks.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Convenience methods for dealing with {@link String}s
 * 
 * @author Reinhard Pilz
 *
 */
public final class Strings {
	
	public static final String EMPTY = "".intern();
	public static final String COLON = ":".intern();
	public static final String SPC = " ".intern();
	public static final String EQ = "=".intern();
	public static final String DOT = ".".intern();
	public static final String SLASH = "/".intern();
	public static final String QUERY = "?".intern();
	public static final String COMMA = ", ".intern();
	public static final String PLUS = "+".intern();
	public static final String ENCSPC = "%20".intern();
	
	public static final String UTF8 = StandardCharsets.UTF_8.name(); 
	
	/**
	 * Encodes the given {@link String} in {@code UTF-8} but instead of using
	 * {@code +} as a replacement for white spaces it uses {@code %20}.<br />
	 * <br />
	 * Encoding {@code null} does not fail in a {@link NullPointerException} but
	 * simply results in {@code null} as return value.
	 *  
	 * @param s the {@link String} to encode
	 * 
	 * @return the encoded {@link String} as {@code UTF-8} with white spaces
	 * 		replaced by {@code %20} or and empty {@link String} if the
	 * 		{@link String} to encode was {@code null}.
	 */
	public static String encode(String s) {
		if (s == null) {
			return EMPTY;
		}
		return urlEncode(s, UTF8).replace(PLUS, ENCSPC);
	}
	
	/**
	 * Calls {@link URLEncoder#encode(String, String)} assuming that the given
	 * encoding is indeed supported.<br />
	 * <br />
	 * Since this method is supposed be be called with {@code UTF-8} as encoding
	 * anyways a {@link UnsupportedEncodingException} is unlikely. In case it
	 * is getting thrown nevertheless, there is something severely wrong and
	 * therefore an {@link InternalError} will be thrown instead.<br />
	 * <br />
	 *  
	 * @param s the {@link String} to encode using the given encoding
	 * @param enc the encoding to use to encode the given {@link String}
	 * 
	 * @return the encoded {@link String} or an empty {@link String} if the
	 * 		passed {@link String} to this method was {@code null}.
	 */
	private static String urlEncode(String s, String enc) {
		if (s == null) {
			return EMPTY;
		}
		try {
			return URLEncoder.encode(s, enc);
		} catch (UnsupportedEncodingException e) {
			throw new InternalError(e.getMessage());
		}
	}
	
	/**
	 * Ensures that the returned {@link String} is not {@code null} but rather
	 * an empty {@link String}.
	 * 
	 * @param s the {@link String} to check for whether it is {@code null}
	 * @return the unmodified {@link String} passed to this method or en empty
	 * 		{@link String} if the argument passed to this method was
	 * 		{@code null}.
	 */
	public static String ensureNotNull(String s) {
		if (s == null) {
			return EMPTY;
		}
		return s;
	}

	/**
	 * Checks if the given {@link String} is either {@code null} or an empty
	 * {@link String}.
	 * 
	 * @param s the {@link String} to check if it is either {@code null} or an
	 * 		empty {@link String}
	 * 
	 * @return {@code true} if the given {@link String} is either {@code null}
	 * 		or an empty {@link String}, {@code false} otherwise
	 */
	public static final boolean isNullOrEmpty(CharSequence s) {
		if (s == null) {
			return true;
		}
		return (s.length() == 0);
	}
	
	/**
	 * Checks if the given {@link CharSequence} contains any characters
	 * 
	 * @param s the {@link CharSequence} to check whether it contains any
	 * 		characters
	 * @return {@code true} if the given {@link CharSequence} contains at least
	 * 		one character, {@code false} if the given {@link CharSequence} is
	 * 		either empty or {@code null}.
	 */
	public static final boolean isNotEmpty(CharSequence s) {
		if (s == null) {
			return false;
		}
		return (s.length() > 0);
	}
	
	/**
	 * Checks if two Objects are equal by performing the necessary null checks
	 * before invoking {@link Object#equals(Object)}.
	 * 
	 * @param o1 an Object or {@code null}
	 * @param s2 an Object or {@code null}
	 * 
	 * @return {@code true} if the two objects are either equal or both are
	 * 		{@code null}, {@code false} otherwise.
	 */
	public static final boolean equals(Object o1, Object o2) {
		if (o1 == null) {
			return o2 == null;
		}
		if (o2 == null) {
			return false;
		}
		return o1.equals(o2);
	}
	
	/**
	 * Checks if the given {@link String} starts with a {@code /} character and
	 * removes it if that's the case.<br />
	 * <br />
	 * If the given {@link String} starts with {@code /} only one of them will
	 * get removed.
	 * 
	 * @param s the {@link String} to potentially remove the leading {@code /}
	 * 		character from
	 * 
	 * @return the original {@link String} passed to this method if it did not
	 * 		start with {@code /} or a new {@link String} with the leading
	 * 		{@code /} removed.
	 */
	public static final String removeLeadingSlash(String s) {
		if (s == null) {
			return null;
		}
		if (s.startsWith(SLASH)) {
			return s.substring(1);
		}
		return s;
	}
	
	/**
	 * Convenience method for creating a string representation for an object
	 * which contains the class name of the object and optionally some of its
	 * defining properties.<br />
	 * <br />
	 * 
	 * @param o
	 * @param properties
	 * @return
	 */
	public static final String toString(Object o, Object... properties) {
		if (o == null) {
			return null;
		}
		if ((properties == null) || (properties.length == 0)) {
			return o.getClass().getSimpleName();
		}
		String join = join(COMMA, properties);
		if (isNullOrEmpty(join)) {
			return o.getClass().getSimpleName();
		}
		StringBuilder sb = new StringBuilder(o.getClass().getSimpleName());
		sb = sb.append("[");
		sb = sb.append(join);
		sb = sb.append("]");
		return sb.toString();
	}
	
	/**
	 * Joins the string representations of the given objects concatenated by the
	 * given delimiter.<br />
	 * <br />
	 * Every entry within the list of objects to join which is {@code null} is
	 * considered to be non existent.<br />
	 * <br />
	 * If the list of objects to join is empty or if all of the objects are
	 * {@code null} an empty {@link String} will be returned.
	 * 
	 * @param delim the delimiter to concatenate the objects with
	 * @param parts the list of objects, whichs string representations to join
	 * 
	 * @return the concatenation of the string representations of the given
	 * 		objects or an empty {@link String}.  
	 */
	public static final String join(CharSequence delim, Object... parts) {
		if (parts == null) {
			return EMPTY;
		}
		if (parts.length == 0) {
			return EMPTY;
		}
		if (delim == null) {
			delim = EMPTY;
		}
		final StringBuilder result = new StringBuilder();
		CharSequence curDelim = EMPTY;
		for (Object part : parts) {
			if (part == null) {
				continue;
			}
			String sPart = part.toString();
			if (sPart.isEmpty()) {
				continue;
			}
			result.append(curDelim).append(sPart);
			curDelim = delim;
		}
		return result.toString();
	}
	
	public static boolean contains(String s, CharSequence c) {
		if (s == null) {
			return false;
		}
		if (c == null) {
			return false;
		}
		return s.contains(c);
	}
}
