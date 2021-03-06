package de.hsb.ms.syn.common.audio.fx;

import de.hsb.ms.syn.common.audio.FxAudioAlgorithm;
import de.hsb.ms.syn.common.audio.Properties;
import de.hsb.ms.syn.common.audio.Property;
import de.hsb.ms.syn.common.util.AudioUtils;

/**
 * Simple tap delay
 * @author Marcel
 *
 */
public class TapDelay extends FxAudioAlgorithm {
	
	/** Property key for Dry/Wet ratio of the outgoing signal */
	public static final int PROP_WET = 0x60;
	/** Property key for the feedback amount of the delay algorithm */
	public static final int PROP_FEEDBACK = 0x61;
	/** Property key for the delay time */
	public static final int PROP_TIME = 0x62;
	
	/** Internal delay cursor for the delay buffer */
	private int delayCursor;
	
	/**
	 * Constructor
	 * @param responseTime	Time between delays in seconds
	 * @param feedback		Feedback factor
	 * @param wet			Dry/wet ratio of the signal
	 */
	public TapDelay(float responseTime, float feedback, float wet) {
		super(1f, "node_delay");
		
		// Remove frequency prop (not needed)
		this.properties.remove(Properties.PROP_FREQUENCY);
		
		// Set specific props
		this.properties.put(PROP_FEEDBACK, new Property(PROP_FEEDBACK, "Feedback", 0.0f, 1.0f, 0.01f, feedback));
		this.properties.put(PROP_WET, new Property(PROP_WET, "Dry/Wet ratio", 0.0f, 1.0f, 0.01f, wet));
		this.properties.put(PROP_TIME, new Property(PROP_TIME, "Delay time", 0.1f, 5.0f, 0.1f, responseTime));
		
		this.delayCursor = 0;
		this.setVolume(1.0f);
		
		this.recalc();
	}
	
	@Override
	public float[] fillFXBuffer(float[] buffer) {
		// Overriding this method because all delay processing needs to go here
		for (int i = 0; i < buffer.length; i++) {
			
			// Get the buffer sample
			float x = buffer[i];
			
			// Get delay data sample at the current position
			float y = this.data[this.delayCursor];
			
			// Save the new delay data
			float fb = property(TapDelay.PROP_FEEDBACK).val();
			this.data[this.delayCursor++] = x + (y * fb);
			
			// Wrap the delay cursor
			if (this.delayCursor >= this.data.length) this.delayCursor = 0;
			
			// Re-set the buffer sample
			if (y > 1) y = 1; else if (y < -1) y = -1;
			float wet = property(TapDelay.PROP_WET).val();
			buffer[i] = (x * (1.0f - wet)) + (y * wet);
		}
		
		return buffer;
	}

	@Override
	public void recalc() {
		float time = property(TapDelay.PROP_TIME).val();
		int samples = AudioUtils.timeToSamples((int) (time * 1000));
		this.data = new float[samples];
		// Make sure the delayCursor does wrap around in case the Buffer Size was made smaller than its current value
		this.delayCursor = this.delayCursor % this.data.length;
	}

}
