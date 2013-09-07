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

import de.hsb.ms.syn.common.audio.Properties;
import de.hsb.ms.syn.common.audio.Property;
import de.hsb.ms.syn.common.interfaces.AndroidConnection;
import de.hsb.ms.syn.common.net.NetMessage;
import de.hsb.ms.syn.common.net.NetMessageFactory;
import de.hsb.ms.syn.common.net.NetMessage.Command;
import de.hsb.ms.syn.mobile.ControllerUI;

public class PropertyTable extends Table {

	private Map<Integer, Properties> map;
	private int id;
	private Properties props;
	
	private List<PropertySlider> sliders;
	
	private AndroidConnection connection;

	public PropertyTable(int id, Map<Integer, Properties> allPropsMap,
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

		for (final Property p : props) {
			// Create PropertySlider
			sl = new PropertySlider(p, skin);

			// Add listener
			sl.addSliderListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent ev, Actor ac) {
					// Get slider value
					float value = ((Slider) ac).getValue();

					// Save locally
					Property newProp = new Property(p, value);
					props.put(newProp.id(), newProp);
					map.put(id, props);

					// Send over
					NetMessage changeMsg = NetMessageFactory.create(Command.CHANGEPARAMS, id, newProp);
					connection.send(changeMsg);
				}
			});
			
			this.sliders.add(sl);

			this.add(sl).minWidth(sl.getPrefWidth())
					.minHeight(sl.getPrefHeight()).center();
			this.row();
		}
	}
	
	public void updateSliderValues(Properties newProps) {
		this.props = newProps;
		
		for (PropertySlider sl : sliders) {
			int propID = sl.getPropID();
			Property prop = props.get(propID);
			float val = prop.val();
			sl.setValue(val);
		}
	}
}
