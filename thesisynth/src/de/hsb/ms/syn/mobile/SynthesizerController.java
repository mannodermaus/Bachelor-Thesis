package de.hsb.ms.syn.mobile;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import de.hsb.ms.syn.common.abs.ControllerUI;
import de.hsb.ms.syn.common.interfaces.Connection;
import de.hsb.ms.syn.common.interfaces.NetCapableApplicationListener;
import de.hsb.ms.syn.common.util.Constants;
import de.hsb.ms.syn.common.vo.NetMessage;

/**
 * Smartphone-sided synthesizer module (Controller)
 * 
 * @author Marcel
 * 
 */
public class SynthesizerController implements NetCapableApplicationListener {

	/**
	 * UI of this controller (will be selected during the Smartphone's
	 * MainActivity)
	 */
	private ControllerUI ui;

	private Texture background;
	private SpriteBatch batch;
	

	/**
	 * Constructor
	 * 
	 * @param ui
	 */
	public SynthesizerController(ControllerUI ui) {
		this.ui = ui;
	}

	@Override
	public void create() {
		// Initialize the UI
		ui.init();
		// Delegate input handling to UI
		Gdx.input.setInputProcessor(ui);

		background = new Texture(Gdx.files.internal(String.format(
				Constants.PATH_UI, "bg")));
		batch = new SpriteBatch();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0.8f, 0.8f, 0.947f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		// Draw moving background (with wrap-around)
		batch.begin();
		batch.draw(background, 0, 0);
		batch.end();

		// Render UI
		ui.render();
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
	}

	@Override
	public void onNetMessageReceived(NetMessage message) {
		// Delegate to ControllerUI
		ui.handle(message);
	}

	@Override
	public void setConnection(Connection c) {
		ui.setConnection(c);
	}

}
