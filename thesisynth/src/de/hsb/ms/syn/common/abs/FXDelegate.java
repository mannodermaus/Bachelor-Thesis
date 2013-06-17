package de.hsb.ms.syn.common.abs;

import de.hsb.ms.syn.common.vo.nodes.FXNode;

/**
 * Base class for FX delegates. It extends Delegate and
 * implements the abstract getServedClass() method
 * 
 * @author Marcel
 *
 */
public abstract class FXDelegate extends Delegate {

	/**
	 * Constructor
	 * @param freq
	 * @param type
	 * @param name
	 */
	protected FXDelegate(float freq, String name) {
		super(freq, name);
	}

	@Override
	/**
	 * Returns the class of the served objects
	 * (In this case: FXNode.class)
	 */
	public Class<? extends DraggableNode> getServedClass() {
		return FXNode.class;
	}
}
