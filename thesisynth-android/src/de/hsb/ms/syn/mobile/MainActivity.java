package de.hsb.ms.syn.mobile;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import de.hsb.ms.syn.R;
import de.hsb.ms.syn.common.abs.ControllerUI;
import de.hsb.ms.syn.common.interfaces.Connection;
import de.hsb.ms.syn.common.interfaces.NetCapableApplicationListener;
import de.hsb.ms.syn.mobile.helper.GdxStarterActivity;
import de.hsb.ms.syn.mobile.net.AndroidBluetoothConnection;
import de.hsb.ms.syn.mobile.net.AndroidNetMessageHandler;
import de.hsb.ms.syn.mobile.ui.CreateNodesUI;
import de.hsb.ms.syn.mobile.ui.OrientationSensorsUI;

/**
 * 
 * @author Marcel
 * 
 */
public class MainActivity extends Activity {

	// Must be reachable for GdxStarterActivity
	public static NetCapableApplicationListener program;
	public static AndroidApplicationConfiguration cfg;
	
	/** ControllerUI object with which to initialize the Controller */
	private ControllerUI ui;

	// Graphical components
	private Button button;
	private RadioGroup radioGroup;

	/** Request Action for Bluetooth Enable intent */
	private static final int REQUEST_ENABLE_BT = 2;

	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.controller_chooser);

		// Init activity components
		button = (Button) findViewById(R.id.button);
		radioGroup = (RadioGroup) findViewById(R.id.radioGroup);

		// Add click listener for button
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Get the checked resource and set the corresponding ControllerUI
				int res = radioGroup.getCheckedRadioButtonId();
				setUiFromIndex(res);
				// Initialize LibGDX context
				initApplication();
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		// Answer Bluetooth Enable request if the result code is OK
		if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_OK)
			// Start LibGDX context
			this.startGdxContext();
	}

	/**
	 * Set ControllerUI object according to entry in the activity's RadioGroup.
	 * Append to this switch-case if new UI experiments are to be added!
	 * @param resId
	 */
	private void setUiFromIndex(int resId) {
		switch (resId) {
		case R.id.radio0:
			// CreateNodesUI
			ui = new CreateNodesUI();
			break;
		case R.id.radio1:
			// OrientationSensorsUI
			ui = new OrientationSensorsUI();
			break;
		default:
			ui = null;
			break;
		}
	}

	/**
	 * Initialize the application with the given ControllerUI,
	 * creating an ApplicationCfg and finally, the LibGDX program to run
	 */
	private void initApplication() {
		
		// When no UI is set, return
		if (ui == null) {
			Toast.makeText(this, "Can't initialize with this ControllerUI", Toast.LENGTH_LONG).show();
			return;
		}
		
		// Create configuration and retrieve settings from ControllerUI's config
		cfg = new AndroidApplicationConfiguration();
		cfg.useGL20				= ui.getConfiguration().useGL20;
		cfg.useAccelerometer	= ui.getConfiguration().useAccelerometer;
		cfg.useCompass			= ui.getConfiguration().useCompass;
		cfg.useWakelock			= ui.getConfiguration().useWakelock;

		// Initialize LibGDX program (special NetCapableApplicationListener)
		program = new SynthesizerController(ui);
		// Initialize Android Handler for net callback from Connection (thread-safety)
		Handler inputHandler = new AndroidNetMessageHandler(program);
		
		// Initialize connection and feed in the LibGDX program
		// (NetCapableListener is also a NetMessageReceiver)
		// Connection connection = new AndroidSimonConnection(inputHandler);
		Connection connection = new AndroidBluetoothConnection(inputHandler);
		// Provide LibGDX with the obtained connection (may invoke connect() at will)
		program.setConnection(connection);

		// If bluetooth is disabled, prompt user to turn it on (if the config specifies
		// that it is necessary. Else, continue.
		if (!connection.isAvailable() && ui.getConfiguration().useBluetooth) {
			// Fire an intent to turn on Bluetooth
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		} else
			// If it is already activated, just start the GDX context
			this.startGdxContext();
	}

	/**
	 * Starts the GDX ApplicationListener via initialize()
	 */
	private void startGdxContext() {
		// Init LibGDX application with given program
		Intent intent = new Intent(this, GdxStarterActivity.class);
		startActivity(intent);
	}
}