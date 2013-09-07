package de.hsb.ms.syn.common.interfaces;

import java.io.Serializable;

import de.hsb.ms.syn.common.net.NetMessage;

public interface SimonServer extends Serializable {

	public void hello(SimonClient c);
	
	public void receive(NetMessage m);
	
}
