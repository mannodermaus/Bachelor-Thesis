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
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import de.hsb.ms.syn.common.abs.ControllerUI;
import de.hsb.ms.syn.common.interfaces.Connection;
import de.hsb.ms.syn.common.util.Constants;
import de.hsb.ms.syn.common.util.GdxConfiguration;
import de.hsb.ms.syn.common.util.NetMessages;
import de.hsb.ms.syn.common.util.Utils;
import de.hsb.ms.syn.common.vo.NetMessage;
import de.hsb.ms.syn.common.vo.NodeProperties;
import de.hsb.ms.syn.common.vo.NodeProperty;
import de.hsb.ms.syn.desktop.ui.PropertySlider;

/**
 * Create Nodes UI
 * 
 * First iteration of Controller UI. This one simply allows to remotely
 * add or remove Nodes from the synthesizer's surface
 * 
 * @author Marcel
 *
 */
public class CreateNodesUI extends ControllerUI implements GestureListener {
	
	// Skin
	private static Skin skin;
	
	// UI components
	private Table wrapper;
	private Table buttonPanel;
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
		skin = new Skin(Gdx.files.internal("data/ui.json"));
		
		wrapper = new Table();
		wrapper.align(Align.top | Align.left);
		wrapper.pad(20);
		wrapper.setFillParent(true);
		
		stage.addActor(wrapper);
		
		buttonPanel = new Table();
		listPanel = new Table();
		sliderPanel = new Table();
		propertyTables = new HashMap<Integer, Table>();
		
		// Nest ListPanel inside of a ScrollPane
		ScrollPane scroll = new ScrollPane(listPanel, skin);
		listPanel.align(Align.top | Align.left);
		listPanel.padLeft(10);
		scroll.setOverscroll(false, false);
		scroll.setSmoothScrolling(true);
		scroll.setScrollingDisabled(true, false);
		scroll.setScrollbarsOnTop(true);
		
		wrapper.add(buttonPanel).minWidth(300).left();
		wrapper.add(scroll).padLeft(20).minHeight(150).maxHeight(150).minWidth(200).left();
		wrapper.row();
		wrapper.add(sliderPanel).fillY().colspan(2).minWidth(500).left();
		
		// Fill the button panel
		final TextButton addButton = new TextButton("Add Gen Node at random Position", skin);
		final TextButton clearButton = new TextButton("Clear all Nodes", skin);
		final TextButton connectButton = new TextButton("Connect to Synthesizer...", skin);
		
		buttonPanel.add(addButton).minHeight(50).minWidth(300);
		buttonPanel.row();
		buttonPanel.add(clearButton).minHeight(50).minWidth(300);
		buttonPanel.row();
		buttonPanel.add(connectButton).minHeight(50).minWidth(300);
		
		// Fill the list panel
		nodeList = new List(new String[] {""}, skin);
		listPanel.add(nodeList);
		
		// Initialize listeners
		
		addButton.addListener(new ChangeListener() {
			public void changed(ChangeEvent ev, Actor ac) {
				// Send message to add a new Node
				if (connection.isConnected()) {
					NetMessage m = new NetMessage(addButton.getText().toString());
					m.addExtra(NetMessages.CMD_METHOD, NetMessages.ARG_ADDNODEATPOSITION);
					m.addExtra(NetMessages.EXTRA_ARGS, Utils.randomPosition());
					connection.send(m);
				} else {
					Gdx.app.log(Constants.LOG_TAG, "not connected");
				}
			}
		});
		

		clearButton.addListener(new ChangeListener() {
			public void changed(ChangeEvent ev, Actor ac) {
				// Send message to add a new Node
				if (connection.isConnected()) {
					NetMessage m = new NetMessage(clearButton.getText().toString());
					m.addExtra(NetMessages.CMD_METHOD, NetMessages.ARG_CLEARNODES);
					connection.send(m);
				} else {
					Utils.log("Not connected.");
				}
			}
		});
		
		connectButton.addListener(new ChangeListener() {
			public void changed(ChangeEvent ev, Actor ac) {
				connection.connect();
			}
		});
		
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
		Utils.log(String.format("Fling (%.2f,%.2f) (BUTTON=%d)", velocityX, velocityY, button));
		if (velocityX > 750) {
			Utils.log("Yup, that's the right stuff.");
			return true;
		}
		return false;
	}

	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
		camera.position.add(-deltaX, deltaY, 0);
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

	@Override
	public GdxConfiguration getConfiguration() {
		GdxConfiguration config = new GdxConfiguration();
		return config;
	}
	
	private void selectSliderTable(int selectedIndex) {
		selectedListItem = selectedIndex;
		int id = (Integer) properties.keySet().toArray()[selectedListItem];
		
		sliderPanel.clear();
		// Create a new Table for the selected item's Sliders if they don't exist already
		if (!propertyTables.containsKey(id)) {
			propertyTables.put(id, makeSliderTable(id));
		}
		
		sliderPanel.add(propertyTables.get(id)).minHeight(100);
	}

	/**
	 * Create a UI table structure containing Sliders for the given ID in the NodeProperties map
	 * @param id
	 * @return
	 */
	private Table makeSliderTable(final int id) {
		final NodeProperties props = properties.get(id);

		Table table = new Table();
		
		// For each NodeProperty, add a Slider!
		PropertySlider sl;
		
		for (final NodeProperty p : props) {
			// Create PropertySlider
			sl = new PropertySlider(p, skin);
			
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
			
			table.add(sl).minWidth(sl.getPrefWidth()).minHeight(sl.getPrefHeight()).center();
			table.row();
		}
		return table;
	}
	
	/**
	 * Create a NetMessage for ChangeParam messages
	 * @param index
	 * @param prop
	 * @return
	 */
	private NetMessage makeChangeParamMessage(int index, NodeProperty prop) {
		NetMessage message = new NetMessage("Change parameter");
		message.addExtra(NetMessages.CMD_CHANGEPARAM, "");
		message.addExtra(NetMessages.EXTRA_PARAMNUMBER, prop.id());
		message.addExtra(NetMessages.EXTRA_NODEID, index);
		message.addExtra(NetMessages.EXTRA_PROPERTY, prop);
		return message;
	}

	/**
	 * Create a NetMessage for SelectNode messages
	 * @return
	 */
	private NetMessage makeSelectNodeMessage() {
		NetMessage message = new NetMessage("Select node");
		message.addExtra(NetMessages.CMD_SELECTNODE, (Integer) properties.keySet().toArray()[selectedListItem]);
		return message;
	}
	
	/**
	 * Nested processing class according to ControllerUI structure
	 */
	private class CreateNodesProcessor extends ControllerProcessor {
		// Deal with incoming net messages in the appropriate manner for the UI's purpose
		public void process(NetMessage message) {
			
			// Access the message's extras
			Set<String> extras = message.getExtras();
			
			// Send Nodes message: Update the property Tables etc.
			if (extras.contains(NetMessages.CMD_SENDNODES)) {
				@SuppressWarnings("unchecked")
				HashMap<Integer, NodeProperties> props = (HashMap<Integer, NodeProperties>) message.getExtra(NetMessages.CMD_SENDNODES);
				properties = props;
				
				String[] items = new String[properties.size()];
				Iterator<Integer> IDiter = properties.keySet().iterator();
				
				for (int index = 0; index < items.length; index++) {
					int id = IDiter.next();
					// Update UI list
					items[index] = String.format("%s%d", properties.get(id).name(), id);
				}
				nodeList.setItems(items);
				
				// Auto-select the first item if none is selected at the moment
				if (selectedListItem == -1 && items.length > 0) {
					selectSliderTable(0);
					connection.send(makeSelectNodeMessage());
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
				
				// If the new index is different from the one selected before, update Table
				if (oldSelectionIndex != selectedListItem) {
					nodeList.setSelectedIndex(selectedListItem);
					selectSliderTable(selectedListItem);
				}
			}
		}
	}

	@Override
	public boolean touchDown(float x, float y, int pointer, int button) { return false; }

	@Override
	public boolean tap(float x, float y, int count, int button) { return false; }

	@Override
	public boolean longPress(float x, float y) { return false; }

	@Override
	public boolean zoom(float initialDistance, float distance) { return false; }

	@Override
	public boolean pinch(Vector2 ip1, Vector2 ip2, Vector2 p1, Vector2 p2) { return false; }
}