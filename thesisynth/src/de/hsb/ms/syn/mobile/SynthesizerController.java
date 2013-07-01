package de.hsb.ms.syn.mobile;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import de.hsb.ms.syn.common.abs.Connection;
import de.hsb.ms.syn.common.abs.ControllerUI;
import de.hsb.ms.syn.common.interfaces.NetCapableApplicationListener;
import de.hsb.ms.syn.common.ui.ConnectionStatusIcon;
import de.hsb.ms.syn.common.util.Constants;
import de.hsb.ms.syn.common.vo.NetMessage;
import de.hsb.ms.syn.mobile.ui.ControllerMenu;
import de.hsb.ms.syn.mobile.ui.ParametricSlidersUI;

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
		switchContentViewTo(ParametricSlidersUI.class);
		
		connection.init();
		
		connectionStatus = new ConnectionStatusIcon(connection);
		int w = Gdx.graphics.getWidth() - connectionStatus.getWidth();
		int h = Gdx.graphics.getHeight() - connectionStatus.getHeight();
		connectionStatus.setPosition(w, h);
		
		// Initialize the Menu
		Button bPara2D	= new TextButton("Parametric Sliders", ControllerUI.getSkin());
		Button bConnect = new ImageButton(ControllerUI.getSkin());
		bConnect.add(new Image(connection.getIconTexture()));
		
		bPara2D.addListener(new ChangeListener() {
			public void changed(ChangeEvent ev, Actor ac) {
				switchContentViewTo(ParametricSlidersUI.class);
			}
		});
		bConnect.addListener(new ChangeListener() {
			public void changed(ChangeEvent ev, Actor ac) {
				connection.connect();
			}
		});
		
		menu = new ControllerMenu(new Button[] {bPara2D, bConnect});
		
		// Delegate input handling to UI and Menu
		content.addProcessor(menu);
		Gdx.input.setInputProcessor(content);

		background = new Texture(Gdx.files.internal(String.format(Constants.PATH_UI, "bg")));
		batch = new SpriteBatch();
	}

	private void switchContentViewTo(Class<? extends ControllerUI> clazz) {
		try {
			content = clazz.newInstance();
			content.init();
			content.setConnection(connection);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
