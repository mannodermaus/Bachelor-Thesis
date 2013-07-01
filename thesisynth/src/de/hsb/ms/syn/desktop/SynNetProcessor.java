package de.hsb.ms.syn.desktop;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import de.hsb.ms.syn.common.util.NetMessages;
import de.hsb.ms.syn.common.util.Utils;
import de.hsb.ms.syn.common.vo.NetMessage;
import de.hsb.ms.syn.common.vo.NodeProperties;
import de.hsb.ms.syn.common.vo.NodeProperty;
import de.hsb.ms.syn.desktop.abs.Delegate;
import de.hsb.ms.syn.desktop.abs.DraggableNode;

/**
 * Net message processor for the SynthesizerProcessor
 * It is notified upon a new NetMessage during the Synthesizer's render() cycle.
 * It is able to access both SynthesizerProcessor and -Renderer in order
 * to manipulate the Synthesizer's current state
 * @author Marcel
 *
 */
public class SynNetProcessor {

	/** Synthesizer processor */
	private SynAudioProcessor processor;
	
	/** Net Message to process ("null" most of the time) */
	private NetMessage mMessage;
	
	/**
	 * Constructor
	 * @param processor
	 * @param renderer
	 */
	public SynNetProcessor(SynAudioProcessor processor) {
		this.processor = processor;
	}
	
	/**
	 * Process an incoming NetMessage
	 * (this may very likely be "null", so check for that first)
	 */
	public synchronized void processMessage() {
		// Return if nothing's new
		if (mMessage == null) return;
		// Get the message's extras
		Set<String> extras = mMessage.getExtras();
		// Return if no extras are relevant
		if (extras.isEmpty()) return;

		// Hello command: A Smartphone has successfully connected to Synthesizer module
		if (extras.contains(NetMessages.CMD_HELLO)) {
		}

		// Bye command: A Smartphone has disconnected from Synthesizer module
		if (extras.contains(NetMessages.CMD_BYE)) {
			try {
				// Get the ID of the disconnected connection and remove it
				int id = mMessage.getInt(NetMessages.EXTRA_CONNID);
				Synthesizer.connection.disconnect(id);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// Method command: Invoke a method on the SynthesizerProcessor of the main Synthesizer
		if (extras.contains(NetMessages.CMD_METHOD)) {
			// Invoke the method via Reflection (it's stored as the CMD_METHOD argument)
			String method = mMessage.getString(NetMessages.CMD_METHOD);
			// Additional method arguments are stored in EXTRA_ARGS
			@SuppressWarnings("unchecked")
			List<Serializable> args = (List<Serializable>) mMessage.getExtra(NetMessages.EXTRA_ARGS);
			// Invoke the method on the SynthesizerProcessor
			this.invokeMethodOnProcessor(method, args);
		}
		
		// Change param command: Replace a property of one Node with a new value
		if (extras.contains(NetMessages.CMD_CHANGEPARAM)) {
			// Retrieve the extras for this command
			int id = mMessage.getInt(NetMessages.EXTRA_NODEID);
			int param = mMessage.getInt(NetMessages.EXTRA_PARAMNUMBER);
			NodeProperty p = (NodeProperty) mMessage.getExtra(NetMessages.EXTRA_PROPERTY);
			
			// Retrieve the NodeProperties of the Node that belongs to this ID
			DraggableNode n = (DraggableNode) processor.getNodes().get(id);
			Delegate d = n.getDelegate();
			NodeProperties props = d.getProperties();
			// Replace the given parameter with the also given new property for that parameter
			props.put(param, p);
			// Recalc this Node
			d.recalc();
		}
		
		// Select Node command: Highlights a Node on the Desktop synthesizer
		if (extras.contains(NetMessages.CMD_SELECTNODE)) {
			// Retrieve the Node ID to be highlighted
			int id = mMessage.getInt(NetMessages.CMD_SELECTNODE);
			// Highlight only this Node, unhighlight every other one
			processor.highlightNodeWithID(id);
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
