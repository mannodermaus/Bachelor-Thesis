package de.hsb.ms.syn.mobile;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import de.hsb.ms.syn.common.audio.Properties;
import de.hsb.ms.syn.common.audio.Property;
import de.hsb.ms.syn.common.audio.fx.TapDelay;
import de.hsb.ms.syn.common.net.NetMessage;
import de.hsb.ms.syn.common.net.NetMessageFactory;
import de.hsb.ms.syn.common.net.NetMessage.Command;
import de.hsb.ms.syn.common.ui.TouchMatrixPad;
import de.hsb.ms.syn.common.ui.TouchMatrixPad.TouchMatrixEvent;
import de.hsb.ms.syn.common.ui.TouchMatrixPad.TouchMatrixListener;
import de.hsb.ms.syn.common.util.Utils;

/**
 * Touch 2D Matrix UI
 * 
 * Second iteration of Controller UI. Using a custom UI widget that provides a KaossPad-like surface,
 * this UI enables the user to change parameter values of Nodes using touch gestures
 * 
 * @author Marcel
 * 
 */
public class TouchMatrix2dUI extends ControllerUI {

	// UI components
	private Table listPanel;
	private List nodeList;
	private TouchMatrixPad pad;
	
	// Logic
	private Property xProperty;
	private Property yProperty;
	
	@Override
	public void init(SynthesizerController context) {
		super.init(context);
		this.processor = new Touch2DMatrixProcessor();
		
		// Initialize UI
		int h = HEIGHT - MENUHEIGHT;
		
		listPanel = new Table();
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
		
		pad = new TouchMatrixPad(getSkin());
		
		contents.add(scroll).minHeight(h).maxHeight(h).minWidth(200).left();
		contents.add(pad).left().top().minWidth(600).maxWidth(600).minHeight(h).maxHeight(h);
		
		nodeList.addListener(new ChangeListener() {
			public void changed(ChangeEvent ev, Actor ac) {
				int selected = ((List) ac).getSelectedIndex();
				selectNode(selected);
				// Send a SELECTNODE message to Desktop side
				NetMessage msg = NetMessageFactory.create(Command.SELECTNODE, getNodeIdAt(mSelectedNodePropertiesIndex));
				connection.send(msg);
			}
		});
		
		pad.addTouchMatrixListener(new TouchMatrixListener() {
			@Override
			public void touchMatrixChanged(TouchMatrixEvent tme, Actor ac) {
				changeSelectedNodeParams(tme);
			}
		});
	}

	private void changeSelectedNodeParams(TouchMatrixEvent event) {
		if (xProperty == null || yProperty == null) return;
		
		// Convert the TouchMatrixPad values back into their original scales
		float xval = Utils.getScaleConvertedValue(event.getXpercentage(), 0.0f, 1.0f, xProperty.lo(), xProperty.hi());
		xProperty.setVal(xval);
		// TODO If the user should have the option to pick any property for each axis, convert y axis here, too!
		float yval = event.getYpercentage();
		yProperty.setVal(yval);
		
		int id = getNodeIdAt(mSelectedNodePropertiesIndex);
		Properties node = mNodePropertiesMap.get(id);
		
		// Save locally
		Property newPropX = new Property(xProperty, xval);
		node.put(newPropX.id(), newPropX);
		Property newPropY = new Property(yProperty, yval);
		node.put(newPropY.id(), newPropY);
		mNodePropertiesMap.put(id, node);
		
		xProperty = newPropX;
		yProperty = newPropY;
		
		// Make a NetMessage
		NetMessage changeMsg = NetMessageFactory.create(Command.CHANGEPARAMS, id, newPropX, newPropY);
		connection.send(changeMsg);
	}
	
	private void selectNode(int index) {
		mSelectedNodePropertiesIndex = index;
		
		if (index > -1) {
			nodeList.setSelectedIndex(index);
			
			int id = getNodeIdAt(mSelectedNodePropertiesIndex);
			Properties nodeProps = mNodePropertiesMap.get(id);
			// Set the param values of these NodeProperties inside the TouchMatrixPad
			// x axis: tone or frequency, y axis: volume
			xProperty = nodeProps.get(Properties.PROP_TONE);
			if (xProperty == null) {
				// If the node doesn't have a "TONE" property, just use the frequency property
				xProperty = nodeProps.get(Properties.PROP_FREQUENCY);
				// TapDelay doesn't have Frequency either.
				if (xProperty == null)
					xProperty = nodeProps.get(TapDelay.PROP_FEEDBACK);
			}
			// TapDelay: Choose TIME property
			if (nodeProps.has(TapDelay.PROP_TIME))
				yProperty = nodeProps.get(TapDelay.PROP_TIME);
			else
				yProperty = nodeProps.get(Properties.PROP_VOLUME);
			
			// For the x axis value, we need to convert the property's scale to [0.0, 1.0] first
			float xval = Utils.getScaleConvertedValue(xProperty.val(), xProperty.lo(), xProperty.hi(), 0.0f, 1.0f);
			// TODO If the user should have the option to pick any property for each axis, convert y axis here, too!
			float yval = yProperty.val();
			
			pad.setAxisLabels(xProperty.name(), yProperty.name());
			pad.setTouchPointByPercentage(xval, yval);
		}
	}
	
	@Override
	public void updateUI() {
		updateNodeList();
		selectNode(mSelectedNodePropertiesIndex);
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

	private class Touch2DMatrixProcessor extends ControllerProcessor {

		@Override
		public void process(NetMessage message) {
			// Access the message's extras
			Set<String> extras = message.getExtras();

			// Send ID message: The SynConnectionManager has sent an ID for this device's connection
			if (extras.contains(NetMessage.CMD_SENDID)) {
				int id = message.getInt(NetMessage.EXTRA_CONNID);
				Utils.log("Got my ID from the Desktop Synthesizer. It is " + id);
				connection.setID(id);
				
				float[] colorVals = (float[]) message.getExtra(NetMessage.EXTRA_COLORVALS);
				Color color = new Color(colorVals[0], colorVals[1], colorVals[2], 1.0f);
				TouchMatrix2dUI.this.getContext().setColor(color);

				// Send a "HELLO" message to the desktop
				Utils.log("Connected.");
				NetMessage m = NetMessageFactory.create(Command.HELLO);
				m.addExtra(NetMessage.CMD_HELLO, "");
				connection.send(m);
			}
			
			// Send Nodes message: Update the property Tables etc.
			if (extras.contains(NetMessage.CMD_SENDNODES)) {
				@SuppressWarnings("unchecked")
				HashMap<Integer, Properties> props = (HashMap<Integer, Properties>) message.getExtra(NetMessage.EXTRA_NODESTRUCTURE);
				mNodePropertiesMap = props;
				
				updateNodeList();

				// Auto-select the first item if none is selected at the moment
				if (mSelectedNodePropertiesIndex == -1 && mNodePropertiesMap.size() > 0) {
					selectNode(0);
					NetMessage msg = NetMessageFactory.create(Command.SELECTNODE, getNodeIdAt(mSelectedNodePropertiesIndex));
					connection.send(msg);
				} else if (mNodePropertiesMap.size() == 0) {
					// If no nodes remain on the synthesizer surface, delete the slider table
					selectNode(-1);
				}
			}
		}

	}
}