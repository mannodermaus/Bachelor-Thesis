package de.hsb.ms.syn.common.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import de.hsb.ms.syn.common.abs.AndroidConnection;
import de.hsb.ms.syn.common.util.NetMessageFactory;
import de.hsb.ms.syn.common.util.NetMessages.Command;
import de.hsb.ms.syn.common.vo.NetMessage;
import de.hsb.ms.syn.common.vo.NodeProperties;
import de.hsb.ms.syn.common.vo.NodeProperty;
import de.hsb.ms.syn.mobile.abs.ControllerUI;

public class PropertyTable extends Table {

	private Map<Integer, NodeProperties> map;
	private int id;
	private NodeProperties props;
	
	private List<PropertySlider> sliders;
	
	private AndroidConnection connection;

	public PropertyTable(int id, Map<Integer, NodeProperties> allPropsMap,
			AndroidConnection connection) {
		this.map = allPropsMap;
		this.id = id;
		this.props = map.get(id);
		
		this.sliders = new ArrayList<PropertySlider>();
		
		this.connection = connection;

		this.init();
	}

	private void init() {
		Skin skin = ControllerUI.getSkin();

		this.add(new Label(props.name() + props.nodeIndex(), skin));
		this.row();

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
					map.put(id, props);

					// Send over
					NetMessage changeMsg = NetMessageFactory.create(Command.CHANGEPARAM, id, newProp);
					connection.send(changeMsg);
				}
			});
			
			this.sliders.add(sl);

			this.add(sl).minWidth(sl.getPrefWidth())
					.minHeight(sl.getPrefHeight()).center();
			this.row();
		}
	}
	
	public void updateSliderValues(NodeProperties newProps) {
		this.props = newProps;
		
		for (PropertySlider sl : sliders) {
			int propID = sl.getPropID();
			NodeProperty prop = props.get(propID);
			float val = prop.val();
			sl.setValue(val);
		}
	}
}
