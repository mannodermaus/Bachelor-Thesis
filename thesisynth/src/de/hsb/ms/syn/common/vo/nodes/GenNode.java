package de.hsb.ms.syn.common.vo.nodes;

import com.badlogic.gdx.math.Vector2;

import de.hsb.ms.syn.common.abs.Delegate;
import de.hsb.ms.syn.common.abs.DraggableNode;

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
		delegate = Delegate.GEN_DEFAULT;
		this.init(this.delegate.getSpriteName());
	}

	@Override
	/**
	 * Fill the processor's buffer with the contents
	 * of each buffer stream from every "Node path"
	 * @param buffer
	 */
	public float[] fillBuffer() {
		// Delegate
		return this.delegate.fillGenBuffer(buffer);
	}
	
	@Override
	public String toString() {
		return "GEN NODE " + id + " (" + this.MAX_INPUTS + " INPUTS)";
	}
}