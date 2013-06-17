package de.hsb.ms.syn.common.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import de.hsb.ms.syn.common.interfaces.Connection;
import de.hsb.ms.syn.common.vo.NetMessage;

/**
 * Input listener runnable for the ingoing stream of a Connection.
 * An instance of this class is given the input stream along with the associated connection
 * and listens for incoming messages on the stream. Whenever a message arrives, the connection
 * is notified via Connection.receive(String).
 * 
 * This class uses a BufferedReader to check for messages! Therefore, Connection.send() implementations
 * have to add escape characters at the end of the message to be sent to ensure that
 * it can be received!
 * @author Marcel
 *
 */
public class ConnectionInputListener implements Runnable {
	
	private ObjectInputStream stream;
	private Connection connection;
	private boolean interrupted;
	
	/**
	 * Constructor
	 * @param in	Input stream to use for listening
	 * @param c		Connection to notify of new messages
	 * @throws IOException
	 */
	public ConnectionInputListener(InputStream in, Connection c) throws IOException {
		this.stream = new ObjectInputStream(in);
		this.connection = c;
		this.interrupted = false;
	}
	
	/**
	 * Run implementation
	 */
	public void run() {
		NetMessage msg;
		try {
			while (!this.interrupted) {
				msg = (NetMessage) stream.readObject();
				connection.receive(msg);
			}
		} catch (IOException e) {
			try { stream.close(); } catch (IOException e1) { }
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void interrupt() {
		this.interrupted = true;
	}
}
