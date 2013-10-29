package de.hsb.ms.syn.desktop.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

import de.hsb.ms.syn.common.interfaces.Connection;
import de.hsb.ms.syn.common.interfaces.DesktopConnection;
import de.hsb.ms.syn.common.interfaces.NetMessageReceiver;
import de.hsb.ms.syn.common.net.ConnectionInputListener;
import de.hsb.ms.syn.common.net.NetMessage;
import de.hsb.ms.syn.common.net.NetMessageFactory;
import de.hsb.ms.syn.common.net.NetMessage.Command;
import de.hsb.ms.syn.common.util.Constants;
import de.hsb.ms.syn.common.util.Utils;
import de.hsb.ms.syn.desktop.SynthesizerRenderer;

/**
 * Desktop connection using bluetooth
 * @author Marcel
 *
 */
public class DesktopBluetoothConnection extends DesktopConnection {
	
	private static final long serialVersionUID = 2830869698883336818L;

	/** LibGDX context to refer to when sending received messages */
	private NetMessageReceiver callback;
	
	/** Universally unique identifier of this connection */
    private UUID uuid = new UUID("1101", true);
    
    /** Counter used to assign ID numbers to mobile clients */
	private static int connectionIDs = 0;
	
	/** Notification object that opens connections for mobile devices */
	StreamConnectionNotifier streamConnNotifier;
	/** Connection stream map to each connected mobile client */
    private Map<Integer, StreamConnection> connections;
    /** Listener thread (on input stream) map for each connected mobile client */
    private Map<Integer, Thread> listeningThreads;
    /** Output stream map for each mobile client */
    private Map<Integer, ObjectOutputStream> outStreams;
    /** Input stream map for each mobile client */
    private Map<Integer, InputStream> inStreams;
	
    /**
     * Constructor
     * @param callback	LibGDX context object to be notified of incoming NetMessages
     */
	public DesktopBluetoothConnection(NetMessageReceiver callback) {
		this.kind = Connection.BLUETOOTH;
		this.callback = callback;

		this.connections = new HashMap<Integer, StreamConnection>();
		this.listeningThreads = new HashMap<Integer, Thread>();
		this.outStreams = new HashMap<Integer, ObjectOutputStream>();
		this.inStreams = new HashMap<Integer, InputStream>();
	}
	
	@Override
	public boolean isAvailable() {
		return true;
	}

	@Override
	public void connect() {
		// Don't make this block
		final Connection c = this;
		new Thread(new Runnable() {
			@Override
			public void run() {
					String url = String.format(Constants.BT_URL, uuid.toString());
					try {
						LocalDevice.getLocalDevice().setDiscoverable(DiscoveryAgent.GIAC);
						streamConnNotifier = (StreamConnectionNotifier) Connector.open(url);
					} catch (IOException e1) {
						Utils.log("Can't connect using Bluetooth. " + e1.getMessage());
						return;
					}
					
					while (true) {
						try {
					        // Wait for client connection
					        System.out.println("\nNet thread waiting for client...");
					        StreamConnection connection = streamConnNotifier.acceptAndOpen();
					        int newID = ++connectionIDs;
					        System.out.println("\nClient found and assigned ID " + newID);
					        
					        OutputStream out = connection.openOutputStream();
					        ObjectOutputStream outStream = new ObjectOutputStream(out);
					        outStream.flush();
					        
					        InputStream inStream = connection.openDataInputStream();
					        Thread listeningThread = new Thread(new ConnectionInputListener(inStream, c));
					        listeningThread.start();
					        
					        // Put references to this new connection in the respective map objects
					        connections.put(newID, connection);
					        outStreams.put(newID, outStream);
					        inStreams.put(newID, inStream);
					        listeningThreads.put(newID, listeningThread);
					        
					        // Send the ID of the connected device back
					        float[] colorVals = SynthesizerRenderer.getInstance().makeColorForConnection(newID);
					        NetMessage response = NetMessageFactory.create(Command.SENDID, newID, colorVals);
							send(response, newID);
						
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
			}
		}).start();
	}

	@Override
	public boolean isConnected() {
		return (connections.size() > 0);
	}

	@Override
	public void send(NetMessage message, int id) {
		ObjectOutputStream outStream = outStreams.get(id);
		if (this.isConnected()) {
			try {
				outStream.writeObject(message);
				outStream.flush();
			} catch (IOException e) {
				// Can happen if the mobile client crashes, in which case the host
				// doesn't notice that it is gone. Remove the connection manually
				Utils.log("Can't send to device with ID " + id + ": " + e.getMessage() + " ; Removing this connection...");
				try {
					this.disconnect(id);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		} else {
			Utils.log("Not connected: Can't send NetMessage " + message);
		}
		
	}

	@Override
	public void broadcast(NetMessage message, Integer... dontSendToTheseIDs) {
		// Fill a list with all Connection IDs available
		List<Integer> devicesToCheck = new ArrayList<Integer>();
		devicesToCheck.addAll(connections.keySet());
		// Remove all Connection IDs passed in as excluded
		devicesToCheck.removeAll(Arrays.asList(dontSendToTheseIDs));
		// Send to all remaining devices
		for (int id : devicesToCheck) {
			send(message, id);
		}
	}

	@Override
	public void receive(NetMessage message) {
		callback.onNetMessageReceived(message);
	}

	@Override
	public void close() {
		try {
			for (int key : connections.keySet()) {
				this.disconnect(key);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void disconnect(int id) throws IOException {
		ObjectOutputStream outStream = outStreams.get(id);
		InputStream inStream = inStreams.get(id);
		Thread listeningThread = listeningThreads.get(id);
		
		outStreams.remove(id);
		inStreams.remove(id);
		listeningThreads.remove(id);
		connections.remove(id);
		
		try {
			if (outStream != null) outStream.close();
			if (inStream != null) inStream.close();
		} catch (IOException e) {
			// In case the connection was forcefully removed, the streams won't be closeable
		}
		if (listeningThread != null) listeningThread.interrupt();
		
		SynthesizerRenderer.getInstance().removeColorForConnection(id);
	}

	@Override
	public String getDescription() {
		return "Bluetooth";
	}

	@Override
	public int getConnectedCount() {
		return connections.size();
	}

	@Override
	public String getDeviceName() {
		String name;
		try {
			name = LocalDevice.getLocalDevice().getFriendlyName();
		} catch (BluetoothStateException e) {
			name = "Unnamed host";
		}
		return name;
	}
}
