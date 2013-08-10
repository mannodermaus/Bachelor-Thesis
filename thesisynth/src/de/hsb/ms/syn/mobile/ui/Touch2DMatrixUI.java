package de.hsb.ms.syn.mobile.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

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
		camera = stage.getCamera();
		
		// Initialize UI
		
		Window window = new Window("Yo", ControllerUI.getSkin());
		TextButton button = new TextButton("AOSDMAISDNNASDNASD", ControllerUI.getSkin());
//		
//		button.addListener(new InputListener() {
//			@Override
//			public boolean touchDown(InputEvent event, float x, float y,
//					int pointer, int button) {
//				Utils.log("touch down");
//				return super.touchDown(event, x, y, pointer, button);
//			}
//		});
//		
		window.add(button);
		contents.add(window).minWidth(500).minHeight(300);
	}

	@Override
	public void render() {
		camera.update();

		// Render here
		stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 60));
		stage.draw();
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
