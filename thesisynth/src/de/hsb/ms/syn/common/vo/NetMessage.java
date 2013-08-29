package de.hsb.ms.syn.common.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.hsb.ms.syn.common.util.NetMessages;

/**
 * Value object wrapper for messages that are sent over network. Every
 * NetMessage has got a String message and may optionally have extra data (Map
 * format) to be accessed (this implementation is similar to Android's Bundle
 * class). In order to instantiate NetMessage objects, it is advised
 * to use the NetMessageFactory class by invoking its create() method.
 * 
 * @author Marcel
 * 
 */
public class NetMessage implements Serializable {
	private static final long serialVersionUID = -4504362258231786080L;

	// Map of extras sent along with the Message (@see NetMessages)
	private Map<String, Serializable> map;

	// ID of the connection that sent this NetMessage (Desktop is -1)
	private int senderID = -1;
	
	/**
	 * Returns the value for the given key in the NetMessage's map, or null if
	 * the Map doesn't exist
	 * 
	 * @param key
	 * @return
	 */
	public Object getExtra(String key) {
		return (map == null) ? null : map.get(key.toLowerCase());
	}

	/**
	 * Returns the Map's keySet in order to access the NetMessage's contents, or
	 * null if it doesn't contain any
	 * 
	 * @return
	 */
	public Set<String> getExtras() {
		return (map == null) ? null : map.keySet();
	}

	/**
	 * Convenience method to retrieve an Integer extra
	 * 
	 * @param key
	 * @return
	 */
	public Integer getInt(String key) {
		Object o = getExtra(key);
		return (Integer) ((o == null) ? 0 : o);
	}

	/**
	 * Convenience method to retrieve a String extra
	 * 
	 * @param key
	 * @return
	 */
	public String getString(String key) {
		Object o = getExtra(key);
		return (String) ((o == null) ? "" : o);
	}

	@SuppressWarnings("unchecked")
	/**
	 * Adds an extra to the NetMessage's Map
	 * @param key
	 * @param value
	 */
	public void addExtra(String key, Serializable value) {
		// Create the Map in the first place if this is the first Extra to be
		// stored
		if (map == null)
			map = new HashMap<String, Serializable>();

		// Special handling for EXTRA_ARGS because it may be multiple arguments
		if (key.equals(NetMessages.EXTRA_ARGS)) {

			// Save the passed-in argument
			Serializable arg = value;

			// Create a new list of arguments if not already there
			// (if more than one argument are passed in via
			// addExtra(Messages.EXTRA_ARGS),
			// this list will be expanded!)
			if (!map.containsKey(NetMessages.EXTRA_ARGS))
				value = new ArrayList<Serializable>();
			else
				value = map.get(NetMessages.EXTRA_ARGS);

			// Add the argument to this list
			((List<Serializable>) value).add(arg);
		}
		// Put the entry in the extra Map
		map.put(key.toLowerCase(), value);
	}

	/**
	 * toString override
	 * 
	 * @return
	 */
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append("'NetMessage' { map = ");
		if (map == null)
			s.append("null }");
		else
			s.append(map.toString() + " }");
		return s.toString();
	}

	public void setID(int id) {
		this.senderID = id;
	}

	public int getSenderID() {
		return this.senderID;
	}
}