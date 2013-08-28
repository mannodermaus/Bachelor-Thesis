package de.hsb.ms.syn.common.vo.nodes;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Touchable;

import de.hsb.ms.syn.common.util.Constants;
import de.hsb.ms.syn.desktop.abs.Node;

/**
 * Center node class.
 * The central Node of the synthesizer's surface. It can't be dragged
 * around and is also the starting point for both the arrangement
 * and computation processes
 * @author Marcel
 *
 */
public class CenterNode extends Node {

	/**
	 * Constructor
	 */
	public CenterNode() {
		super(Constants.CENTERNODE_INPUTS, new Vector2(0, 0));
		setTouchable(Touchable.disabled);
		
		this.init("node_center");
		setNodePosition(-getWidth() / 2, -getHeight() / 2);
	}
	
	/**
	 * Fill the processor's buffer with the contents
	 * of each buffer stream from every "Node path"
	 * @param buffer
	 */
	public float[] fillBuffer() {
		// When there are no inputs, return immediately
		if (this.inputs.size() == 0) return buffer;
		
		// For each connection to the Center node, create a new buffer and let it be filled
		float[][] buffers = new float[this.inputs.size()][Constants.BUFFER_SIZE];
		for (int i = 0; i < this.inputs.size(); i++) {
			buffers[i] = this.inputs.get(i).fillBuffer();
		}
		
		// Set the processor's buffer to the first Node path's buffer, add the other ones on top
		buffer = buffers[0];
		if (this.inputs.size() > 1) {
			for (int i = 0; i < Constants.BUFFER_SIZE; i++) {
				for (int j = 1; j < this.inputs.size(); j++) {
					// Normalize
					float addition = buffers[j][i];
					buffer[i] = Math.max(Math.min(buffer[i] + addition, 1), -1);
				}
			}
		}
		
		return buffer;
	}

	@Override
	public String toString() {
		return "CENTR NODE (" + this.MAX_INPUTS + " INPUTS)";
	}
}