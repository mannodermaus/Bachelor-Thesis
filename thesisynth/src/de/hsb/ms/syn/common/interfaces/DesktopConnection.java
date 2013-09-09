package de.hsb.ms.syn.common.interfaces;

import java.io.IOException;

import de.hsb.ms.syn.common.net.NetMessage;

/**
 * Connection sub-facade for Desktop-specific connections.
 * It extends the Connection class with several methods unique to Desktop/Host devices
 * @author Marcel
 *
 */
public abstract class DesktopConnection extends Connection {
	
	private static final long serialVersionUID = 4891894783887222840L;

	/**
	 * Broadcast a NetMessage to all mobile devices, excluding those specified in dontSendToTheseIDs
	 * @param message
	 * @param dontSendToTheseIDs
	 */
	public abstract void broadcast(NetMessage message, Integer... dontSendToTheseIDs);
	
	/**
	 * Send a NetMessage to the mobile device with the given ID
	 * @param message
	 * @param id
	 */
	public abstract void send(NetMessage message, int id);

	/**
	 * Returns the number of connected devices to this host
	 * @return
	 */
	public abstract int getConnectedCount();

	/**
	 * Disconnects a mobile device from the host by the given ID
	 * @param id
	 * @throws IOException
	 */
	public abstract void disconnect(int id) throws IOException;
}
