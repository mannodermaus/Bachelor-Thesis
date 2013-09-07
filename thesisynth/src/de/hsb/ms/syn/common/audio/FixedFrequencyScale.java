package de.hsb.ms.syn.common.audio;

public class FixedFrequencyScale extends Scale {
	
	private static final long serialVersionUID = -6620185805803314902L;
	
	private float freq;
	
	public FixedFrequencyScale(float freq) {
		this.freq = freq;
	}

	public float getBaseFrequency() {
		return this.freq;
	}
	
	public int noteCount() {
		return 1;
	}
}
