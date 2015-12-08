package com.dynatrace.rest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Agents implements Iterable<Agent> {

	private final Document document;
	
	public Agents(Document document) {
		Objects.requireNonNull(document);
		this.document = document;
	}

	@Override
	public Iterator<Agent> iterator() {
		NodeList elements = document.getElementsByTagName("agentinformation");
		if (elements == null) {
			return Collections.emptyIterator();
		}
		Collection<Agent> agents = new ArrayList<>();
		for (int i = 0; i < elements.getLength(); i++) {
			agents.add(new Agent((Element) elements.item(i)));
		}
		return agents.iterator();
	}
	
}
