package de.hsb.ms.syn.common.audio.fx;

import de.hsb.ms.syn.common.audio.FxAudioAlgorithm;
import de.hsb.ms.syn.common.audio.FixedFrequencyScale;
import de.hsb.ms.syn.common.audio.GenAudioAlgorithm;
import de.hsb.ms.syn.common.audio.Properties;
import de.hsb.ms.syn.common.audio.Property;
import de.hsb.ms.syn.common.audio.Scale;

/**
 * Low frequency oscillator for various waveforms
 * @author Marcel
 *
 */
public class LFO extends FxAudioAlgorithm {

	/** Algorithm providing the LFO's modulation wave */
	private GenAudioAlgorithm processor;
	
	/**
	 * Constructor
	 * @param freq
	 * @param delClass
	 */
	public LFO(float freq, Class<? extends GenAudioAlgorithm> delClass) {
		super(freq, "node_lfo");
		this.setVolume(1.0f);
		try {
			// Create the GenAlgorithm for the modulation wave
			Scale sc = new FixedFrequencyScale(freq);
			this.processor = delClass.getConstructor(Scale.class).newInstance(sc);
			this.processor.setVolume(1.0f);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Re-set frequency property (LFOs use frequencies < 20 Hz, hence the name
		this.properties.put(Properties.PROP_FREQUENCY,
				new Property(Properties.PROP_FREQUENCY, "Frequency", 0.1f, 10.0f, 0.1f, freq));
		
		this.recalc();
	}

	@Override
	public void recalc() {
		// Let the processor do this
		if (processor != null) {
			Property volume = this.properties.get(Properties.PROP_FREQUENCY);
			processor.getProperties().put(Properties.PROP_FREQUENCY, volume);
			processor.recalc();
			this.data = processor.getData();
		}
	}
}
