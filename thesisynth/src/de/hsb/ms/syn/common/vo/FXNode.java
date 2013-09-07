package de.hsb.ms.syn.common.vo;

import com.badlogic.gdx.math.Vector2;

import de.hsb.ms.syn.common.audio.AudioAlgorithm;
import de.hsb.ms.syn.common.util.Constants;

/**
 * FX Node
 * 
 * An FX Node can't produce audio data itself, but rather it modifies
 * the incoming signal from its inputs. Examples for FX Nodes include
 * Low Frequency Oscillators, Delay and Pitch Shift algorithms
 * @author Marcel
 *
 */
public class FXNode extends DraggableNode {
	
	/**
	 * Constructor
	 * @param inputs	Number of inputs attached to this FX Node
	 * @param pos		Initial position on the synthesizer's surface
	 */
	public FXNode(int inputs, Vector2 pos) {
		super(inputs, pos);
		algorithm = AudioAlgorithm.FX_DEFAULT;
		this.init(this.algorithm.getSpriteName());
	}

	@Override
	/**
	 * Fill the processor's buffer with the contents
	 * of each buffer stream from every "Node path"
	 * @param buffer
	 */
	public float[] fillBuffer() {
		// An FX Node is dependent on its inputs. Therefore,
		// get the buffers from them and process whatever FX applies
		
		// When there are no inputs, return immediately
		if (this.inputs.size() == 0) {
			this.resetBuffer();
			return buffer;
		}
		
		// For each connection, create a new buffer and let it be filled
		float[][] buffers = new float[this.inputs.size()][Constants.BUFFER_SIZE];
		for (int i = 0; i < this.inputs.size(); i++) {
			buffers[i] = this.inputs.get(i).fillBuffer();
		}
		
		// Set the processor's buffer to the first Node path's buffer, add the
		// other ones on top
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
		
		// Finally, process the FX on the cumulated buffer
		buffer = this.algorithm.fillFXBuffer(buffer);
		
		return buffer;
	}

	@Override
	public String toString() {
		return "FX NODE " + id + " (" + this.MAX_INPUTS + " INPUTS)";
	}
}
