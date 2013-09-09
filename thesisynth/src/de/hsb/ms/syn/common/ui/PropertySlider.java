package de.hsb.ms.syn.common.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import de.hsb.ms.syn.common.audio.Property;
import de.hsb.ms.syn.common.audio.Scale;

/**
 * UI element representing a slider that is linked to an algorithm's Property
 * with additional labels depicting the function of the slider
 * @author Marcel
 *
 */
public class PropertySlider extends WidgetGroup {

	/** Table object representing the base of this element's layout */
	private Table t;
	
	/** ID number of the Property */
	private int propID;
	/** Property itself */
	private Property prop;
	/** UI slider element */
	private Slider slider;
	/** Slider label for the Property's name */
	private Label label;
	/** Slider label for the current Property value */
	private Label value;
	
	/**
	 * Constructor
	 * @param prop
	 * @param skin
	 */
	public PropertySlider(Property prop, Skin skin) {
		this.prop = prop;
		this.slider = new Slider(prop.lo(), prop.hi(), prop.step(), false, skin);
		
		String add = "";
		if (prop.name().equals("Tone")) {
			Scale s = (Scale) prop.extra();
			add = String.format(" (%s)", s.getName());
		}
		this.label = new Label(prop.name() + add, skin);
		
		this.propID = prop.id();
		this.value = new Label("", skin);

		this.slider.setValue(prop.val());
		updateValue();
		
		t = new Table(skin);
		t.setFillParent(true);
		
		label.setSize(80, 50);
		slider.setSize(300, 80);
		value.setSize(80, 50);
		t.add(label).minWidth(80).maxWidth(80).padRight(20);
		t.add(slider).minWidth(300).minHeight(50).padRight(20);
		t.add(value).minWidth(80).maxWidth(80);
		
		this.setSize(getPrefWidth(), getPrefHeight());
		this.addActor(t);
		
		slider.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent ev, Actor ac) {
				updateValue();
			}
		});
	}
	
	/**
	 * Adds an event listener to the slider. It will be notified when the value of the slider changes
	 * @param e
	 */
	public void addSliderListener(EventListener e) {
		slider.addListener(e);
	}
	
	/**
	 * Returns the ID of the Property that belongs to this PropertySlider
	 * @return
	 */
	public int getPropID() {
		return propID;
	}
	
	/**
	 * Sets the value of this PropertySlider
	 * @param val
	 */
	public void setValue(float val) {
		this.slider.setValue(val);
		updateValue();
	}
	
	/**
	 * Returns the value of this PropertySlider
	 * @return
	 */
	public float getValue() {
		return slider.getValue();
	}
	
	/**
	 * Updates the value of this PropertySlider
	 */
	public void updateValue() {
		float value = slider.getValue();
		
		if (prop.name().equals("Tone")) {
			Scale s = (Scale) prop.extra();
			String base = s.getNoteName((int) value);
			int oct = s.getNoteOctave((int) value);
			this.value.setText(String.format("%s%d", base, oct));
		} else
			this.value.setText(String.format("%.2f", value));
	}

	@Override
	public float getPrefWidth() {
		return slider.getMinWidth() + label.getMinWidth() + value.getMinWidth();
	}
	
	@Override
	public float getPrefHeight() {
		return 50;
	}
}
