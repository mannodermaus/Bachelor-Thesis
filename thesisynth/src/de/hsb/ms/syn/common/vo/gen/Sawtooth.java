package de.hsb.ms.syn.common.vo.gen;

import de.hsb.ms.syn.common.util.AudioUtils;
import de.hsb.ms.syn.common.vo.FixedFrequencyScale;
import de.hsb.ms.syn.common.vo.NodeProperties;
import de.hsb.ms.syn.common.vo.Scale;
import de.hsb.ms.syn.desktop.abs.GenDelegate;

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
	public Sawtooth(Scale scale) {
		super(scale, "node_sawtooth");
		this.recalc();
	}

	@Override
	public void recalc() {
		float freq;
		if (scale instanceof FixedFrequencyScale)
			freq = property(NodeProperties.PROP_FREQUENCY).val();
		else {
			int knob = (int) property(NodeProperties.PROP_TONE).val();
			freq = scale.getFrequencyForKnobValue(knob);
		}
		float volume = property(NodeProperties.PROP_VOLUME).val();
		this.data = AudioUtils.sawtooth(freq, volume);
	}

}
