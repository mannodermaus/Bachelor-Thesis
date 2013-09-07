package de.hsb.ms.syn.common.audio.gen;

import de.hsb.ms.syn.common.audio.FixedFrequencyScale;
import de.hsb.ms.syn.common.audio.GenAudioAlgorithm;
import de.hsb.ms.syn.common.audio.Properties;
import de.hsb.ms.syn.common.audio.Scale;
import de.hsb.ms.syn.common.util.AudioUtils;

/**
 * Sinewave generator
 * @author Marcel
 *
 */
public class Sinewave extends GenAudioAlgorithm {
	
	/**
	 * Constructor
	 * @param freq
	 */
	public Sinewave(Scale scale) {
		super(scale, "node_sinewave");
		this.recalc();
	}

	@Override
	public void recalc() {
		float freq;
		if (scale instanceof FixedFrequencyScale)
			freq = property(Properties.PROP_FREQUENCY).val();
		else {
			int knob = (int) property(Properties.PROP_TONE).val();
			freq = scale.getFrequencyForKnobValue(knob);
		}
		float volume = property(Properties.PROP_VOLUME).val();
		this.data = AudioUtils.sinewave(freq, volume);
	}

}
