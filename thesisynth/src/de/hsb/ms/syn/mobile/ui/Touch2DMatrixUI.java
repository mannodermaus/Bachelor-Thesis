package de.hsb.ms.syn.mobile.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.scenes.scene2d.Actor;

import de.hsb.ms.syn.common.ui.TouchMatrixPad;
import de.hsb.ms.syn.common.ui.TouchMatrixPad.TouchMatrixEvent;
import de.hsb.ms.syn.common.ui.TouchMatrixPad.TouchMatrixListener;
import de.hsb.ms.syn.common.util.Utils;
import de.hsb.ms.syn.common.vo.NetMessage;
import de.hsb.ms.syn.mobile.abs.ControllerUI;

/**
 * Orientation Sensors UI
 * 
 * Test for smartphone sensor data processing.
 * 
 * @author Marcel
 * 
 */
public class Touch2DMatrixUI extends ControllerUI {

	private Camera camera;

	@Override
	public void init() {
		super.init();
		this.processor = new OrientationSensorsController();
		camera = getCamera();
		
		// Initialize UI
		TouchMatrixPad pad = new TouchMatrixPad(skin);
		pad.addTouchMatrixListener(new TouchMatrixListener() {
			@Override
			public void touchMatrixChanged(TouchMatrixEvent tme, Actor ac) {
				Utils.log("Yo I got this TouchMatrixEvent: " + tme.getXpercentage() + "," + tme.getYpercentage());
			}
		});
		contents.add(pad).left().minWidth(400).maxWidth(400).minHeight(400).maxHeight(400);
		Utils.log("done");
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	public void handle(NetMessage message) {

	}

	private class OrientationSensorsController extends ControllerProcessor {

		@Override
		public void process(NetMessage m) {

		}

	}
}
