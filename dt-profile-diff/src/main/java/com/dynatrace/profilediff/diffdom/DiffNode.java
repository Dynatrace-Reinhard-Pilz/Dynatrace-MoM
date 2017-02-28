package com.dynatrace.profilediff.diffdom;

import org.w3c.dom.Node;

public interface DiffNode {

	String getNodeName();
	String getNodeValue();
	int getNodeType();
	Node getBase();
	Node getMatch();
}
