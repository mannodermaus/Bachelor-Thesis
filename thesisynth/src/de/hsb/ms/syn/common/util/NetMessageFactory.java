package de.hsb.ms.syn.common.util;

import de.hsb.ms.syn.common.exc.IllegalNetMessageCommandException;
import de.hsb.ms.syn.common.vo.NetMessage;
import de.hsb.ms.syn.common.vo.NodeProperty;

public final class NetMessageFactory {

	public static NetMessage create(String type, Object... args) throws IllegalNetMessageCommandException {
		NetMessage response = new NetMessage();
		response.addExtra(type, "");
		
		// Add the extras according to the type
		if (type.equals(NetMessages.CMD_HELLO)) {
		
		} else if (type.equals(NetMessages.CMD_CHANGEPARAM)) {
			// Two parameters: int id, NodeProperty prop
			int id = 			(Integer) args[0];
			NodeProperty prop = (NodeProperty) args[1];
			response.addExtra(NetMessages.EXTRA_PARAMNUMBER, prop.id());
			response.addExtra(NetMessages.EXTRA_NODEID, id);
			response.addExtra(NetMessages.EXTRA_PROPERTY, prop);
		} else if (type.equals(NetMessages.CMD_SENDNODES)) {
			
			
		} else if (type.equals(NetMessages.CMD_METHOD)) {
			
			
		} else if (type.equals(NetMessages.CMD_SENDID)) {
			
			
		} else if (type.equals(NetMessages.CMD_SELECTNODE)) {
			
			
		} else if (type.equals(NetMessages.CMD_BYE)) {
			
		} else
			throw new IllegalNetMessageCommandException(type);
		
		return response;
	}
	
}
