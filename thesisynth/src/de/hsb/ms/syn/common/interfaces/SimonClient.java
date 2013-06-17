package de.hsb.ms.syn.common.interfaces;

import java.io.Serializable;

import de.hsb.ms.syn.common.vo.NetMessage;

public interface SimonClient extends Serializable {

	public void receive(NetMessage m);
	
}
