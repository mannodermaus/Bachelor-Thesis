package de.hsb.ms.syn.common.vo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

import de.hsb.ms.syn.common.exceptions.NodeNotInitializedException;
import de.hsb.ms.syn.common.util.Constants;
import de.hsb.ms.syn.common.util.Utils;
import de.hsb.ms.syn.desktop.SynthesizerAudioProcessor;
import de.hsb.ms.syn.desktop.SynthesizerRenderer;

/**
 * Base class for a Node representation on the synthesizer's surface.
 * It is extended by CenterNode and DraggableNode (abstract)
 * @author Marcel
 * 
 */
public abstract class Node extends Actor {

	// ID counter for all Node objects
	protected static int cnt = 0;
	
	// ID number
	protected int id = 0;
	
	// Input slots and maximum number of inputs for this Node
	protected List<Node> inputs;
	protected int MAX_INPUTS = 1;

	// Buffer for the Node's algorithm
	protected float[] buffer;

	// Graphical attributes
	protected Vector2 position;		// Position on the synthesizer surface
	protected Texture texture;		// Texture for this Node
	protected Sprite sprite;		// Sprite wrapper for the texture of this Node

	// Rendering objects
	private ShapeRenderer renderer;
	private BitmapFont font;

	// Boolean usage flags
	private boolean initialized = false;	// A Node may not be arranged or drawn if it didn't call init()
	private boolean arranged = false;		// Flag during arrangeAll(), has to be reset before calling that
	protected boolean dragged = false;		// Flag depicting if this Node is being dragged by the mouse
	protected boolean highlighted = false;	// Set to true when this Node is being highlighted by a SELECTNODE message
	
	// ID numbers of the mobile devices that may have this Node highlighted at a given point
	protected List<Integer> highlightedByIDsList;
	
	/**
	 * Constructor
	 * 
	 * @param inputs
	 *            Number of inputs allowed for this node
	 * @param pos
	 *            Initial position
	 */
	protected Node(int inputs, Vector2 pos) {
		super();

		// Start initializing some core data such as ID and starting position
		this.id = cnt++;
		this.MAX_INPUTS = inputs;
		this.inputs = new ArrayList<Node>(this.MAX_INPUTS);
		this.position = pos;

		this.renderer = new ShapeRenderer();
		this.font = new BitmapFont(Gdx.files.internal("data/robotocondensed.fnt"), false);
		
		this.highlightedByIDsList = new ArrayList<Integer>();

	}

	/**
	 * Initializing method that finishes the preparation of the Node object
	 * @param spriteName	Name of the sprite to be loaded for this Node
	 */
	protected void init(String spriteName) {
		
		// Set the actor's name to identify this Node easier
		this.setName("[" + spriteName + this.id + "]");
		
		// Load sprite and texture reference & update the Node's position
		this.setSprite(spriteName);
		setNodePosition(this.position.x, this.position.y);

		// Init the buffer
		this.resetBuffer();

		// Set the initialized flag
		this.initialized = true;
	}
 
	/**
	 * Sprite setter method. It takes the sprite filename
	 * and loads its texture. Finally, it resizes the Node's bounds
	 * @param spriteName
	 */
	protected void setSprite(String spriteName) {
		String spriteText = String.format(Constants.PATH_NODE, spriteName);
		texture = new Texture(Gdx.files.internal(spriteText));
		sprite = new Sprite(texture);
		setWidth(sprite.getWidth());
		setHeight(sprite.getHeight());
	}

	/**
	 * Set the position of this Node to the given value pair
	 * @param x
	 * @param y
	 */
	public void setNodePosition(float x, float y) {
		setBounds(x, y, getWidth(), getHeight());
		sprite.setBounds(x, y, getWidth(), getHeight());
		setOrigin(getX() + getWidth() / 2, getY() + getHeight() / 2);
	}

	/**
	 * Re-initialize the buffer for this Node
	 */
	public void resetBuffer() {
		buffer = new float[Constants.BUFFER_SIZE];
	}
	
	/**
	 * Returns the Node's ID
	 * @return
	 */
	public int getID() {
		return id;
	}

	/**
	 * Toggle the arranged flag for this Node
	 * @param b
	 */
	public void setArranged(boolean b) {
		arranged = b;
	}
	
	/**
	 * Highlights this Node (additional Sprites will be drawn for this Node during the render cycle)
	 * @param connectionId
	 */
	public void highlight(int connectionId) {
		this.highlighted = true;
		this.highlightedByIDsList.add(connectionId);
	}
	
	/**
	 * Unhighlights this Node
	 * @param connectionId
	 */
	public void unhighlight(int connectionId) {
		this.highlightedByIDsList.remove((Integer) connectionId);
		if (this.highlightedByIDsList.isEmpty())
			this.highlighted = false;
	}

	@SuppressWarnings("unchecked")
	/**
	 * Arrange this Node with its inputs to form a Node computation graph
	 * @throws NodeNotInitializedException
	 */
	public void arrange() throws NodeNotInitializedException {
		// If the Node does not allow any inputs, return immediately
		if (this.MAX_INPUTS == 0)
			return;

		// If it wasn't initialized properly, throw an exception
		if (!this.initialized)
			throw new NodeNotInitializedException(this);

		// Delete the Node's structure
		this.inputs.clear();

		// Calculate distance between this node and every other Node that hasn't
		// been arranged yet; save this information
		Object[] info = this.calculateDistances();
		List<Integer> distances = (List<Integer>) info[0];
		HashMap<Integer, Node> associations = (HashMap<Integer, Node>) info[1];

		// Now, recursively access all nodes, ask if they've been arranged yet,
		// and if not, create a relation as long as this node still has got
		// some empty inputs. After that, recursively go over the path
		// of that Node and build more relations for its inputs
		int c = 0;
		while ((this.MAX_INPUTS - this.inputs.size() > 0)
				&& c < distances.size()) {
			Node next = associations.get(distances.get(c));
			if (!next.arranged) {
				// Check if this "next" Node is also closest to the current one
				// (i.e. also "interested" in the relation)
				if (!next.closerToCenterThan(distances.get(c))) {
					// Match!
					this.inputs.add(next);
					next.arranged = true;
					// Arrange the next one
					next.arrange();
				}
			}
			c++;
		}
	}

	/**
	 * Draws the connections from this Node to its inputs using the ShapeRenderer object.
	 * This is called by SynthesizerRenderer and delegated from the NodesStage
	 */
	public void drawConnections() {
		// Render line connections to other nodes
		renderer.begin(ShapeType.Line);
		for (Node n : inputs) {
			renderer.setColor(Constants.COLOR_NODECON);
			// From origin to origin
			renderer.line(getOriginX(), getOriginY(), n.getOriginX(),
					n.getOriginY());
		}
		renderer.end();
	}
	
	/**
	 * Draw method overwriting Actor.draw:
	 * This draws everything but the Node's relation, i.e.
	 * its Sprite and information.
	 * Note: This does not throw an exception when the Node
	 * is not initialized properly, however it will log a warning
	 */
	public void draw(SpriteBatch b, float alpha) {
		// Update renderer projection
		renderer.setProjectionMatrix(b.getProjectionMatrix());

		if (!this.initialized) {
			Utils.log("Node not initialized: " + this.toString());
			return;
		}
		
		b.end();

		// If this Node is highlighted, render another Sprite on top
		if (this.highlighted) {
			renderer.begin(ShapeType.Filled);
			renderer.setColor(SynthesizerRenderer.getInstance().getColorForConnection(this.highlightedByIDsList.get(0)));
			renderer.circle(getOriginX(), getOriginY(), 19);
			renderer.end();
//			highlightSprite.setX(sprite.getX());
//			highlightSprite.setY(sprite.getY());
//			highlightSprite.draw(b);
		}
		
		b.begin();
		
		// Render sprite & test information (ID and Input #)
		sprite.draw(b);
		
		font.setColor(Color.WHITE);
		font.draw(b, "ID: " + this.id, getX(), getY());
		if (this.MAX_INPUTS > 0) {
			font.setColor(Color.RED);
			font.draw(b, "" + this.inputs.size() + "/" + this.MAX_INPUTS,
					getOriginX(), getOriginY());
		}
		
		if (this.highlighted && this.highlightedByIDsList.size() >= 2) {
			b.end();
			for (int i = 1; i < this.highlightedByIDsList.size(); i++) {
				renderer.begin(ShapeType.Filled);
				renderer.setColor(SynthesizerRenderer.getInstance().getColorForConnection(this.highlightedByIDsList.get(i)));
				renderer.circle(getOriginX() - 10 + (5 * (i - 1)), getOriginY() + 10, 5);
				renderer.end();
			}
			b.begin();
		}
	}

	/**
	 * Calculate the distances between this Node and every other Node on the synthesizer surface.
	 * This method returns a two-item Object array, with the first item
	 * containing a sorted list of Integers depicting the distances to other Nodes
	 * (lowest distance first), and with the second item containing a Map of those distances
	 * mapped to the Node objects in question
	 * @return
	 */
	private Object[] calculateDistances() {
		Object[] retvals = new Object[2];
		List<Integer> distances = new ArrayList<Integer>();
		HashMap<Integer, Node> associations = new HashMap<Integer, Node>();
		for (Node node : SynthesizerAudioProcessor.getInstance().getNodes().values()) {
			if (node != this && !node.arranged) {
				int x = (int) (node.sprite.getX() - this.sprite.getX());
				int y = (int) (node.sprite.getY() - this.sprite.getY());
				int d = (int) Math.sqrt((x * x) + (y * y));
				while (distances.contains(d))
					d += 1;
				distances.add(d);
				associations.put(d, node);
			}
		}
		// Sort list of distances from lowest to highest
		Collections.sort(distances);
		// Return
		retvals[0] = distances;
		retvals[1] = associations;
		return retvals;
	}
	
	/**
	 * Returns true if this Node is closer to the Center node than the given Integer distance,
	 * false if this is not the case.
	 * @param distance
	 * @return
	 */
	private boolean closerToCenterThan(int distance) {
		Node center = SynthesizerAudioProcessor.getInstance().getCenterNode();
		int x = (int) (center.sprite.getX() - this.sprite.getX());
		int y = (int) (center.sprite.getY() - this.sprite.getY());
		int d = (int) (Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)));
		return (d < distance);
	}

	/**
	 * Fill a buffer with the "contents" of this Node.
	 * Has to be implemented by sub classes
	 * @param buffer
	 */
	public abstract float[] fillBuffer();
}
