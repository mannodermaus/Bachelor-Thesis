package de.hsb.ms.syn.common.net;

import java.util.HashMap;

import de.hsb.ms.syn.common.audio.Properties;
import de.hsb.ms.syn.common.net.NetMessage.Command;

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
	 * 					NodeProperty...	-	The adjusted NodeProperty objects (can be more than one)
	 * Command SENDNODES:
	 * 					HashMap<Integer,
	 * 					NodeProperties>	-	Map structure with ID-Properties relations
	 * Command METHOD:
	 * 					String			-	Method name
	 * 					Object...		-	Method arguments in correct order
	 * Command SENDID:
	 * 					int				-	The ID of the device's connection
	 * 					float[]			-	The RGB value of the Color for this device's highlights
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
		NetMessage response = new NetMessage();
		
		switch (command) {
		case HELLO:			// No parameters
			response.addExtra(NetMessage.CMD_HELLO, "");
			break;
		case CHANGEPARAMS:	// Two parameters: int id, NodeProperty... prop
			assert args.length >= 2;
			int id = 			(Integer) args[0];
			
			response.addExtra(NetMessage.CMD_CHANGEPARAM, "");
			response.addExtra(NetMessage.EXTRA_NODEID, id);
			
			Object[] props = new Object[args.length - 1];
			for (int i = 0; i < props.length; i++) props[i] = args[i + 1];
			response.addExtra(NetMessage.EXTRA_PROPERTY_OBJECTS, props);
			
			break;
		case SENDNODES:		// One parameter: HashMap<Integer, NodeProperties> map
			assert args.length == 1;
			@SuppressWarnings("unchecked")
			HashMap<Integer, Properties> map = (HashMap<Integer, Properties>) args[0];
			
			response.addExtra(NetMessage.CMD_SENDNODES, "");
			response.addExtra(NetMessage.EXTRA_NODESTRUCTURE, map);
			break;
		case METHOD:	// Multiple parameters: String method, Object... args
			assert args.length > 0;
			String method = (String) args[0];
			Object[] arguments = new Object[args.length - 1];
			for (int i = 0; i < arguments.length; i++) arguments[i] = args[i + 1];
			
			response.addExtra(NetMessage.CMD_METHOD, "");
			response.addExtra(NetMessage.EXTRA_METHODNAME, method);
			response.addExtra(NetMessage.EXTRA_ARGS, arguments);
			break;
		case SENDID:	// Two parameters: int connectionID, float[] colorVals (3 items)
			assert args.length == 2;
			int connid = (Integer) args[0];
			float[] colorVals = (float[]) args[1];
			
			response.addExtra(NetMessage.CMD_SENDID, "");
			response.addExtra(NetMessage.EXTRA_CONNID, connid);
			response.addExtra(NetMessage.EXTRA_COLORVALS, colorVals);
			break;
		case SELECTNODE:// One parameter: int id
			assert args.length == 1;
			id = (Integer) args[0];
			
			response.addExtra(NetMessage.CMD_SELECTNODE, "");
			response.addExtra(NetMessage.EXTRA_NODEID, id);
			break;
		case BYE:	// One parameter: int connectionID
			assert args.length == 1;
			connid = (Integer) args[0];
			
			response.addExtra(NetMessage.CMD_BYE, "");
			response.addExtra(NetMessage.EXTRA_CONNID, connid);
			
			break;
		default:
			break;
		}
		
		return response;
	}
}
