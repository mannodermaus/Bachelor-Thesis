package de.hsb.ms.syn.mobile;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import de.hsb.ms.syn.R;

/**
 * Android starter class for Thesisynth.
 * Using the Android backend of the framework, it initializes an instance of the SynthesizerController
 * ApplicationListener inside of a LibGDX context.
 * @author Marcel
 * 
 */
public class MainActivity extends Activity {

	/** Request code for the "switch on bluetooth" Intent */
	private static final int REQUEST_ENABLE_BT = 2;
	
	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.controller_chooser);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		this.initApplication();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		// Answer "Bluetooth Enable" request if the result code is OK
		if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_OK)
			// Start LibGDX context
			this.startGdxContext();
	}
	
	/**
	 * Initialize the application with the given ControllerUI,
	 * creating an ApplicationCfg and finally, the LibGDX program to run
	 */
	private void initApplication() {
		// Check if bluetooth is turned on. If not, send an intent to switch it on
		BluetoothAdapter bt = BluetoothAdapter.getDefaultAdapter();
		if ((bt != null) && (bt.isEnabled())) {
			// If it is already activated, just start the GDX context
			this.startGdxContext();
		} else {
			// Fire an intent to turn on Bluetooth
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
	}

	/**
	 * Starts the GDX ApplicationListener via initialize()
	 */
	private void startGdxContext() {
		// Start a helper Activity that holds the LibGDX context
		// (the application checks for Bluetooth availability and starts an intent
		// if it is switched off - an AndroidApplication needs to have an ApplicationListener
		// right away, though.)
		Intent intent = new Intent(this, GdxStarterActivity.class);
		startActivity(intent);
	}
}