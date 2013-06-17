package de.hsb.ms.syn.desktop;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.AudioDevice;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;

import de.hsb.ms.syn.common.abs.DraggableNode;
import de.hsb.ms.syn.common.abs.Node;
import de.hsb.ms.syn.common.exc.NodeNotInitializedException;
import de.hsb.ms.syn.common.util.Constants;
import de.hsb.ms.syn.common.util.NetMessages;
import de.hsb.ms.syn.common.util.Utils;
import de.hsb.ms.syn.common.vo.NetMessage;
import de.hsb.ms.syn.common.vo.gen.Sawtooth;
import de.hsb.ms.syn.common.vo.nodes.CenterNode;
import de.hsb.ms.syn.common.vo.nodes.GenNode;

/**
 * Logic processing unit of the Synthesizer.
 * This Singleton manages the list of Nodes that are placed on the synthesizer's surface
 * and the processing of each audio thread per clock cycle.
 * @author Marcel
 *
 */
public class SynthesizerProcessor {

	// Singleton instance
	private static SynthesizerProcessor instance;
	
	// Node managing structures
	private CenterNode centerNode;		// Center Node from which recursive computations start
	private Map<Integer, Node> nodes;	// Map that maps Node's ID numbers to themselves
	private Stack<Node> nodeStack;		// Stack that keeps track of the order of additions to the Map
	
	// Stage on which the Nodes are placed (Reference is gathered from SynthesizerRenderer
	private Stage stage;
	
	// Audio device that outputs the computed synthesizer signal
	private AudioDevice speakers;
	
	// Arrange times for Nodes (debug)
	public static int LAST_ARRANGE_TIME = 0;
	public static int MAX_ARRANGE_TIME = 0;
	
	/**
	 * Constructor
	 */
	private SynthesizerProcessor() {
	}
	
	/**
	 * Singleton access method to retrieve the class' only instance
	 * @return
	 */
	public static SynthesizerProcessor getInstance() {
		if (instance == null)
			instance = new SynthesizerProcessor();
		return instance;
	}
	
	/**
	 * Initialization of processor's status.
	 * Creates the CenterNode etc.
	 */
	public void init() {
		// Init Node structures
		nodes = new HashMap<Integer, Node>();
		nodeStack = new Stack<Node>();
		centerNode = new CenterNode();
		
		// Add center Node to the stage
		stage.addActor(centerNode);

		// Init audio device
		speakers = Gdx.audio.newAudioDevice(Constants.SAMPLING_RATE, true);
	}

	/**
	 * Process method.
	 * The heart of the processing unit
	 */
	public void process() {
		// Start the recursive buffer filling process
		float[] buffer = centerNode.fillBuffer();
		
		// Send the buffer to the audio device
		speakers.writeSamples(buffer, 0, buffer.length);
	}
	
	/**
	 * Returns a reference to the Center node
	 * @return
	 */
	public Node getCenterNode() {
		return centerNode;
	}
	
	/**
	 * Returns a collection of each Node object currently on the synthesizer's surface
	 * @return
	 */
	public Map<Integer, Node> getNodes() {
		return nodes;
	}
	
	/**
	 * Set the Stage instance (gathered from the Renderer)
	 * @param s
	 */
	public void setStage(Stage s) {
		stage = s;
	}
	
	public void highlightNodeWithID(int id) {
		for (Integer i : nodes.keySet()) {
			Node node = nodes.get(i);
			// If the given ID matches the ID of this Node, highlight it. Else, unhighlight it
			if (id == i.intValue())
				node.highlight();
			else
				node.unhighlight();
		}
	}
	
	/**
	 * Adds a Node to the Node map by reference
	 * @param n
	 */
	public void addNode(Node n) {
		// Add it to the Stage as well!
		nodes.put(n.getID(), n);
		nodeStack.add(n);
		stage.addActor(n);
		
		// Send Nodes update
		NetMessage sendnotesMsg = new NetMessage("Nodes");
		sendnotesMsg.addExtra(NetMessages.CMD_SENDNODES, Utils.makeNodePropertyStructure(nodes));
		Synthesizer.connection.send(sendnotesMsg);
		
		// Update Node structure
		this.arrangeAll();
	}
	
	/**
	 * Adds a new Node at the given position
	 * TODO Expand to addNodeAtPosition(Vector2, NodeProperties) eventually!
	 * @param position
	 */
	public void addNodeAtPosition(Vector2 position) {
		GenNode n = new GenNode(1, position);
		n.setDelegate(new Sawtooth(Utils.randomFrequency()));
		this.addNode(n);
	}
	
	/**
	 * Removes the Node that was added last
	 * (ergo, the one on top of the Node stack)
	 */
	public void removeLastNode() {
		Node last = nodeStack.pop();
		this.removeNode(last);
	}
	
	/**
	 * Removes a Node from the Node map
	 * @param n
	 */
	public void removeNode(Node n) {
		// If the Node's ID is in the map, proceed
		if (nodes.containsKey(n.getID())) {
			// Remove it from the Node structures and the stage
			nodes.remove(n.getID());
			if (nodeStack.contains(n)) nodeStack.remove(n);
			stage.getRoot().removeActor(n);
			
			// Update Node structure
			this.arrangeAll();
		}
		
		// Send Nodes update
		NetMessage sendnotesMsg = new NetMessage("Nodes");
		sendnotesMsg.addExtra(NetMessages.CMD_SENDNODES, Utils.makeNodePropertyStructure(nodes));
		Synthesizer.connection.send(sendnotesMsg);
		
		if (nodes.size() == 0)
			centerNode.resetBuffer();
	}
	
	/**
	 * Removes all Nodes (but the CenterNode)
	 */
	public void removeAllNodes() {
		nodes.clear();
		nodeStack.clear();
		stage.getRoot().clear();
		
		// Send Nodes update
		NetMessage sendnotesMsg = new NetMessage("Nodes");
		sendnotesMsg.addExtra(NetMessages.CMD_SENDNODES, Utils.makeNodePropertyStructure(nodes));
		Synthesizer.connection.send(sendnotesMsg);
		
		centerNode.resetBuffer();
	}

	/**
	 * Re-sets the 'arranged' flag of all nodes
	 */
	public void clearArrangedStates() {
		for (Node n : nodes.values()) {
			n.setArranged(false);
		}
	}
	/**
	 * Recursively re-arranges all nodes starting with the center node.
	 */
	public void arrangeAll() {
		clearArrangedStates();
		try {
			centerNode.arrange();
		} catch (NodeNotInitializedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Debug method: Print the Node map contents to the console
	 */
	public void printNodeMap() {
		System.out.println("Synthesizer Processor: Node Map contents");
		for (Integer key : nodes.keySet()) {
			DraggableNode val = (DraggableNode) nodes.get(key);
			System.out.println(String.format("ID=%d\t|\tClass=%s\t|\tName=%s", key, val.getClass().getSimpleName(), val.getName()));
		}
	}
}
