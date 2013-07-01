package de.hsb.ms.syn.desktop.net;

import java.io.IOException;
import java.net.UnknownHostException;

import de.hsb.ms.syn.common.abs.Connection;
import de.hsb.ms.syn.common.abs.DesktopConnection;
import de.hsb.ms.syn.common.interfaces.NetMessageReceiver;
import de.hsb.ms.syn.common.interfaces.SimonClient;
import de.hsb.ms.syn.common.interfaces.SimonServer;
import de.hsb.ms.syn.common.util.Constants;
import de.hsb.ms.syn.common.util.Utils;
import de.hsb.ms.syn.common.vo.NetMessage;
import de.root1.simon.Registry;
import de.root1.simon.Simon;
import de.root1.simon.annotation.SimonRemote;
import de.root1.simon.exceptions.NameBindingException;

/**
 * Desktop connection using Simon
 * @author Marcel
 *
 */
@SimonRemote(value = {SimonServer.class})
public class DesktopSimonConnection extends DesktopConnection implements SimonServer {
	
	private static final long serialVersionUID = -5408519580495473096L;

	private NetMessageReceiver callback;
	
	private Registry registry;
	
	private SimonClient smartphone;
	
	public DesktopSimonConnection(NetMessageReceiver callback) {
		this.kind = Connection.SIMON;
		this.callback = callback;
	}
	
	@Override
	public boolean isAvailable() {
		return true;
	}

	@Override
	public void connect() {
		try {
			registry = Simon.createRegistry(Constants.SIMON_PORT);
			registry.bindAndPublish(Constants.SIMON_NAME, this);
			Utils.log("Simon desktop endpoint bound & published.");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NameBindingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean isConnected() {
		return (registry != null && registry.isRunning() && smartphone != null);
	}

	@Override
	public void hello(SimonClient c) {
		Utils.log("Smartphone detected.");
		smartphone = c;
		
	}

	@Override
	public void send(NetMessage message, int id) {
		smartphone.receive(message);
	}

	@Override
	public void broadcast(NetMessage message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receive(NetMessage message) {
		Utils.log("Received " + message);
		// Otherwise, let the callback handle the message
		callback.onNetMessageReceived(message);
	}

	@Override
	public void close() {
		registry.unpublish(Constants.SIMON_NAME);
		registry.unbind(Constants.SIMON_NAME);
		registry.stop();
		smartphone = null;
		Utils.log("Simon desktop endpoint released.");
	}

	@Override
	public String getDescription() {
		return "SIMON";
	}

	@Override
	public int getConnectedCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void disconnect(int id) throws IOException {
		// TODO Auto-generated method stub
		
	}
}
