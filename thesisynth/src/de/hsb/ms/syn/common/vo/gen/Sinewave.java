package de.hsb.ms.syn.common.vo.gen;

import de.hsb.ms.syn.common.util.AudioUtils;
import de.hsb.ms.syn.common.vo.NodeProperties;
import de.hsb.ms.syn.desktop.abs.GenDelegate;

/**
 * Sinewave generator
 * @author Marcel
 *
 */
public class Sinewave extends GenDelegate {
	
	/**
	 * Constructor
	 * @param freq
	 */
	public Sinewave(float freq) {
		super(freq, "node_sinewave");
		this.recalc();
	}

	@Override
	public void recalc() {
		float freq = property(NodeProperties.PROP_FREQUENCY).val();
		float volume = property(NodeProperties.PROP_VOLUME).val();
		this.data = AudioUtils.sinewave(freq, volume);
	}

}
