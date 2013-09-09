package de.hsb.ms.syn.common.audio;

import de.hsb.ms.syn.common.vo.DraggableNode;
import de.hsb.ms.syn.common.vo.GenNode;

/**
 * Base class for Gen algorithms. It extends AudioAlgorithm and
 * implements the abstract getServedClass() method
 * 
 * @author Marcel
 *
 */
public abstract class GenAudioAlgorithm extends AudioAlgorithm {

	/**
	 * Constructor
	 * @param freq
	 * @param type
	 * @param name
	 */
	protected GenAudioAlgorithm(Scale scale, String name) {
		super(scale, name);
		
		// Add a property that is locked to a scale rather than Hz
		Property scaleProp = new Property(Properties.PROP_TONE, "Tone",
								 0, scale.noteCount(), 1, 0);
		scaleProp.setExtra(scale);
		properties.put(Properties.PROP_TONE, scaleProp);
		
		// Hide the frequency property (it will not be sent over network)
		properties.get(Properties.PROP_FREQUENCY).hide();
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
