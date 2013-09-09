package de.hsb.ms.syn.common.interfaces;

import java.io.Serializable;

import de.hsb.ms.syn.common.net.NetMessage;

/**
 * Simon Client Interface (unused)
 * @author Marcel
 *
 */
public interface SimonClient extends Serializable {

	public void receive(NetMessage m);
	
}
