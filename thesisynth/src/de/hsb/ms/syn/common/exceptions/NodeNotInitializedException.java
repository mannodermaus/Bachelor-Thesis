package de.hsb.ms.syn.common.exceptions;

import de.hsb.ms.syn.common.vo.Node;

public class NodeNotInitializedException extends Exception {
	
	private static final long serialVersionUID = -7891059883696241234L;

	public NodeNotInitializedException(Node node) {
		super("Node not initialized: " + node.toString());
	}
	
}
