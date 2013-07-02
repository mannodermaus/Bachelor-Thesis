package de.hsb.ms.syn.common.util;

/**
 * Network messages and arguments for Synth-Controller communication
 * @author Marcel
 *
 */
public abstract class NetMessages {
	
	public static enum Command {METHOD, HELLO, BYE, CHANGEPARAM, SELECTNODE, SENDNODES, SENDID};
	
	public static String fromCommand(Command c) {
		return String.format("CMD_%s", c);
	}

	/* FROM SMARTPHONE TO DESKTOP */
	
	/** Command: Invoke an explicit method on the Desktop SynthesizerProcessor (Direction: SP->D) */
	public static final String CMD_METHOD = "command_method";
	public static final String EXTRA_METHODNAME = "extra_methodname";
	public static final String EXTRA_ARGS = "extra_arguments";
	
	/** Command: Introduce Smartphone to Desktop Synthesizer (Direction: SP->D) */
	public static final String CMD_HELLO = "command_hello";
	public static final String EXTRA_SIMONREMOTE = "extra_simonremote";
	
	/** Command: Disconnect Smartphone from Desktop Synthesizer (Direction: SP->D) */
	public static final String CMD_BYE = "command_bye";
	
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
	public static final String EXTRA_NODESTRUCTURE = "extra_nodestructure";
	
	/** Command: Send the ID determined by the SynConnectionManager back to the smartphone */
	public static final String CMD_SENDID = "command_sendid";
	public static final String EXTRA_CONNID = "extra_connid";
}
