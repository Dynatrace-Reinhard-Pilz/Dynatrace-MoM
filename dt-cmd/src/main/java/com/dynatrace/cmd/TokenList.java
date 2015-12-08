package com.dynatrace.cmd;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

public class TokenList implements Iterable<Token>, Token {

	private final ArrayList<Token> tokens = new ArrayList<Token>();
	
	public TokenList(Token first) {
		Objects.requireNonNull(first);
		tokens.add(first);
	}

	public boolean equals(String arg) {
		Objects.requireNonNull(arg);
		return tokens.get(0).equals(arg);
	}

	public boolean matches(String arg) {
		Objects.requireNonNull(arg);
		return tokens.get(0).matches(arg);
	}

	public Iterator<Token> iterator() {
		return tokens.iterator();
	}

}
