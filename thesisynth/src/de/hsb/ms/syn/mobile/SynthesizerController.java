package de.hsb.ms.syn.mobile;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import de.hsb.ms.syn.common.abs.ControllerUI;
import de.hsb.ms.syn.common.interfaces.Connection;
import de.hsb.ms.syn.common.interfaces.NetCapableApplicationListener;
import de.hsb.ms.syn.common.ui.ConnectionStatusIcon;
import de.hsb.ms.syn.common.util.Constants;
import de.hsb.ms.syn.common.vo.NetMessage;
import de.hsb.ms.syn.mobile.ui.ControllerMenu;
import de.hsb.ms.syn.mobile.ui.CreateNodesUI;

/**
 * Smartphone-sided synthesizer module (Controller)
 * 
 * @author Marcel
 * 
 */
public class SynthesizerController implements NetCapableApplicationListener {

	private ControllerMenu menu;
	private ControllerUI content;
	private ConnectionStatusIcon connectionStatus;
	private Texture background;
	private SpriteBatch batch;
	
	private Connection connection;

	@Override
	public void create() {
		// Initialize the default UI (parametric view)
		content = new CreateNodesUI();
		content.init();
		content.setConnection(connection);
		
		connectionStatus = new ConnectionStatusIcon(connection);
		int w = Gdx.graphics.getWidth() - connectionStatus.getWidth();
		int h = Gdx.graphics.getHeight() - connectionStatus.getHeight();
		connectionStatus.setPosition(w, h);
		
		// Initialize the Menu
		TextButton bAdd		= new TextButton("Add Gen Node at random position", ControllerUI.getSkin());
		TextButton bClear	= new TextButton("Clear all Nodes", ControllerUI.getSkin());
		TextButton bConnect = new TextButton("Connect to Synthesizer...", ControllerUI.getSkin());
		bAdd.addListener(content.createAddListener());
		bClear.addListener(content.createClearListener());
		bConnect.addListener(content.createConnectListener());
		menu = new ControllerMenu(new TextButton[] {bAdd, bClear, bConnect});
		
		// Delegate input handling to UI and Menu
		content.addProcessor(menu);
		Gdx.input.setInputProcessor(content);

		background = new Texture(Gdx.files.internal(String.format(Constants.PATH_UI, "bg")));
		
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
		content.render();
		
		// Render menu
		menu.draw();
		
		// Render connection state
		connectionStatus.draw(batch);
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
		content.handle(message);
	}

	@Override
	public void setConnection(Connection c) {
		this.connection = c;
	}

}
