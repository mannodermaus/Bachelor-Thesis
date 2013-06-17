package de.hsb.ms.syn.desktop.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

import de.hsb.ms.syn.common.interfaces.Connection;
import de.hsb.ms.syn.common.interfaces.NetMessageReceiver;
import de.hsb.ms.syn.common.net.ConnectionInputListener;
import de.hsb.ms.syn.common.util.Constants;
import de.hsb.ms.syn.common.util.Utils;
import de.hsb.ms.syn.common.vo.NetMessage;

/**
 * Desktop connection using bluetooth
 * @author Marcel
 *
 */
public class DesktopBluetoothConnection implements Connection {
	
	private static final long serialVersionUID = 2830869698883336818L;

	private NetMessageReceiver callback;
	
    private UUID uuid = new UUID("1101", true);
    
    private StreamConnection connection;
    private Thread listeningThread;
    private ObjectOutputStream outStream;
    private InputStream inStream;
	
	public DesktopBluetoothConnection(NetMessageReceiver callback) {
		this.callback = callback;
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
				try {
					String url = String.format(Constants.BT_URL, uuid.toString());
					streamConnNotifier = (StreamConnectionNotifier) Connector.open(url);

			        //Wait for client connection
			        System.out.println("\nNet thread started and waiting for client...");
			        connection = streamConnNotifier.acceptAndOpen();
			        
			        OutputStream out = connection.openOutputStream();
			        outStream = new ObjectOutputStream(out);
			        
			        inStream = connection.openDataInputStream();
			        listeningThread = new Thread(new ConnectionInputListener(inStream, c));
			        listeningThread.start();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	@Override
	public boolean isConnected() {
		return (connection != null);
	}

	@Override
	public void send(NetMessage message) {
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
	public void receive(NetMessage message) {
		callback.onNetMessageReceived(message);
	}

	@Override
	public void close() {
		try {
			if (outStream != null)
				outStream.close();
			if (inStream != null)
				inStream.close();
			if (listeningThread != null)
				listeningThread.interrupt();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getDescription() {
		return "Bluetooth";
	}
}
