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

import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

import de.hsb.ms.syn.common.abs.Connection;
import de.hsb.ms.syn.common.abs.DesktopConnection;
import de.hsb.ms.syn.common.interfaces.NetMessageReceiver;
import de.hsb.ms.syn.common.net.ConnectionInputListener;
import de.hsb.ms.syn.common.util.Constants;
import de.hsb.ms.syn.common.util.NetMessageFactory;
import de.hsb.ms.syn.common.util.NetMessages.Command;
import de.hsb.ms.syn.common.util.Utils;
import de.hsb.ms.syn.common.vo.NetMessage;

/**
 * Desktop connection using bluetooth
 * @author Marcel
 *
 */
public class DesktopBluetoothConnection extends DesktopConnection {
	
	private static final long serialVersionUID = 2830869698883336818L;

	private NetMessageReceiver callback;
	
    private UUID uuid = new UUID("1101", true);

	private static int connectionIDs = 0;
    private Map<Integer, StreamConnection> connections;
    private Map<Integer, Thread> listeningThreads;
    private Map<Integer, ObjectOutputStream> outStreams;
    private Map<Integer, InputStream> inStreams;
	
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
				StreamConnectionNotifier streamConnNotifier;
					String url = String.format(Constants.BT_URL, uuid.toString());
					try {
						streamConnNotifier = (StreamConnectionNotifier) Connector.open(url);
					} catch (IOException e1) {
						e1.printStackTrace();
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
					        
					        InputStream inStream = connection.openDataInputStream();
					        Thread listeningThread = new Thread(new ConnectionInputListener(inStream, c));
					        listeningThread.start();
					        
					        // Put references to this new connection in the respective map objects
					        connections.put(newID, connection);
					        outStreams.put(newID, outStream);
					        inStreams.put(newID, inStream);
					        listeningThreads.put(newID, listeningThread);
					        
					        // Send the ID of the connected device back
					        NetMessage response = NetMessageFactory.create(Command.SENDID, newID);
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
				e.printStackTrace();
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
		if (outStream != null)
			outStream.close();
		if (inStream != null)
			inStream.close();
		if (listeningThread != null)
			listeningThread.interrupt();
		outStreams.remove(id);
		inStreams.remove(id);
		listeningThreads.remove(id);
		connections.remove(id);
	}

	@Override
	public String getDescription() {
		return "Bluetooth";
	}

	@Override
	public int getConnectedCount() {
		return connections.size();
	}
}
