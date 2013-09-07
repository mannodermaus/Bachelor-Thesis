package de.hsb.ms.syn.common.audio;

import de.hsb.ms.syn.common.vo.DraggableNode;
import de.hsb.ms.syn.common.vo.FXNode;

/**
 * Base class for FX delegates. It extends Delegate and
 * implements the abstract getServedClass() method
 * 
 * @author Marcel
 *
 */
public abstract class FxAudioAlgorithm extends AudioAlgorithm {

	/**
	 * Constructor
	 * @param freq
	 * @param type
	 * @param name
	 */
	protected FxAudioAlgorithm(float freq, String name) {
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
