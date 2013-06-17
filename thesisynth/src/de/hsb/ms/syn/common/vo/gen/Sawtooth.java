package de.hsb.ms.syn.common.vo.gen;

import de.hsb.ms.syn.common.abs.GenDelegate;
import de.hsb.ms.syn.common.util.AudioUtils;
import de.hsb.ms.syn.common.vo.NodeProperties;

/**
 * Sawtooth generator
 * @author Marcel
 *
 */
public class Sawtooth extends GenDelegate {
	
	/**
	 * Constructor
	 * @param freq
	 */
	public Sawtooth(float freq) {
		super(freq, "node_sawtooth");
		this.recalc();
	}

	@Override
	public void recalc() {
		float freq = property(NodeProperties.PROP_FREQUENCY).val();
		float volume = property(NodeProperties.PROP_VOLUME).val();
		this.data = AudioUtils.sawtooth(freq, volume);
	}

}
