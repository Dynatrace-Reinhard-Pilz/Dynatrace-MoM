package com.dynatrace.http;

/**
 * Known HTTP Response Codes
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 */
public enum ResponseCode {

	OK(200),
	METHOD_NOT_ALLOWED(405);
	
	/**
	 * c'tor
	 * 
	 * @param code the HTTP response code value
	 */
	ResponseCode(int code) {
		this.code = code;
	};
	
	private final int code;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return new StringBuilder(code).
				append(" (").
				append(name())
				.append(")").
				toString();
	}
	
	/**
	 * Checks if the given integer based response code matches up with
	 * the response code of this enum value.
	 * 
	 * @param code the integer response code to check against
	 * 
	 * @return {@code true} if the given integer response code matches up
	 * 		with the response code of this enum value
	 */
	public boolean matches(int code) {
		return (this.code == code);
	}
	
	/**
	 * Queries for the right enum value for the given integer response code
	 * 
	 * @param code the integer response code to query with
	 * 
	 * @return the enum value matching up with the given integer response
	 * 		code or {@code null} if none is matching
	 */
	public static ResponseCode fromCode(int code) {
		final ResponseCode[] values = values();
		for (ResponseCode value : values) {
			if (value.matches(code)) {
				return value;
			}
		}
		return null;
	}
	
}
