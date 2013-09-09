package de.hsb.ms.syn.common.vo;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.AudioRecorder;
import com.badlogic.gdx.math.Vector2;

import de.hsb.ms.syn.common.util.Constants;

/**
 * Microphone Input Node
 * 
 * A Microphone Input Node obtains signals from the device's microphone
 * in order to generate its buffer. It usually doesn't have any inputs
 * (Buggy implementation)
 * 
 * @author Marcel
 *
 */
public class MicrophoneInputNode extends DraggableNode {

	/** Audio recorder */
	private AudioRecorder recorder;
	/** Gdx short temp buffer for incoming audio */
	private short[] shorts;

	/**
	 * Constructor
	 * @param inputs	Number of inputs attached to this Microphone Input Node
	 * @param pos		Initial position on the synthesizer's surface
	 */
	public MicrophoneInputNode(int inputs, Vector2 pos) {
		super(inputs, pos);
		recorder = Gdx.audio.newAudioRecorder(Constants.SAMPLING_RATE, true);
		shorts = new short[Constants.BUFFER_SIZE];
		
		this.init("node_microphone");
	}

	@Override
	/**
	 * Fill the processor's buffer with the contents
	 * of each buffer stream from every "Node path"
	 * @param buffer
	 */
	public float[] fillBuffer() {
		
		// TODO Find out the cause of this stupid white noise distortion
		
		// Read from the microphone
		recorder.read(shorts, 0, shorts.length);
		
		// Find the highest value in the buffer
		List<Short> l = Arrays.asList(ArrayUtils.toObject(shorts));
		int max = Math.abs(Collections.max(l));
		int min = Math.abs(Collections.min(l));
		float scale = (max > min) ? max : min;
		
		// Scale the buffer by this factor (map to Gdx's [0:1] volume scale)
		// We apply a primitive noise gate, too.
		//int THRESHOLD = 40;
		
		for (int i = 0; i < buffer.length; i++) {
			//buffer[i] = (Math.abs(shorts[i]) > THRESHOLD) ? (float) shorts[i] / scale : 0;
			buffer[i] = (float) shorts[i] / scale;
		}
		
		return buffer;
	}

	@Override
	public String toString() {
		return "MICROPHONE INPUT NODE " + id + " (" + this.MAX_INPUTS
				+ " INPUTS)";
	}
}
