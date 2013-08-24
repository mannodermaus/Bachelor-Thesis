package de.hsb.ms.syn.common.util;

import java.util.UUID;

import com.badlogic.gdx.graphics.Color;

/**
 * Constants used throughout the application
 * @author Marcel
 *
 */
public abstract class Constants {
	
	// Logging
	public static final String LOG_TAG = "thesisynth";
	
	// Network (Bluetooth)
	public static final UUID	LAPTOP_UUID			= UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	public static final String 	LAPTOP_MAC_MARCEL	= "C0:18:85:A8:C9:10";
	public static final String	LAPTOP_MAC_TIFFY	= "68:94:23:3A:74:14";
	public static final String	BT_URL				= "btspp://localhost:%s;name=thesisynth";
	
	// Network (Simon)
	public static final String	SIMON_IP		= "192.168.53.1";
	public static final int		SIMON_PORT		= 4753;
	public static final String	SIMON_NAME		= "Thesisynth Server powered by SIMON";
	
	// Audio
	public static final int 	BUFFER_SIZE		= 1000;
	public static final int 	SAMPLING_RATE 	= 44100;
	
	// Files
	private static final String PATH_ASSETS 	= "data";
	public  static final String PATH_NODE 		= PATH_ASSETS + "/nodes/%s.png";
	public  static final String PATH_UI 		= PATH_ASSETS + "/ui/%s.png";
	
	// Graphics
	public static final Color 	COLOR_NODECON	= new Color(0.2f, 0.4f, 0.5f, 1.0f);
}
