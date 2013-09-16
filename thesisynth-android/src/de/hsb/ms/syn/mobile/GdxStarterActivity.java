package de.hsb.ms.syn.mobile;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.surfaceview.FillResolutionStrategy;

import de.hsb.ms.syn.common.interfaces.Connection;
import de.hsb.ms.syn.common.interfaces.NetCapableApplicationListener;
import de.hsb.ms.syn.mobile.SynthesizerController;
import de.hsb.ms.syn.mobile.net.AndroidBluetoothConnection;
import de.hsb.ms.syn.mobile.net.AndroidNetMessageHandler;

/**
 * Wrapper class for starting the LibGDX context from within Android.
 * I wanted to have a native Activity before GDX comes into play, which
 * is why this activity's main job is to start GDX. It receives an Intent
 * to do so from the MainActivity at will.
 * @author Marcel
 *
 */
public class GdxStarterActivity extends AndroidApplication {
	@Override
	public void onCreate(Bundle b) {
		super.onCreate(b);
		this.startGdx();
	}
	
	private void startGdx() {
		Log.d("thesisynth", "Start gdx");
		// Initialize LibGDX program
		NetCapableApplicationListener program = new SynthesizerController();
		
		// Initialize Android Handler for net callback from Connection (thread-safety)
		Handler inputHandler = new AndroidNetMessageHandler(program);
		
		// Initialize configuration
		AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
		cfg.useAccelerometer = true;
		cfg.useGL20 = true;
		cfg.resolutionStrategy = new FillResolutionStrategy();
		
		// Initialize connection and feed in the LibGDX program
		// Connection connection = new AndroidSimonConnection(inputHandler);
		Connection connection = new AndroidBluetoothConnection(inputHandler);
		
		// Provide LibGDX with the obtained connection
		program.setConnection(connection);
		
		// Start LibGDX
		initialize(program, cfg);
	}
}