package de.hsb.ms.syn.common.audio;

/**
 * Special fixed frequency scale used by LFO nodes to hold their
 * modulating data algorithm
 * @author Marcel
 *
 */
public class FixedFrequencyScale extends Scale {
	
	private static final long serialVersionUID = -6620185805803314902L;
	
	/** Frequency */
	private float freq;
	
	/**
	 * Constructor
	 * @param freq
	 */
	public FixedFrequencyScale(float freq) {
		this.freq = freq;
	}

	/**
	 * Get frequency
	 * @return
	 */
	public float getBaseFrequency() {
		return this.freq;
	}
	
	/**
	 * Get the number of notes in this scale
	 * @return 1, because it is a single fixed frequency scale
	 */
	public int noteCount() {
		return 1;
	}
}
