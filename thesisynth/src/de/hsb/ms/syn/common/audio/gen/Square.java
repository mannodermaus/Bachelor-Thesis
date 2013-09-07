package de.hsb.ms.syn.common.audio.gen;

import de.hsb.ms.syn.common.audio.FixedFrequencyScale;
import de.hsb.ms.syn.common.audio.GenAudioAlgorithm;
import de.hsb.ms.syn.common.audio.Properties;
import de.hsb.ms.syn.common.audio.Scale;
import de.hsb.ms.syn.common.util.AudioUtils;

/**
 * Square generator
 * @author Marcel
 *
 */
public class Square extends GenAudioAlgorithm {
	
	/**
	 * Constructor
	 * @param freq
	 */
	public Square(Scale scale) {
		super(scale, "node_square");
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
		this.data = AudioUtils.square(freq, volume);
	}

}
