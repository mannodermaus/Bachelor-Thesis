package de.hsb.ms.syn.mobile.ui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import de.hsb.ms.syn.common.abs.Connection;
import de.hsb.ms.syn.common.util.NetMessages;
import de.hsb.ms.syn.common.util.Utils;
import de.hsb.ms.syn.common.vo.NetMessage;
import de.hsb.ms.syn.common.vo.NodeProperties;
import de.hsb.ms.syn.common.vo.NodeProperty;
import de.hsb.ms.syn.desktop.ui.PropertySlider;
import de.hsb.ms.syn.mobile.abs.ControllerUI;

/**
 * Create Nodes UI
 * 
 * First iteration of Controller UI. This one simply allows to remotely add or
 * remove Nodes from the synthesizer's surface
 * 
 * @author Marcel
 * 
 */
public class ParametricSlidersUI extends ControllerUI implements GestureListener {

	// UI components
	private Table listPanel;
	private Table sliderPanel;

	private Map<Integer, Table> propertyTables;
	private int selectedListItem = -1;

	private List nodeList;

	// Rendering
	private Camera camera;

	// Logic
	private Map<Integer, NodeProperties> properties = null;

	@Override
	public void init() {
		super.init();

		// Add a gesture detector to the input handler
		this.addProcessor(new GestureDetector(this));
		this.processor = new CreateNodesProcessor();
		camera = stage.getCamera();

		// Initialize UI
		listPanel = new Table();
		sliderPanel = new Table();
		propertyTables = new HashMap<Integer, Table>();

		// Nest ListPanel inside of a ScrollPane
		ScrollPane scroll = new ScrollPane(listPanel, getSkin());
		listPanel.align(Align.top | Align.left);
		scroll.setOverscroll(false, false);
		scroll.setSmoothScrolling(true);
		scroll.setScrollingDisabled(true, false);
		scroll.setScrollbarsOnTop(true);
		
		int h = Gdx.graphics.getHeight() - 50;
		contents.add(scroll).minHeight(h).maxHeight(h).minWidth(200).left();
		contents.add(sliderPanel).fillY().colspan(2).minWidth(500).left();
		
		// Fill the list panel
		nodeList = new List(new String[] { "" }, getSkin());
		listPanel.add(nodeList);

		// Initialize listeners
		nodeList.addListener(new ChangeListener() {
			public void changed(ChangeEvent ev, Actor ac) {
				int selected = ((List) ac).getSelectedIndex();
				// Select the PropertySlider Table to be displayed
				selectSliderTable(selected);
				// Send a SELECTNODE message to Desktop side
				connection.send(makeSelectNodeMessage());
			}
		});
	}

	@Override
	public void render() {
		camera.update();

		// Render here
		stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 60));
		stage.draw();
	}

	@Override
	public boolean fling(float velocityX, float velocityY, int button) {
		Utils.log(String.format("Fling (%.2f,%.2f) (BUTTON=%d)", velocityX,
				velocityY, button));
		if (velocityX > 750) {
			Utils.log("Yup, that's the right stuff.");
			return true;
		}
		return false;
	}

	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
		//camera.position.add(-deltaX, deltaY, 0);
		return true;
	}

	@Override
	public void handle(NetMessage message) {
		this.processor.process(message);
	}

	@Override
	public Stage getUIStage() {
		return this.stage;
	}

	@Override
	public void setConnection(Connection c) {
		this.connection = c;
	}

	private void selectSliderTable(int selectedIndex) {
		selectedListItem = selectedIndex;
		sliderPanel.clear();
		
		if (selectedIndex > -1) {
			int id = (Integer) properties.keySet().toArray()[selectedListItem];
			
			// Create a new Table for the selected item's Sliders if they don't exist already
			if (!propertyTables.containsKey(id)) {
				propertyTables.put(id, makeSliderTable(id));
			}
	
			sliderPanel.add(propertyTables.get(id)).minHeight(100).padLeft(50);
		}
	}

	/**
	 * Create a UI table structure containing Sliders for the given ID in the
	 * NodeProperties map
	 * 
	 * @param id
	 * @return
	 */
	private Table makeSliderTable(final int id) {
		final NodeProperties props = properties.get(id);

		Table table = new Table();

		table.add(new Label(props.name() + props.nodeIndex(), getSkin()));
		table.row();
		// For each NodeProperty, add a Slider!
		PropertySlider sl;

		for (final NodeProperty p : props) {
			// Create PropertySlider
			sl = new PropertySlider(p, getSkin());

			// Add listener
			sl.addSliderListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent ev, Actor ac) {
					// Get slider value
					float value = ((Slider) ac).getValue();

					// Save locally
					NodeProperty newProp = new NodeProperty(p, value);
					props.put(newProp.id(), newProp);
					properties.put(id, props);

					// Send over
					connection.send(makeChangeParamMessage(id, newProp));
				}
			});

			table.add(sl).minWidth(sl.getPrefWidth())
					.minHeight(sl.getPrefHeight()).center();
			table.row();
		}
		return table;
	}

	/**
	 * Create a NetMessage for ChangeParam messages
	 * 
	 * @param index
	 * @param prop
	 * @return
	 */
	private NetMessage makeChangeParamMessage(int index, NodeProperty prop) {
		NetMessage message = new NetMessage();
		message.addExtra(NetMessages.CMD_CHANGEPARAM, "");
		message.addExtra(NetMessages.EXTRA_PARAMNUMBER, prop.id());
		message.addExtra(NetMessages.EXTRA_NODEID, index);
		message.addExtra(NetMessages.EXTRA_PROPERTY, prop);
		return message;
	}

	/**
	 * Create a NetMessage for SelectNode messages
	 * 
	 * @return
	 */
	private NetMessage makeSelectNodeMessage() {
		NetMessage message = new NetMessage();
		message.addExtra(NetMessages.CMD_SELECTNODE, (Integer) properties
				.keySet().toArray()[selectedListItem]);
		return message;
	}

	/**
	 * Nested processing class according to ControllerUI structure
	 */
	private class CreateNodesProcessor extends ControllerProcessor {
		// Deal with incoming net messages in the appropriate manner for the
		// UI's purpose
		public void process(NetMessage message) {

			// Access the message's extras
			Set<String> extras = message.getExtras();

			// Send Nodes message: Update the property Tables etc.
			if (extras.contains(NetMessages.CMD_SENDNODES)) {
				@SuppressWarnings("unchecked")
				HashMap<Integer, NodeProperties> props = (HashMap<Integer, NodeProperties>) message
						.getExtra(NetMessages.CMD_SENDNODES);
				properties = props;
				
				String[] items = new String[properties.size()];
				Iterator<Integer> IDiter = properties.keySet().iterator();
				
				Utils.log("Got Sendnodes message: There are " + items.length + " items right now.");

				for (int index = 0; index < items.length; index++) {
					int id = IDiter.next();
					// Update UI list
					items[index] = String.format("%s%d", properties.get(id)
							.name(), id);
				}
				nodeList.setItems(items);

				// Auto-select the first item if none is selected at the moment
				if (selectedListItem == -1 && items.length > 0) {
					selectSliderTable(0);
					connection.send(makeSelectNodeMessage());
				} else if (items.length == 0) {
					// If no nodes remain on the synthesizer surface, delete the slider table
					selectSliderTable(-1);
				} else {
					
					nodeList.setSelectedIndex(selectedListItem);
				}
				
				// Update property Tables (remove any that are not there anymore)
				propertyTables.keySet().retainAll(properties.keySet());
			}

			// Select Node message: Update property Table to reflect currently selected Node
			if (extras.contains(NetMessages.CMD_SELECTNODE)) {

				int newSelectionID = message.getInt(NetMessages.CMD_SELECTNODE);
				int oldSelectionIndex = selectedListItem;

				Object[] keys = properties.keySet().toArray();
				for (int i = 0; i < keys.length; i++) {
					if (keys[i].equals(newSelectionID)) {
						selectedListItem = i;
						break;
					}
				}

				// If the new index is different from the one selected before,
				// update Table
				if (oldSelectionIndex != selectedListItem) {
					nodeList.setSelectedIndex(selectedListItem);
					selectSliderTable(selectedListItem);
				}
			}
		}
	}

	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		return false;
	}

	@Override
	public boolean tap(float x, float y, int count, int button) {
		return false;
	}

	@Override
	public boolean longPress(float x, float y) {
		return false;
	}

	@Override
	public boolean zoom(float initialDistance, float distance) {
		return false;
	}

	@Override
	public boolean pinch(Vector2 ip1, Vector2 ip2, Vector2 p1, Vector2 p2) {
		return false;
	}
}