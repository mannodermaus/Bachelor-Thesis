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
	protected GenDelegate(Scale scale, String name) {
		super(scale, name);
		
		// Add a property that is locked to a scale rather than Hz
		NodeProperty scaleProp = new NodeProperty(NodeProperties.PROP_TONE, "Tone",
								 0, scale.noteCount(), 1, 0);
		scaleProp.setExtra(scale);
		properties.put(NodeProperties.PROP_TONE, scaleProp);
		
		// Hide the frequency property (it will not be sent over network)
		properties.get(NodeProperties.PROP_FREQUENCY).hide();
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
