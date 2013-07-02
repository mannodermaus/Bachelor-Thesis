package de.hsb.ms.syn.common.util;

import java.util.HashMap;

import de.hsb.ms.syn.common.util.NetMessages.Command;
import de.hsb.ms.syn.common.vo.NetMessage;
import de.hsb.ms.syn.common.vo.NodeProperties;
import de.hsb.ms.syn.common.vo.NodeProperty;

/**
 * This class is utilized to properly instantiate NetMessages to be sent over network.
 * A direct call to new NetMessage() will result in an IllegalAccessException, alas it
 * is necessary to use the create() method of this class.
 * @author Marcel
 *
 */
public final class NetMessageFactory {

	/**
	 * Create a NetMessage object with the given Command and the specified arguments.
	 * These arguments are listed below:
	 * 
	 * Command HELLO:
	 * 					No parameters necessary
	 * Command CHANGEPARAM:
	 * 					int				-	The ID of the Node affected by the property change
	 * 					NodeProperty	-	The adjusted NodeProperty object
	 * Command SENDNODES:
	 * 					HashMap<Integer,
	 * 					NodeProperties>	-	Map structure with ID-Properties relations
	 * Command METHOD:
	 * 					String			-	Method name
	 * 					Object...		-	Method arguments in correct order
	 * Command SENDID:
	 * 					int				-	The ID of the device's connection
	 * Command SELECTNODE:
	 * 					int				-	The ID of the Node to select
	 * Command BYE:
	 * 					int				-	The ID of the device that has disconnected
	 * 
	 * @param command	Command of the NetMessage
	 * @param args		Arguments needed to construct the NetMessage (see above)
	 * @return			NetMessage object to send over network
	 */
	public static NetMessage create(Command command, Object... args) {
		NetMessage response;
		try {
			response = new NetMessage();
		} catch (IllegalAccessException e) {
			// Can't occur because NetMessageFactory is allowed access at any time, but oh well...
			return null;
		}
		
		switch (command) {
		case HELLO:			// No parameters
			response.addExtra(NetMessages.CMD_HELLO, "");
			break;
		case CHANGEPARAM:	// Two parameters: int id, NodeProperty prop
			assert args.length == 2;
			int id = 			(Integer) args[0];
			NodeProperty prop = (NodeProperty) args[1];
			
			response.addExtra(NetMessages.CMD_CHANGEPARAM, "");
			response.addExtra(NetMessages.EXTRA_PARAMNUMBER, prop.id());
			response.addExtra(NetMessages.EXTRA_NODEID, id);
			response.addExtra(NetMessages.EXTRA_PROPERTY, prop);
			break;
		case SENDNODES:		// One parameter: HashMap<Integer, NodeProperties> map
			assert args.length == 1;
			@SuppressWarnings("unchecked")
			HashMap<Integer, NodeProperties> map = (HashMap<Integer, NodeProperties>) args[0];
			
			response.addExtra(NetMessages.CMD_SENDNODES, "");
			response.addExtra(NetMessages.EXTRA_NODESTRUCTURE, map);
			break;
		case METHOD:	// Multiple parameters: String method, Object... args
			assert args.length > 0;
			String method = (String) args[0];
			Object[] arguments = new Object[args.length - 1];
			for (int i = 0; i < arguments.length; i++) arguments[i] = args[i + 1];
			
			response.addExtra(NetMessages.CMD_METHOD, "");
			response.addExtra(NetMessages.EXTRA_METHODNAME, method);
			response.addExtra(NetMessages.EXTRA_ARGS, arguments);
			break;
		case SENDID:	// One parameter: int connectionID
			assert args.length == 1;
			int connid = (Integer) args[0];
			
			response.addExtra(NetMessages.CMD_SENDID, "");
			response.addExtra(NetMessages.EXTRA_CONNID, connid);
			break;
		case SELECTNODE:// One parameter: int id
			assert args.length == 1;
			id = (Integer) args[0];
			
			response.addExtra(NetMessages.CMD_SELECTNODE, "");
			response.addExtra(NetMessages.EXTRA_NODEID, id);
			break;
		case BYE:	// One parameter: int connectionID
			assert args.length == 1;
			connid = (Integer) args[0];
			
			response.addExtra(NetMessages.CMD_BYE, "");
			response.addExtra(NetMessages.EXTRA_CONNID, connid);
			
			break;
		default:
			break;
		}
		
		return response;
	}
}
