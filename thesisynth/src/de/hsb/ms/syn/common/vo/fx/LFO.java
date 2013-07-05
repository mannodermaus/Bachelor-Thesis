package de.hsb.ms.syn.common.vo.fx;

import de.hsb.ms.syn.common.vo.FixedFrequencyScale;
import de.hsb.ms.syn.common.vo.NodeProperties;
import de.hsb.ms.syn.common.vo.NodeProperty;
import de.hsb.ms.syn.common.vo.Scale;
import de.hsb.ms.syn.desktop.abs.FXDelegate;
import de.hsb.ms.syn.desktop.abs.GenDelegate;

/**
 * Low frequency oscillator for various waveforms
 * @author Marcel
 *
 */
public class LFO extends FXDelegate {

	/** Gen Delegate providing the LFO's modulation wave */
	private GenDelegate processor;
	
	/**
	 * Constructor
	 * @param freq
	 * @param delClass
	 */
	public LFO(float freq, Class<? extends GenDelegate> delClass) {
		super(freq, "node_lfo");
		this.setVolume(1.0f);
		try {
			Scale sc = new FixedFrequencyScale(freq);
			this.processor = delClass.getConstructor(Scale.class).newInstance(sc);
			this.processor.setVolume(1.0f);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Re-set frequency property (LFOs use frequencies < 20 Hz, hence the name
		this.properties.put(NodeProperties.PROP_FREQUENCY,
				new NodeProperty(NodeProperties.PROP_FREQUENCY, "Frequency", 0.1f, 10.0f, 0.1f, freq));
		
		this.recalc();
	}

	@Override
	public void recalc() {
		// Let the processor do this
		if (processor != null) {
			NodeProperty volume = this.properties.get(NodeProperties.PROP_FREQUENCY);
			processor.getProperties().put(NodeProperties.PROP_FREQUENCY, volume);
			processor.recalc();
			this.data = processor.getData();
		}
	}
}
