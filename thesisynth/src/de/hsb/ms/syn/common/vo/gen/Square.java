package de.hsb.ms.syn.common.vo.gen;

import de.hsb.ms.syn.common.abs.GenDelegate;
import de.hsb.ms.syn.common.util.AudioUtils;
import de.hsb.ms.syn.common.vo.NodeProperties;

/**
 * Square generator
 * @author Marcel
 *
 */
public class Square extends GenDelegate {
	
	/**
	 * Constructor
	 * @param freq
	 */
	public Square(float freq) {
		super(freq, "node_square");
		this.recalc();
	}

	@Override
	public void recalc() {
		float freq = property(NodeProperties.PROP_FREQUENCY).val();
		float volume = property(NodeProperties.PROP_VOLUME).val();
		this.data = AudioUtils.square(freq, volume);
	}

}
