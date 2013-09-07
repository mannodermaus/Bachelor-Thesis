package de.hsb.ms.syn.common.interfaces;

import java.io.IOException;

import de.hsb.ms.syn.common.net.NetMessage;

public abstract class DesktopConnection extends Connection {
	
	private static final long serialVersionUID = 4891894783887222840L;

	public abstract void broadcast(NetMessage message, Integer... dontSendToTheseIDs);
	
	public abstract void send(NetMessage message, int id);

	public abstract int getConnectedCount();

	public abstract void disconnect(int id) throws IOException;
}
