package com.dynatrace.profilediff;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class NodeMatcher {
	
	public static boolean matches(Node a, Node b) {
		if ((a == null) && (b != null)) {
			return false;
		}
		if ((a != null) && (b == null)) {
			return false;
		}
		if ((a == null) && (b == null)) {
			return true;
		}
		if (a.getNodeType() != b.getNodeType()) {
			return false;
		}
		switch (a.getNodeType()) {
		case Node.ATTRIBUTE_NODE:
			Log.info("ATTRIBUTE_NODE: " + a.getNodeName());
			break;
		case Node.ELEMENT_NODE:
			Log.info("ELEMENT_NODE: " + a.getNodeName());
			break;
		case Node.COMMENT_NODE:
			Log.info("COMMENT_NODE: " + a.getNodeValue());
			break;
		case Node.DOCUMENT_NODE:
			Log.info("DOCUMENT_NODE: " + a.getNodeName());
			break;
		case Node.TEXT_NODE:
			Log.info("TEXT_NODE: " + a.getNodeValue());
			break;
		default:
			Log.info("matches(" + a.getClass() + ")");
		}
		
		if (!Strings.equals(a.getNodeName(), b.getNodeName())) {
			return false;
		}
		if (!Strings.equals(a.getNodeValue(), b.getNodeValue())) {
			return false;
		}
		NamedNodeMap aAttributes = a.getAttributes();
		NamedNodeMap bAttributes = b.getAttributes();
		return matches(a.getChildNodes(), b.getChildNodes());
	}
	
	private static boolean matches(NamedNodeMap a, NamedNodeMap b) {
		if ((a == null) && (b != null)) {
			return false;
		}
		if ((a != null) && (b == null)) {
			return false;
		}
		if ((a == null) && (b == null)) {
			return true;
		}
		int length = a.getLength();
		for (int i = 0; i < length; i++) {
			Node attribute = a.item(i);
		}
		return false;
	}
	
	private static boolean matches(NodeList a, NodeList b) {
		if ((a == null) && (b != null)) {
			return false;
		}
		if ((a != null) && (b == null)) {
			return false;
		}
		if ((a == null) && (b == null)) {
			return true;
		}
		int aLength = a.getLength();
		int bLength = b.getLength();
		if ((aLength == 0) && (bLength == 0)) {
			return true;
		}
		if (aLength != bLength) {
			return false;
		}
		for (int i = 0; i < aLength; i++) {
			Node aChild = a.item(i);
			boolean found = false;
			for (int j = 0; j < bLength; j++) {
				Node bChild = b.item(j);
				if (matches(aChild, bChild)) {
					found = true;
					break;
				}
			}
			if (!found) {
				return false;
			}
		}
		return true;
	}

}
