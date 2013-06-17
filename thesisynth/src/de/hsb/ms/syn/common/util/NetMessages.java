package de.hsb.ms.syn.common.util;

/**
 * Network messages and arguments for Synth-Controller communication
 * @author Marcel
 *
 */
public abstract class NetMessages {

	/* FROM SMARTPHONE TO DESKTOP */
	
	/** Command: Invoke an explicit method on the Desktop SynthesizerProcessor (Direction: SP->D) */
	public static final String CMD_METHOD = "command_method";
	public static final String EXTRA_ARGS = "extra_arguments";
	public static final String ARG_ADDNODEATPOSITION 	= "addNodeAtPosition";
	public static final String ARG_CLEARNODES 			= "removeAllNodes";
	
	/** Command: Introduce Smartphone to Desktop Synthesizer (Direction: SP->D) */
	public static final String CMD_HELLO = "command_hello";
	public static final String EXTRA_SIMONREMOTE = "extra_simonremote";
	
	/** Command: Send a NodeProperty with probably updated values (Direction: SP->D) */
	public static final String CMD_CHANGEPARAM = "command_changeparam";
	public static final String EXTRA_NODEID = "extra_id";
	public static final String EXTRA_PARAMNUMBER = "extra_paramnumber";
	public static final String EXTRA_PROPERTY = "extra_property";
	
	/** Command: Highlight a Node on the Desktop side that is currently selected on Smartphone side (Direction: SP->D) */
	public static final String CMD_SELECTNODE = "command_selectnode";
	
	/* FROM DESKTOP TO SMARTPHONE */
	
	/** Command: Send node structure to the Smartphone (stripped-down to only the NodeProperties) (Direction: D->SP) */
	public static final String CMD_SENDNODES = "command_sendnodes";
}
