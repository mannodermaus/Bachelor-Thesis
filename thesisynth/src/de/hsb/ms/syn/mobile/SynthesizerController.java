package de.hsb.ms.syn.mobile;

import java.util.HashMap;
import java.util.Map;

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

import de.hsb.ms.syn.common.abs.AndroidConnection;
import de.hsb.ms.syn.common.abs.Connection;
import de.hsb.ms.syn.common.interfaces.NetCapableApplicationListener;
import de.hsb.ms.syn.common.ui.ConnectionStatusIcon;
import de.hsb.ms.syn.common.util.Constants;
import de.hsb.ms.syn.common.util.NetMessageFactory;
import de.hsb.ms.syn.common.util.NetMessages.Command;
import de.hsb.ms.syn.common.vo.NetMessage;
import de.hsb.ms.syn.mobile.abs.ControllerUI;
import de.hsb.ms.syn.mobile.ui.ControllerMenu;
import de.hsb.ms.syn.mobile.ui.ParametricSlidersUI;
import de.hsb.ms.syn.mobile.ui.Sensor3DMatrixUI;
import de.hsb.ms.syn.mobile.ui.Touch2DMatrixUI;

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

	private Map<Class<? extends ControllerUI>, ControllerUI> cachedUIs;
	private AndroidConnection connection;

	@Override
	public void create() {
		
		ControllerUI.reloadSkin();
		
		// Initialize the default UI (parametric view)
		cachedUIs = new HashMap<Class<? extends ControllerUI>, ControllerUI>();
		
		switchContentViewTo(ParametricSlidersUI.class);

		connection.init();
		
		connectionStatus = new ConnectionStatusIcon(connection);
		int w = Gdx.graphics.getWidth() - connectionStatus.getWidth();
		int h = Gdx.graphics.getHeight() - connectionStatus.getHeight();
		connectionStatus.setPosition(w, h);
		
		// Initialize the Menu
		Button bPara2D	= new TextButton("Parametric Sliders", ControllerUI.getSkin());
		Button bTouch	= new TextButton("2D Touch Matrix", ControllerUI.getSkin());
		Button bSensor	= new TextButton("3D Sensor Matrix", ControllerUI.getSkin());
		Button bConnect = new ImageButton(ControllerUI.getSkin());
		bConnect.add(new Image(connection.getIconTexture()));

		bPara2D.addListener(new ChangeListener() {
			public void changed(ChangeEvent ev, Actor ac) {
				switchContentViewTo(ParametricSlidersUI.class);
			}
		});
		bTouch.addListener(new ChangeListener() {
			public void changed(ChangeEvent ev, Actor ac) {
				switchContentViewTo(Touch2DMatrixUI.class);
			}
		});
		bSensor.addListener(new ChangeListener() {
			public void changed(ChangeEvent ev, Actor ac) {
				switchContentViewTo(Sensor3DMatrixUI.class);
			}
		});
		bConnect.addListener(new ChangeListener() {
			public void changed(ChangeEvent ev, Actor ac) {
				connection.connect();
			}
		});
		
		menu = new ControllerMenu(new Button[] {bPara2D, bTouch, bSensor}, bConnect);
		
		// Delegate input handling to UI and Menu
		content.addProcessor(menu);
		Gdx.input.setInputProcessor(content);

		background = new Texture(Gdx.files.internal(String.format(Constants.PATH_UI, "bg")));
		batch = new SpriteBatch();
	}

	private void switchContentViewTo(Class<? extends ControllerUI> clazz) {
		try {
			if (!cachedUIs.containsKey(clazz)) {
				// Create a new instance for that UI and store it in the map
				ControllerUI inst = clazz.newInstance();
				inst.init();
				inst.setConnection(connection);
				cachedUIs.put(clazz, inst);
			}
			// Now set the content view to this UI
			content = cachedUIs.get(clazz);
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
		// Disconnect from desktop synthesizer
		NetMessage message = NetMessageFactory.create(Command.BYE, connection.getID());
		connection.send(message);
		// Now, close the connection and dispose
		connection.close();
		
		this.dispose();
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		background.dispose();
		batch.dispose();
		menu.dispose();
		content.dispose();
		
		Gdx.app.exit();
	}

	@Override
	public void onNetMessageReceived(NetMessage message) {
		// Delegate to ControllerUI
		content.handle(message);
	}

	@Override
	public void setConnection(Connection c) {
		if (c instanceof AndroidConnection)
			this.connection = (AndroidConnection) c;
	}

}
