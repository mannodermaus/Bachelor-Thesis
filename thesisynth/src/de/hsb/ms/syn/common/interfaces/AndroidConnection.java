package de.hsb.ms.syn.common.interfaces;

import de.hsb.ms.syn.common.net.NetMessage;

/**
 * Connection sub-facade for Android-specific connections.
 * It extends the Connection class with a send() method unique to Android devices
 * @author Marcel
 *
 */
public abstract class AndroidConnection extends Connection {
	
	private static final long serialVersionUID = 7652620176373542354L;

	/**
	 * Send a message to the other endpoint
	 * @param message
	 */
	public abstract void send(NetMessage message);
}
