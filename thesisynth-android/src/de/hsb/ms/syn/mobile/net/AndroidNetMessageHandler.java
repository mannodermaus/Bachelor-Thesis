package de.hsb.ms.syn.mobile.net;

import android.os.Handler;
import android.os.Message;
import de.hsb.ms.syn.common.interfaces.NetMessageReceiver;
import de.hsb.ms.syn.common.net.NetMessage;

public class AndroidNetMessageHandler extends Handler {
	
	private NetMessageReceiver rec;
	
	public AndroidNetMessageHandler(NetMessageReceiver rec) {
		this.rec = rec;
	}
	
	@Override
	public void handleMessage(Message m) {
		rec.onNetMessageReceived((NetMessage) m.obj);
	}

}