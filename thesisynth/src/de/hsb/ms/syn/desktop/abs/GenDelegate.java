package de.hsb.ms.syn.desktop.abs;

import de.hsb.ms.syn.common.vo.NodeProperties;
import de.hsb.ms.syn.common.vo.NodeProperty;
import de.hsb.ms.syn.common.vo.Scale;
import de.hsb.ms.syn.common.vo.nodes.GenNode;

/**
 * Base class for Gen delegates. It extends Delegate and
 * implements the abstract getServedClass() method
 * 
 * @author Marcel
 *
 */
public abstract class GenDelegate extends Delegate {

	/**
	 * Constructor
	 * @param freq
	 * @param type
	 * @param name
	 */
	protected GenDelegate(float freq, String name) {
		super(freq, name);
		
		// Add a property that is locked to a scale rather than Hz
		properties.put(NodeProperties.PROP_TONE,
				new NodeProperty(NodeProperties.PROP_TONE, "Tone",
				0, Scale.getNumberOfPossibleOctaves(), 1, 0));
	}

	@Override
	/**
	 * Returns the class of the served objects
	 * (In this case: GenNode.class)
	 */
	public Class<? extends DraggableNode> getServedClass() {
		return GenNode.class;
	}
}
