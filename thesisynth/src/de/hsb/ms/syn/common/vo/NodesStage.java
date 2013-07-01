package de.hsb.ms.syn.common.vo;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.SnapshotArray;

import de.hsb.ms.syn.common.util.Utils;
import de.hsb.ms.syn.desktop.abs.Node;

/**
 * Special kind of Libgdx Stage object with
 * the additional service of drawing connections for each Node
 * @author Marcel
 *
 */
public class NodesStage extends Stage {
	
	/**
	 * Constructor
	 * @param w
	 * @param h
	 * @param b
	 */
	public NodesStage(float w, float h, boolean b) {
		super(w, h, b);
	}
	
	/**
	 * Override for actor adding. Make sure that only Node objects are added to this Stage!
	 */
	public void addActor(Actor a) {
		if (a instanceof Node)
			super.addActor(a);
		else
			Utils.log("Can't add Object of type " + a.getClass().getSimpleName() + " to a NodesStage!");
	}
	
	/**
	 * Draw connections between Nodes on the synthesizer's surface.
	 * Called during SynthesizerRenderer.render() each cycle
	 */
	public void drawConnections() {
		// Return if the Center Node isn't even visible
		if (!getRoot().isVisible()) return;
		
		// Get the root's children and delegate to each child
		SnapshotArray<Actor> children = getRoot().getChildren();
		for (Actor a : children)
			((Node) a).drawConnections();
	}

}
