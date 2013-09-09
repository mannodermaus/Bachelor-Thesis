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

/**
 * UI element containing multiple instances of {@link PropertySlider} for
 * a single algorithm's Properties object.
 * It is used by the ParametricSlidersUI.
 * @author Marcel
 *
 */
public class PropertyTable extends Table {

	/** Map relating Node id values to their Properties objects (mirroring the static ControllerUI map) */
	private Map<Integer, Properties> map;
	/** ID of the algorithm's Node */
	private int id;
	/** Properties of the algorithm */
	private Properties props;
	
	/** PropertySlider list */
	private List<PropertySlider> sliders;
	
	/** Connection to send over value changes made by the user */
	private AndroidConnection connection;

	/**
	 * Constructor
	 * @param id
	 * @param allPropsMap
	 * @param connection
	 */
	public PropertyTable(int id, Map<Integer, Properties> allPropsMap,
			AndroidConnection connection) {
		this.map = allPropsMap;
		this.id = id;
		this.props = map.get(id);
		
		this.sliders = new ArrayList<PropertySlider>();
		
		this.connection = connection;

		this.init();
	}

	/**
	 * Initialization method
	 */
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
	
	/**
	 * Update method for each slider's value in the PropertyTable
	 * @param newProps
	 */
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
