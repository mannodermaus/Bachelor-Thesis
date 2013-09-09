package de.hsb.ms.syn.common.exceptions;

import de.hsb.ms.syn.common.vo.Node;

/**
 * Exception type thrown when a Node is being requested to draw itself without
 * having gone through a proper initialization process first
 * @author Marcel
 *
 */
public class NodeNotInitializedException extends Exception {
	
	private static final long serialVersionUID = -7891059883696241234L;

	/**
	 * Constructor
	 * @param node
	 */
	public NodeNotInitializedException(Node node) {
		super("Node not initialized: " + node.toString());
	}
	
}
