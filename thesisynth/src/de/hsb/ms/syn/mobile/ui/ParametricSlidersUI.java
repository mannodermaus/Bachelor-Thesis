package de.hsb.ms.syn.mobile.ui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import de.hsb.ms.syn.common.ui.PropertyTable;
import de.hsb.ms.syn.common.util.NetMessageFactory;
import de.hsb.ms.syn.common.util.NetMessages;
import de.hsb.ms.syn.common.util.NetMessages.Command;
import de.hsb.ms.syn.common.util.Utils;
import de.hsb.ms.syn.common.vo.NetMessage;
import de.hsb.ms.syn.common.vo.NodeProperties;
import de.hsb.ms.syn.common.vo.NodeProperty;
import de.hsb.ms.syn.mobile.abs.ControllerUI;

/**
 * Parametric Sliders UI
 * 
 * First iteration of Controller UI. This UI provides sliders that represent Node parameters
 * manipulated by user input.
 * 
 * @author Marcel
 * 
 */
public class ParametricSlidersUI extends ControllerUI {

	// UI components
	private Table listPanel;
	private Table sliderPanel;

	private Map<Integer, PropertyTable> propertyTables;

	private List nodeList;

	@Override
	public void init() {
		super.init();
		this.processor = new CreateNodesProcessor();

		// Initialize UI
		listPanel = new Table();
		sliderPanel = new Table();
		propertyTables = new HashMap<Integer, PropertyTable>();
		
		// Nest ListPanel inside of a ScrollPane
		ScrollPane scroll = new ScrollPane(listPanel, getSkin());
		listPanel.align(Align.top | Align.left);
		scroll.setOverscroll(false, false);
		scroll.setSmoothScrolling(true);
		scroll.setScrollingDisabled(true, false);
		scroll.setScrollbarsOnTop(true);
		
		// Fill the list panel
		nodeList = new List(new String[] { "" }, getSkin());
		listPanel.add(nodeList);
		
		int h = HEIGHT - MENUHEIGHT;
		contents.add(scroll).minHeight(h).maxHeight(h).minWidth(200).left();
		contents.add(sliderPanel).fillY().colspan(2).minWidth(500).left();

		// Initialize listeners
		nodeList.addListener(new ChangeListener() {
			public void changed(ChangeEvent ev, Actor ac) {
				int selected = ((List) ac).getSelectedIndex();
				// Select the PropertySlider Table to be displayed
				selectSliderTable(selected);
				// Send a SELECTNODE message to Desktop side
				NetMessage msg = NetMessageFactory.create(Command.SELECTNODE, (Integer) mNodePropertiesMap
						.keySet().toArray()[mSelectedNodePropertiesIndex]);
				connection.send(msg);
			}
		});
	}
	
	@Override
	public void updateUI() {
		updateNodeList();
		selectSliderTable(mSelectedNodePropertiesIndex);
	}
	
	private void updateNodeList() {
		if (mNodePropertiesMap != null) {
			String[] items = new String[mNodePropertiesMap.size()];
			Iterator<Integer> IDiter = mNodePropertiesMap.keySet().iterator();
	
			for (int index = 0; index < items.length; index++) {
				int id = IDiter.next();
				// Update UI list
				items[index] = String.format("%s%d", mNodePropertiesMap.get(id)
						.name(), id);
			}
			nodeList.setItems(items);
		}
	}

	private void selectSliderTable(int index) {
		mSelectedNodePropertiesIndex = index;
		sliderPanel.clear();
		
		if (index > -1) {
			nodeList.setSelectedIndex(index);
			
			int id = (Integer) mNodePropertiesMap.keySet().toArray()[mSelectedNodePropertiesIndex];
			
			// Create a new Table for the selected item's Sliders if they don't exist already
			if (!propertyTables.containsKey(id)) {
				PropertyTable table = new PropertyTable(id, mNodePropertiesMap, connection);
				propertyTables.put(id, table);
			}
	
			sliderPanel.add(propertyTables.get(id)).minHeight(100).padLeft(50);
		}
	}

	/**
	 * Nested processing class according to ControllerUI structure
	 */
	private class CreateNodesProcessor extends ControllerProcessor {
		@Override
		public void process(NetMessage message) {
			// Access the message's extras
			Set<String> extras = message.getExtras();

			// Send ID message: The SynConnectionManager has sent an ID for this device's connection
			if (extras.contains(NetMessages.CMD_SENDID)) {
				int id = message.getInt(NetMessages.EXTRA_CONNID);
				Utils.log("Got my ID from the Desktop Synthesizer. It is " + id);
				connection.setID(id);

				// Send a "HELLO" message to the desktop
				Utils.log("Connected.");
				NetMessage m = NetMessageFactory.create(Command.HELLO);
				m.addExtra(NetMessages.CMD_HELLO, "");
				connection.send(m);
			}
			
			// Send Nodes message: Update the property Tables etc.
			if (extras.contains(NetMessages.CMD_SENDNODES)) {
				@SuppressWarnings("unchecked")
				HashMap<Integer, NodeProperties> props = (HashMap<Integer, NodeProperties>) message
						.getExtra(NetMessages.EXTRA_NODESTRUCTURE);
				mNodePropertiesMap = props;
				
				updateNodeList();

				// Auto-select the first item if none is selected at the moment
				if (mSelectedNodePropertiesIndex == -1 && mNodePropertiesMap.size() > 0) {
					selectSliderTable(0);
					NetMessage msg = NetMessageFactory.create(Command.SELECTNODE, (Integer) mNodePropertiesMap
							.keySet().toArray()[mSelectedNodePropertiesIndex]);
					connection.send(msg);
				} else if (mNodePropertiesMap.size() == 0) {
					// If no nodes remain on the synthesizer surface, delete the slider table
					selectSliderTable(-1);
				}
				
				// Update property Tables (remove any that are not there anymore)
				propertyTables.keySet().retainAll(mNodePropertiesMap.keySet());
				
				for (int i = 0; i < propertyTables.size(); i++) {
					int id = (Integer) mNodePropertiesMap.keySet().toArray()[mSelectedNodePropertiesIndex];
					NodeProperties n = mNodePropertiesMap.get(id);
					PropertyTable t = propertyTables.get(id);
					t.updateSliderValues(n);
				}
			}
			
			// Change Param message: Update the corresponding property and its table
			if (extras.contains(NetMessages.CMD_CHANGEPARAM)) {
				Utils.log("Got a changeparam message");
				NodeProperty changed = (NodeProperty) message.getExtra(NetMessages.EXTRA_PROPERTY_OBJECTS);
				int nodeIndex = message.getInt(NetMessages.EXTRA_NODEID);
				NodeProperties corresponding = mNodePropertiesMap.get(nodeIndex);
				corresponding.put(changed.id(), changed);
				
				PropertyTable t = propertyTables.get(nodeIndex);
				t.updateSliderValues(corresponding);
			}

			// Select Node message: Update property Table to reflect currently selected Node
			if (extras.contains(NetMessages.CMD_SELECTNODE)) {

				int newSelectionID = message.getInt(NetMessages.EXTRA_NODEID);
				int oldSelectionIndex = mSelectedNodePropertiesIndex;

				Object[] keys = mNodePropertiesMap.keySet().toArray();
				for (int i = 0; i < keys.length; i++) {
					if (keys[i].equals(newSelectionID)) {
						mSelectedNodePropertiesIndex = i;
						break;
					}
				}

				// If the new index is different from the one selected before,
				// update Table
				if (oldSelectionIndex != mSelectedNodePropertiesIndex) {
					nodeList.setSelectedIndex(mSelectedNodePropertiesIndex);
					selectSliderTable(mSelectedNodePropertiesIndex);
				}
			}
		}
	}
}