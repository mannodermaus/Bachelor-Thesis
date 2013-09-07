package de.hsb.ms.syn.common.interfaces;

import de.hsb.ms.syn.common.net.NetMessage;

/**
 * Interface for a receiver of network messages. Will be called from
 * the corresponding Connection upon message receival
 * @author Marcel
 *
 */
public interface NetMessageReceiver {

	/**
	 * Callback for new net message
	 * @param message	Message
	 */
	public void onNetMessageReceived(NetMessage message);
	
}
