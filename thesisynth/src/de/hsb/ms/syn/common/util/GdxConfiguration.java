package de.hsb.ms.syn.common.util;

/**
 * Data structure for a ControllerUI's configuration.
 * Its contents will be put into the AndroidApplicationConfiguration
 * upon initializing the ControllerUI. Each ControllerUI must
 * provide a getConfiguration() implementation with the desired
 * functions enabled. For instance, some experiments require access
 * to the smartphone's accelerometer data - this must be enabled here.
 * @author Marcel
 *
 */
public class GdxConfiguration {
	
	/** Use OpenGL ES 2.0 */
	public boolean useGL20 = false;
	
	/** Use accelerometer data */
	public boolean useAccelerometer = false;
	
	/** Use wakelock functionality */
	public boolean useWakelock = false;
	
	/** Use compass */
	public boolean useCompass = false;
	
	/** Use bluetooth */
	public boolean useBluetooth = true;
	
}
