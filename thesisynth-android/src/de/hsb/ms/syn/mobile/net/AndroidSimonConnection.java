package de.hsb.ms.syn.mobile.net;

import java.net.UnknownHostException;
import java.util.List;
import java.util.Set;

import android.os.Handler;
import android.os.Message;
import de.hsb.ms.syn.common.interfaces.AndroidConnection;
import de.hsb.ms.syn.common.interfaces.Connection;
import de.hsb.ms.syn.common.interfaces.SimonClient;
import de.hsb.ms.syn.common.interfaces.SimonServer;
import de.hsb.ms.syn.common.net.NetMessage;
import de.hsb.ms.syn.common.net.NetMessageFactory;
import de.hsb.ms.syn.common.net.NetMessage.Command;
import de.hsb.ms.syn.common.util.Constants;
import de.hsb.ms.syn.common.util.Utils;
import de.root1.simon.Lookup;
import de.root1.simon.Simon;
import de.root1.simon.SimonPublication;
import de.root1.simon.annotation.SimonRemote;
import de.root1.simon.exceptions.EstablishConnectionFailed;
import de.root1.simon.exceptions.LookupFailedException;

/**
 * Android connection using Simon
 * @author Marcel
 *
 */
@SimonRemote(value = {SimonClient.class})
public class AndroidSimonConnection extends AndroidConnection implements SimonClient {
	
	private static final long serialVersionUID = 4094496006840079281L;

	private transient Handler callback;
	
	private Lookup lookup;
	private SimonServer desktop;
	
	public AndroidSimonConnection(Handler handler) {
		this.kind = Connection.SIMON;
		this.callback = handler;
	}
	
	@Override
	public boolean isAvailable() {
		return true;
	}
	
	@Override
	public void connect() {
		
		final SimonClient self = this;
		
		new Thread(new Runnable() {
			@Override
			public void run() {
		        try {
			        List<SimonPublication> pubs = Simon.searchRemoteObjects(1000);
			        for (SimonPublication p : pubs) {
			        	if (p.getRemoteObjectName().equals(Constants.SIMON_NAME)) {
			        		lookup = Simon.createNameLookup(p.getAddress().getHostAddress(), p.getPort());
					        desktop = (SimonServer) lookup.lookup(Constants.SIMON_NAME);
							Utils.log("Simon android endpoint connected to desktop. Sending Hello...");
							desktop.hello(self);
							
							NetMessage m = NetMessageFactory.create(Command.HELLO);
							send(m);
							
							Utils.log("Simon android endpoint sent Hello.");
					        break;
			        	}
			        }
				} catch (LookupFailedException e) {
					e.printStackTrace();
				} catch (EstablishConnectionFailed e) {
					e.printStackTrace();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	@Override
	public boolean isConnected() {
		return (desktop != null) && (lookup != null);
	}
	
	@Override
	public void send(NetMessage message) {
		Utils.log("Sending " + message);
		
		Set<String> keys = message.getExtras();
		for (String key : keys) {
			Utils.log("Extra: key = " + key + ", key class = " + key.getClass() + ", value = " + message.getExtra(key) + ", value class = " + message.getExtra(key).getClass());
		}
		
		desktop.receive(message);
	}
	
	@Override
	public void receive(NetMessage message) {
		Message m = new Message();
		m.obj = message;
		callback.sendMessage(m);
	}
	
	@Override
	public void close() {
		lookup.release(desktop);
		desktop = null;
		Utils.log("Simon android endpoint closed.");
	}

	@Override
	public String getDescription() {
		return "Simon";
	}

	@Override
	public String getDeviceName() {
		// TODO Auto-generated method stub
		return null;
	}
}
