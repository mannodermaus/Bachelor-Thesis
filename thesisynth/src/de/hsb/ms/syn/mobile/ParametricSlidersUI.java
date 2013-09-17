package de.hsb.ms.syn.mobile;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import de.hsb.ms.syn.common.audio.Properties;
import de.hsb.ms.syn.common.audio.Property;
import de.hsb.ms.syn.common.net.NetMessage;
import de.hsb.ms.syn.common.net.NetMessage.Command;
import de.hsb.ms.syn.common.net.NetMessageFactory;
import de.hsb.ms.syn.common.ui.PropertyTable;
import de.hsb.ms.syn.common.util.Utils;

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
	
	/** Table for the PropertyTable objects to be displayed on the right of the Node list */
	private Table sliderPanel;

	/** Map relating integer IDs to PropertyTable objects */
	private Map<Integer, PropertyTable> propertyTables;

	@Override
	public void init(SynthesizerController context) {
		super.init(context);
		this.processor = new ParametricSlidersProcessor();

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
		
		int h = Gdx.graphics.getHeight() - MENUHEIGHT;
		contents.add(scroll).minHeight(h).maxHeight(h).minWidth(LISTPANELWIDTH).left();
		contents.add(sliderPanel).fillY().colspan(2).minWidth(Gdx.graphics.getWidth() - LISTPANELWIDTH).left();

		// Initialize listeners
		nodeList.addListener(new ChangeListener() {
			public void changed(ChangeEvent ev, Actor ac) {
				int selected = ((List) ac).getSelectedIndex();
				// Select the PropertySlider Table to be displayed
				selectSliderTable(selected);
				// Send a SELECTNODE message to Desktop side
				NetMessage msg = NetMessageFactory.create(Command.SELECTNODE, getNodeIdAt(selectedPropIndex));
				connection.send(msg);
			}
		});
	}
	
	@Override
	public void updateUI() {
		updateNodeList();
		updateSliderTables();
		selectSliderTable(selectedPropIndex);
	}
	
	/**
	 * Updates the slider values for each slider currently present in the slider table map
	 */
	private void updateSliderTables() {
		for (int i = 0; i < propertyTables.size(); i++) {
			int id = getNodeIdAt(i);
			Properties n = nodePropMap.get(id);
			PropertyTable t = propertyTables.get(id);
			t.updateSliderValues(n);
		}
	}

	/**
	 * Selects the slider table with the given index
	 * @param index
	 */
	private void selectSliderTable(int index) {
		selectedPropIndex = index;
		sliderPanel.clear();
		
		if (index > -1) {
			nodeList.setSelectedIndex(index);
			
			int id = getNodeIdAt(selectedPropIndex);
			
			// Create a new Table for the selected item's Sliders if they don't exist already
			if (!propertyTables.containsKey(id)) {
				PropertyTable table = new PropertyTable(id, nodePropMap, connection);
				propertyTables.put(id, table);
			}
	
			sliderPanel.add(propertyTables.get(id)).minHeight(100).padLeft(50);
		}
	}
	
	/**
	 * Nested processor implementation class for the ParametricSlidersUI
	 * @author Marcel
	 *
	 */
	private class ParametricSlidersProcessor extends ControllerProcessor {
		@Override
		public void process(NetMessage message) {
			// General handling
			super.process(message);
			
			// Access the message's extras
			Set<String> extras = message.getExtras();
			
			// Send Nodes message: Update the property Tables etc.
			if (extras.contains(NetMessage.CMD_SENDNODES)) {
				@SuppressWarnings("unchecked")
				HashMap<Integer, Properties> props = (HashMap<Integer, Properties>) message
						.getExtra(NetMessage.EXTRA_NODESTRUCTURE);
				nodePropMap = props;
				
				updateNodeList();

				// Auto-select the first item if none is selected at the moment
				if (selectedPropIndex == -1 && nodePropMap.size() > 0) {
					selectSliderTable(0);
					NetMessage msg = NetMessageFactory.create(Command.SELECTNODE, getNodeIdAt(selectedPropIndex));
					connection.send(msg);
				} else if (nodePropMap.size() == 0) {
					// If no nodes remain on the synthesizer surface, delete the slider table
					selectSliderTable(-1);
				}
				
				// Update property Tables (remove any that are not there anymore)
				propertyTables.keySet().retainAll(nodePropMap.keySet());
				updateSliderTables();
			}
			
			// Change Param message: Update the corresponding property and its table
			if (extras.contains(NetMessage.CMD_CHANGEPARAM)) {
				Utils.log("Got a changeparam message");
				Property changed = (Property) message.getExtra(NetMessage.EXTRA_PROPERTY_OBJECTS);
				int nodeIndex = message.getInt(NetMessage.EXTRA_NODEID);
				Properties corresponding = nodePropMap.get(nodeIndex);
				corresponding.put(changed.id(), changed);
				
				PropertyTable t = propertyTables.get(nodeIndex);
				t.updateSliderValues(corresponding);
			}

			// Select Node message: Update property Table to reflect currently selected Node
			if (extras.contains(NetMessage.CMD_SELECTNODE)) {

				int newSelectionID = message.getInt(NetMessage.EXTRA_NODEID);
				int oldSelectionIndex = selectedPropIndex;

				Object[] keys = nodePropMap.keySet().toArray();
				for (int i = 0; i < keys.length; i++) {
					if (keys[i].equals(newSelectionID)) {
						selectedPropIndex = i;
						break;
					}
				}

				// If the new index is different from the one selected before,
				// update Table
				if (oldSelectionIndex != selectedPropIndex) {
					nodeList.setSelectedIndex(selectedPropIndex);
					selectSliderTable(selectedPropIndex);
				}
			}
		}
	}
}