package de.hsb.ms.syn.desktop;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.hsb.ms.syn.common.audio.AudioAlgorithm;
import de.hsb.ms.syn.common.audio.Properties;
import de.hsb.ms.syn.common.audio.Property;
import de.hsb.ms.syn.common.net.NetMessage;
import de.hsb.ms.syn.common.net.NetMessageFactory;
import de.hsb.ms.syn.common.net.NetMessage.Command;
import de.hsb.ms.syn.common.util.Utils;
import de.hsb.ms.syn.common.vo.DraggableNode;
import de.hsb.ms.syn.common.vo.Node;

/**
 * Net message processor for the SynthesizerProcessor
 * It is notified upon a new NetMessage during the Synthesizer's render() cycle.
 * It is able to access both SynthesizerProcessor and -Renderer in order
 * to manipulate the Synthesizer's current state
 * @author Marcel
 *
 */
public class SynthesizerNetworkProcessor {

	/** Synthesizer processor */
	private SynthesizerAudioProcessor processor;
	
	/** Net Message to process ("null" most of the time) */
	private NetMessage mMessage;
	
	/**
	 * Constructor
	 * @param processor
	 * @param renderer
	 */
	public SynthesizerNetworkProcessor(SynthesizerAudioProcessor processor) {
		this.processor = processor;
	}
	
	/**
	 * Process an incoming NetMessage
	 * (this may very likely be "null", so check for that first)
	 */
	public synchronized void processNetMessage() {
		
		// Return if nothing's new
		if (mMessage == null) return;
		
		// Get the message's extras
		Set<String> extras = mMessage.getExtras();
		// Return if no extras are relevant
		if (extras.isEmpty()) return;

		// Hello command: A Smartphone has successfully connected to Synthesizer module
		if (extras.contains(NetMessage.CMD_HELLO)) {
			int id = mMessage.getSenderID();
			// Get the device's name and save it
			String deviceName = (String) mMessage.getExtra(NetMessage.EXTRA_DEVICENAME);
			SynthesizerRenderer.getInstance().addDeviceName(id, deviceName);
			
			// In case there are Nodes on the synthesizer surface, send a Sendnodes command back
			Map<Integer, Node> nodes = SynthesizerAudioProcessor.getInstance().getNodes();
			if (nodes.size() > 0) {
				NetMessage sendnodes = NetMessageFactory.create(Command.SENDNODES, Utils.makeNodePropertyStructure(nodes));
				// Send it only to the connected ID
				Synthesizer.send(sendnodes, id);
			}
		}

		// Bye command: A Smartphone has disconnected from Synthesizer module
		if (extras.contains(NetMessage.CMD_BYE)) {
			// Get the ID of the disconnected connection and remove it
			int id = mMessage.getInt(NetMessage.EXTRA_CONNID);
			Synthesizer.disconnect(id);
		}
		
		// Method command: Invoke a method on the SynthesizerProcessor of the main Synthesizer
		if (extras.contains(NetMessage.CMD_METHOD)) {
			// Invoke the method via Reflection (it's stored as the METHODNAME extra)
			String method = mMessage.getString(NetMessage.EXTRA_METHODNAME);
			// Additional method arguments are stored in EXTRA_ARGS
			@SuppressWarnings("unchecked")
			List<Serializable> args = (List<Serializable>) mMessage.getExtra(NetMessage.EXTRA_ARGS);
			// Invoke the method on the SynthesizerProcessor
			this.invokeMethodOnProcessor(method, args);
		}
		
		// Change param command: Replace properties of one Node with a new value
		if (extras.contains(NetMessage.CMD_CHANGEPARAM)) {
			// Retrieve the extras for this command
			int nodeId = mMessage.getInt(NetMessage.EXTRA_NODEID);
			Object[] objs = (Object[]) mMessage.getExtra(NetMessage.EXTRA_PROPERTY_OBJECTS);
			
			for (int i = 0; i < objs.length; i++) {
				Property property = (Property) objs[i];
				int paramId = property.id();
				// Retrieve the NodeProperties of the Node that belongs to this ID
				DraggableNode node = (DraggableNode) processor.getNodes().get(nodeId);
				AudioAlgorithm delegate = node.getAlgorithm();
				Properties props = delegate.getProperties();
				// Replace the given parameter with the also given new property for that parameter
				props.put(paramId, property);
				// Recalc this Node
				delegate.recalc();
				
				// The changed value has to be broadcast to all devices except the one that sent the ChangeParam msg in the first place
				int senderConnection = mMessage.getSenderID();
				NetMessage response = NetMessageFactory.create(Command.CHANGEPARAMS, nodeId, property);
				Synthesizer.broadcast(response, senderConnection);
			}
			
		}
		
		// Select Node command: Highlights a Node on the Desktop synthesizer
		if (extras.contains(NetMessage.CMD_SELECTNODE)) {
			// Retrieve the Node ID to be highlighted
			int id = mMessage.getInt(NetMessage.EXTRA_NODEID);
			int senderId = mMessage.getSenderID();
			// Highlight only this Node, unhighlight every other one
			processor.highlightNodeWithID(senderId, id);
		}

		// Consume the NetMessage because it has been processed by now
		mMessage = null;
	}
	
	/**
	 * Invoke the given method with given arguments using Reflection
	 * @param method
	 * @param args
	 */
	private void invokeMethodOnProcessor(String method, List<Serializable> args) {
		try {
			// Check if there are method arguments and alter the Reflection call accordingly
			if (args == null) {
				// No method arguments
				Method m = this.processor.getClass().getMethod(method);
				m.invoke(this.processor);
			} else {
				// Method arguments
				Class<?>[] classes = Utils.getClassesFromListArguments(args);
				Method m = this.processor.getClass().getMethod(method, classes);
				m.invoke(this.processor, args.toArray());
			}
		} catch (Exception e) {
			// Upon an error in the Reflection process (e.g. "No such method"), log it.
			for (StackTraceElement elem : e.getStackTrace())
				Utils.log(elem);
		}
	}

	/**
	 * Set the new netmessage
	 * @param m
	 */
	public void setMessage(NetMessage m) {
		mMessage = m;
	}
}
