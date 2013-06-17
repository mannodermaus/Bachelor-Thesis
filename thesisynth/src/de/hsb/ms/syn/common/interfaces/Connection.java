package de.hsb.ms.syn.common.interfaces;

import java.io.Serializable;

import de.hsb.ms.syn.common.vo.NetMessage;

/**
 * Interface representing a connection endpoint, either implemented
 * by the smart device or the PC end (and by different means depending
 * on the kind of connection that is attempted)
 * @author Marcel
 *
 */
public interface Connection extends Serializable {
	
	/**
	 * Check if the connection's infrastructure is available
	 * @return
	 */
	public boolean isAvailable();
	
	/**
	 * Connect to other endpoint using this connection
	 */
	public void connect();
	
	/**
	 * Check if the connection is currently established
	 * @return
	 */
	public boolean isConnected();
	
	/**
	 * Send a message to the other endpoint
	 * @param message
	 */
	public void send(NetMessage message);
	
	/**
	 * Receive a message from the other endpoint
	 * @param message
	 */
	public void receive(NetMessage message);
	
	/**
	 * Close the connection
	 */
	public void close();
	
	/**
	 * Get description of this connection's type
	 */
	public String getDescription();
}
