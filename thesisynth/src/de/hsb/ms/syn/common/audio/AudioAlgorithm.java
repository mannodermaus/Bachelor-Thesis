package de.hsb.ms.syn.common.audio;

import de.hsb.ms.syn.common.audio.fx.LFO;
import de.hsb.ms.syn.common.audio.gen.Sinewave;
import de.hsb.ms.syn.common.util.Constants;
import de.hsb.ms.syn.common.vo.DraggableNode;

/**
 * Base class for Delegate algorithms used by both
 * GenNode and FXNode instances
 * It is extended by FXDelegate and GenDelegate
 * @author Marcel
 *
 */
public abstract class AudioAlgorithm {

	// Default algorithms applied to new instances (fallback)
	public static AudioAlgorithm GEN_DEFAULT = new Sinewave(new Scale(Scale.BASE_C, Scale.MODE_CHROMATIC));
	public static AudioAlgorithm FX_DEFAULT  = new LFO(1f, Sinewave.class);
	
	/** Properties of the delegate */
	protected Properties properties;
	
	/** Musical scale of this delegate */
	protected Scale scale;
	
	protected float[] data;		// Data array containing one algorithm iteration
	protected int cursor;		// Cursor pointing to data array (wrap-around)

	// Graphical representation
	private String spriteName;	// Sprite name of this Delegate
	
	// Index of the Node
//	private int nodeIndex;
	
	protected AudioAlgorithm(Scale scale, String name) {
		this(scale.getBaseFrequency(), name);
		this.scale = scale;
	}
	
	protected AudioAlgorithm(float frequency, String name) {
		this.properties = new Properties(name, -1, 0.1f, frequency, 0.0f);
		this.cursor = 0;
		this.spriteName = name;
	}
	
	/** Abstract: Return class of served objects */
	public abstract Class<? extends DraggableNode> getServedClass();
	
	/** Abstract: Recalc the data of this Delegate */
	public abstract void recalc();
	
	/**
	 * Fill the buffer the "GenNode" way
	 * (set the buffer)
	 * @param buffer
	 * @return
	 */
	public float[] fillGenBuffer(float[] buffer) {
		for (int i = 0; i < Constants.BUFFER_SIZE; i++) {
			cursor = (cursor >= data.length) ? 0 : cursor;
			buffer[i] = data[cursor++];
		}
		return buffer;
	}
	
	/**
	 * Fill the buffer the "FXNode" way
	 * (multiply the buffer)
	 * @param buffer
	 * @return
	 */
	public float[] fillFXBuffer(float[] buffer) {
		for (int i = 0; i < Constants.BUFFER_SIZE; i++) {
			cursor = (cursor >= data.length) ? 0 : cursor;
			buffer[i] *= data[cursor++];
		}
		return buffer;
	}
	
	/**
	 * Set volume to given value [0:1]
	 * @param volume
	 */
	public void setVolume(float volume) {
		property(Properties.PROP_VOLUME).setVal(volume);
		this.recalc();
	}
	
	/**
	 * Get volume
	 * @return
	 */
	public float getVolume() {
		return this.properties.get(Properties.PROP_VOLUME).val();
	}
	
	/**
	 * Set frequency to given value
	 * @param freq
	 */
	public void setFreq(float freq) {
		property(Properties.PROP_FREQUENCY).setVal(freq);
		this.recalc();
	}
	
	/**
	 * Get frequency
	 * @return
	 */
	public float getFreq() {
		return this.properties.get(Properties.PROP_FREQUENCY).val();
	}
	
	/**
	 * Get name of sprite
	 * @return
	 */
	public String getSpriteName() {
		return this.spriteName;
	}
	
	/**
	 * Get algorithm data
	 * @return
	 */
	public float[] getData() {
		return this.data;
	}

//	public int nodeIndex() {
//		return nodeIndex;
//	}
	
//	public void setNodeIndex(int id) {
//		this.nodeIndex = id;
//		this.properties.setNodeIndex(id);
//	}
	
	/**
	 * Get the Delegate's property behind the given key
	 * @param key
	 * @return
	 */
	protected Property property(int key) {
		return this.properties.get(key);
	}
	
	/**
	 * Get all NodeProperties of this Delegate
	 * @return
	 */
	public Properties getProperties() {
		return this.properties;
	}
	
	@Override
	/**
	 * toString override
	 */
	public String toString() {
		return String.format("%s (%.2f Hz)", this.spriteName, this.properties.get(Properties.PROP_FREQUENCY));
	}
}
