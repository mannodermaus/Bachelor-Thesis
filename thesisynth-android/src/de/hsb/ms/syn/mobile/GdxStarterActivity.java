package de.hsb.ms.syn.mobile;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.surfaceview.FillResolutionStrategy;

import de.hsb.ms.syn.common.interfaces.AndroidConnection;
import de.hsb.ms.syn.common.interfaces.NetCapableApplicationListener;
import de.hsb.ms.syn.common.net.NetMessage;
import de.hsb.ms.syn.common.net.NetMessage.Command;
import de.hsb.ms.syn.common.net.NetMessageFactory;
import de.hsb.ms.syn.common.util.Constants;
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
	
	private IntentFilter btIntentFilter;
	private BroadcastReceiver btReceiver;
	private boolean receiverRegistered = false;
	
	private NetCapableApplicationListener program;
	private Handler inputHandler;
	private AndroidConnection connection;
	
	@Override
	public void onCreate(Bundle b) {
		super.onCreate(b);
		
		// Initialize broadcast receiver for Bluetooth actions
		btReceiver = new BroadcastReceiver() {
			
			/* Flag indicating if the Thesisynth service was found during discovery */
			private boolean foundService = false;
			
			@Override
			public void onReceive(Context context, Intent i) {
				String action = i.getAction();
				
				if (action.equals(BluetoothDevice.ACTION_FOUND)) {
					// Bluetooth device found through discovery
					BluetoothDevice device = i.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					Log.d(Constants.LOG_TAG, "Bluetooth device found: " + device.getName());
					// Fetch UUIDs that are supported by this device (look for Thesisynth service)
					if (device.fetchUuidsWithSdp()) {
						Log.d(Constants.LOG_TAG, "  " + device.getName() + " has UUIDs. Fetching...");
					}
				} else if (action.equals(BluetoothDevice.ACTION_UUID)) {
					// Bluetooth device UUID fetching returned results
					Parcelable[] uuids = i.getParcelableArrayExtra(BluetoothDevice.EXTRA_UUID);
					BluetoothDevice device = i.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					for (int c = 0; c < uuids.length; c++) {
						// Compare UUID to the Thesisynth UUID
						if (uuids[c].toString().equals(Constants.BT_SERVICE_UUID.toString())) {
							Log.d(Constants.LOG_TAG, "  Matching UUID found. Connecting...");
							this.foundService = true;
							// Matching UUIDs. Connect to this remote device
							((AndroidBluetoothConnection) connection).connectToRemoteDevice(device);
							unregisterReceiver(this);
							receiverRegistered = false;
						}
					}
				} else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
					Log.d(Constants.LOG_TAG, "Bluetooth discovery started.");
				} else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
					Log.d(Constants.LOG_TAG, "Bluetooth discovery finished.");
					// If no Thesisynth was found, display a message
					if (!this.foundService) {
						NetMessage message = NetMessageFactory.create(Command.SHOWCONNECTIONFAILED);
						connection.receive(message);
					}
				}
			}
		};
		
		// Initialize intent filter for Bluetooth actions
		btIntentFilter = new IntentFilter();
		btIntentFilter.addAction(BluetoothDevice.ACTION_FOUND);
		btIntentFilter.addAction(BluetoothDevice.ACTION_UUID);
		btIntentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		btIntentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		
		// Initialize LibGDX program
		this.program = new SynthesizerController();
		
		// Initialize Android Handler for net callback from Connection (thread-safety)
		this.inputHandler = new AndroidNetMessageHandler(program);
		
		// Initialize connection that is being fed into the LibGDX program
		// Connection connection = new AndroidSimonConnection(inputHandler);
		this.connection = new AndroidBluetoothConnection(inputHandler);
		
		// Provide LibGDX with the obtained connection
		this.program.setConnection(this.connection);
		
		// Start LibGDX program
		this.startGdx();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		this.registerReceiver(btReceiver, btIntentFilter);
		receiverRegistered = true;
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if (receiverRegistered) {
			this.unregisterReceiver(btReceiver);
			receiverRegistered = false;
		}
	}
	
	/**
	 * Initialize LibGDX context for Android devices
	 */
	private void startGdx() {
		
		// Initialize configuration
		AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
		cfg.useAccelerometer = true;
		cfg.useGL20 = true;
		cfg.resolutionStrategy = new FillResolutionStrategy();
		
		// Start LibGDX
		initialize(program, cfg);
	}
}