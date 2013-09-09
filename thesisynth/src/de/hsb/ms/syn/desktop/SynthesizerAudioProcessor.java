package de.hsb.ms.syn.desktop;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.AudioDevice;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;

import de.hsb.ms.syn.common.audio.Scale;
import de.hsb.ms.syn.common.audio.gen.Sawtooth;
import de.hsb.ms.syn.common.exceptions.NodeNotInitializedException;
import de.hsb.ms.syn.common.net.NetMessage;
import de.hsb.ms.syn.common.net.NetMessageFactory;
import de.hsb.ms.syn.common.net.NetMessage.Command;
import de.hsb.ms.syn.common.util.Constants;
import de.hsb.ms.syn.common.util.Utils;
import de.hsb.ms.syn.common.vo.CenterNode;
import de.hsb.ms.syn.common.vo.DraggableNode;
import de.hsb.ms.syn.common.vo.GenNode;
import de.hsb.ms.syn.common.vo.Node;

/**
 * Logic processing unit of the Synthesizer.
 * This Singleton manages the list of Nodes that are placed on the synthesizer's surface
 * and the processing of each audio thread per clock cycle.
 * @author Marcel
 *
 */
public class SynthesizerAudioProcessor {

	/** Singleton instance */
	private static SynthesizerAudioProcessor instance;
	
	/** Map containing relations between the connection IDs and the Node ID highlighted by each one */
	private Map<Integer, Integer> mapConnectionHighlightedNodes;
	
	/** Center Node from which recursive computations start */
	private CenterNode centerNode;
	/** Map that maps Node's ID numbers to themselves */
	private Map<Integer, Node> nodes;
	/** Stack that keeps track of the order of additions to the Map */
	private Stack<Node> nodeStack;
	
	/** Stage on which the Nodes are placed (Reference is gathered from SynthesizerRenderer */
	private Stage stage;
	
	/** Audio device that outputs the computed synthesizer signal */
	private AudioDevice speakers;
	
	/** Private Singleton Constructor */
	private SynthesizerAudioProcessor() { }
	
	/**
	 * Singleton access method to retrieve the class' only instance
	 * @return
	 */
	public static SynthesizerAudioProcessor getInstance() {
		if (instance == null)
			instance = new SynthesizerAudioProcessor();
		return instance;
	}
	
	/**
	 * Initialization of processor's status.
	 * Creates the CenterNode etc.
	 */
	public void init() {
		// Init relation map
		mapConnectionHighlightedNodes = new HashMap<Integer, Integer>();
		
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
	
	/**
	 * Highlights the Node with the given ID using the color referring to the given connection ID.
	 * @param connectionId
	 * @param newNodeId
	 */
	public void highlightNodeWithID(int connectionId, int newNodeId) {
		// If any Node was highlighted earlier by this connection ID, unhighlight it
		if (mapConnectionHighlightedNodes.containsKey(connectionId)) {
			int oldNodeId = mapConnectionHighlightedNodes.get(connectionId);
			nodes.get(oldNodeId).unhighlight();
		}
		
		// Highlight the new one
		nodes.get(newNodeId).highlight(connectionId);
		mapConnectionHighlightedNodes.put(connectionId, newNodeId);
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
		NetMessage sendnotesMsg = NetMessageFactory.create(Command.SENDNODES, Utils.makeNodePropertyStructure(nodes));
		Synthesizer.broadcast(sendnotesMsg);
		
		// Update Node structure
		this.arrangeAll();
	}
	
	/**
	 * Adds a new Node at the given position
	 * @param position
	 */
	public void addNodeAtPosition(Vector2 position) {
		GenNode n = new GenNode(1, position);
		n.setAlgorithm(new Sawtooth(new Scale(Scale.BASE_C, Scale.MODE_MAJ_OCTAVE)));
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
		NetMessage sendnotesMsg = NetMessageFactory.create(Command.SENDNODES, Utils.makeNodePropertyStructure(nodes));
		Synthesizer.broadcast(sendnotesMsg);
		
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
		NetMessage sendnotesMsg = NetMessageFactory.create(Command.SENDNODES, Utils.makeNodePropertyStructure(nodes));
		Synthesizer.broadcast(sendnotesMsg);
		
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
