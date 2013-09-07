package de.hsb.ms.syn.common.vo;

import com.badlogic.gdx.math.Vector2;

import de.hsb.ms.syn.common.audio.AudioAlgorithm;

/**
 * Gen Node
 * 
 * A Gen Node produces streams of audio based on mathematical
 * functions. Usually, a Gen Node doesn't have any inputs.
 * Examples include Sinewave, Sawtooth and Square signals
 * @author Marcel
 *
 */
public class GenNode extends DraggableNode {
	
	/**
	 * Constructor
	 * @param inputs	Number of inputs attached to this Gen Node
	 * @param pos		Initial position on the synthesizer's surface
	 */
	public GenNode(int inputs, Vector2 pos) {
		super(inputs, pos);
		algorithm = AudioAlgorithm.GEN_DEFAULT;
		// Default Scale
		//scale = new Scale(Scale.BASE_C, Scale.MODE_CHROMATIC);
		this.init(this.algorithm.getSpriteName());
	}

	@Override
	public void setAlgorithm(AudioAlgorithm d) {
		super.setAlgorithm(d);
		//this.delegate.setFreq(scale.getBaseFrequency());
	}
	
	@Override
	/**
	 * Fill the processor's buffer with the contents
	 * of each buffer stream from every "Node path"
	 * @param buffer
	 */
	public float[] fillBuffer() {
		// Delegate
		return this.algorithm.fillGenBuffer(buffer);
	}
	
	@Override
	public String toString() {
		return "GEN NODE " + id + " (" + this.MAX_INPUTS + " INPUTS)";
	}
}