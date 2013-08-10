package de.hsb.ms.syn.common.util;

/**
 * Utility class for specific audio things. It contains the generation
 * algorithms for most Gen Nodes, for example
 * 
 * @author Marcel
 * 
 */
public abstract class AudioUtils {

	/**
	 * Generate a sinewave of given velocity and frequency
	 * 
	 * @param freq
	 * @param velocity
	 * @return
	 */
	public static float[] sinewave(float freq, float velocity) {
		// Caluclate number of samples for the given set of params
		int samples = (int) Math.floor(Constants.SAMPLING_RATE * (1 / freq));
		float[] vals = new float[samples];
		for (int i = 0; i < samples; i++) {
			vals[i] = velocity
					* (float) Math.sin(2 * Math.PI * i / (44100 / freq));
		}
		return vals;
	}

	/**
	 * Generate a square wave of given velocity and frequency
	 * 
	 * @param freq
	 * @param velocity
	 * @return
	 */
	public static float[] square(float freq, float velocity) {
		// Caluclate number of samples for the given set of params
		int samples = (int) Math.floor(Constants.SAMPLING_RATE * (1 / freq));
		float[] vals = new float[samples];
		// Fill half of these samples with a 1
		for (int i = 0; i < (vals.length - (samples % 2)) / 2; i++) {
			vals[i] = velocity * 1;
		}
		// Fill the other half with -1
		for (int i = (vals.length - (samples % 2)) / 2; i < samples; i++) {
			vals[i] = velocity * (-1);
		}
		return vals;
	}

	/**
	 * Generate a sawtooth of given velocity and frequency
	 * 
	 * @param freq
	 * @param velocity
	 * @return
	 */
	public static float[] sawtooth(float freq, float velocity) {
		// Caluclate number of samples for the given set of params
		int samples = (int) Math.floor(Constants.SAMPLING_RATE * (1 / freq));
		float[] vals = new float[samples];
		// Use the linear function f(x) = 2/samples * x - 1 for emulating the
		// sawtooth
		for (int i = 0; i < (vals.length - (samples % 2)) / 2; i++) {
			vals[i] = velocity * (((2.0f / samples) * i) - 1);
		}

		return vals;
	}

	/**
	 * Generate a triangle of given velocity and frequency
	 * 
	 * @param freq
	 * @param velocity
	 * @return
	 */
	public static float[] triangle(float freq, float velocity) {
		// Caluclate number of samples for the given set of params
		int samples = (int) Math.floor(Constants.SAMPLING_RATE * (1 / freq));
		float[] vals = new float[samples];
		// Fill half of these samples with f(x) = 2/(samples/2) * x - 1 (upwards slope of the triangle)
		for (int i = 0; i < (vals.length - (samples % 2)) / 2; i++) {
			vals[i] = velocity * (((2.0f / (samples / 2)) * i) - 1);
		}
		// Fill the other half with f(x) = -2/(samples/2) * x + 3 (downwards slope of the triangle)
		for (int i = (vals.length - (samples % 2)) / 2; i < samples; i++) {
			vals[i] = velocity * (((-2.0f / (samples / 2)) * i) + 3);
		}

		return vals;
	}

	/**
	 * Convert the given time in milliseconds to an amount of samples with the
	 * same duration (for instance, at 44100 kHz, the number of samples for 500
	 * ms would be 22050)
	 * 
	 * @param time
	 * @return
	 */
	public static int timeToSamples(int time) {
		return Math.round((float) Constants.SAMPLING_RATE
				* ((float) time / 1000.0f));
	}

}
