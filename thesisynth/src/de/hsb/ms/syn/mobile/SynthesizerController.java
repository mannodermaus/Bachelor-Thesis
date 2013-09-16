package de.hsb.ms.syn.mobile;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import de.hsb.ms.syn.common.interfaces.AndroidConnection;
import de.hsb.ms.syn.common.interfaces.Connection;
import de.hsb.ms.syn.common.interfaces.NetCapableApplicationListener;
import de.hsb.ms.syn.common.net.NetMessage;
import de.hsb.ms.syn.common.net.NetMessage.Command;
import de.hsb.ms.syn.common.net.NetMessageFactory;
import de.hsb.ms.syn.common.ui.ConnectionStatusIcon;
import de.hsb.ms.syn.common.util.Constants;

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

	private InputMultiplexer inputHandlers;
	
	private Map<Class<? extends ControllerUI>, ControllerUI> cachedUIs;
	private AndroidConnection connection;

	@Override
	public void create() {
		
		// Initialize the default UI (parametric view)
		cachedUIs = new HashMap<Class<? extends ControllerUI>, ControllerUI>();

		connection.init();
		
		connectionStatus = new ConnectionStatusIcon(connection);
		int w = Gdx.graphics.getWidth() - connectionStatus.getWidth();
		int h = Gdx.graphics.getHeight() - connectionStatus.getHeight();
		connectionStatus.setPosition(w, h);
		
		// Initialize the Menu
		final Button bPara		= new TextButton("Parametric Sliders", ControllerUI.getSkin());
		final Button bTouch		= new TextButton("2D Touch Matrix", ControllerUI.getSkin());
		final Button bSensor	= new TextButton("3D Sensor Matrix", ControllerUI.getSkin());
		
		final Color colorDefault = bPara.getColor().cpy();
		final Color colorChecked = Color.RED;
		
		Button bConnect = new ImageButton(ControllerUI.getSkin());
		bConnect.add(new Image(connection.getIconTexture()));
		
		bPara.addListener(new ChangeListener() {
			public void changed(ChangeEvent ev, Actor ac) {
				bPara.setColor(colorChecked);
				bTouch.setColor(colorDefault);
				bSensor.setColor(colorDefault);
				switchContentViewTo(ParametricSlidersUI.class);
			}
		});
		bTouch.addListener(new ChangeListener() {
			public void changed(ChangeEvent ev, Actor ac) {
				bPara.setColor(colorDefault);
				bTouch.setColor(colorChecked);
				bSensor.setColor(colorDefault);
				switchContentViewTo(TouchMatrix2dUI.class);
			}
		});
		bSensor.addListener(new ChangeListener() {
			public void changed(ChangeEvent ev, Actor ac) {
				bPara.setColor(colorDefault);
				bTouch.setColor(colorDefault);
				bSensor.setColor(colorChecked);
				switchContentViewTo(OrientationSensors3dUI.class);
			}
		});
		bConnect.addListener(new ChangeListener() {
			public void changed(ChangeEvent ev, Actor ac) {
				connection.connect();
			}
		});

		inputHandlers = new InputMultiplexer();
		
		menu = new ControllerMenu(new Button[] {bPara, bTouch, bSensor}, bConnect);
		
		bPara.setColor(colorChecked);
		switchContentViewTo(ParametricSlidersUI.class);
		
		// Delegate input handling to UI and Menu
		Gdx.input.setInputProcessor(inputHandlers);

		background = new Texture(Gdx.files.internal(String.format(Constants.PATH_UI, "bg")));
		batch = new SpriteBatch();
	}
	
	private void switchContentViewTo(Class<? extends ControllerUI> clazz) {
		try {
			if (!cachedUIs.containsKey(clazz)) {
				// Create a new instance for that UI and store it in the map
				ControllerUI inst = clazz.newInstance();
				inst.init(this);
				inst.setConnection(connection);
				cachedUIs.put(clazz, inst);
			}
			// Now set the content view to this UI
			content = cachedUIs.get(clazz);
			content.updateUI();
			
			// Update input processors
			inputHandlers.clear();
			inputHandlers.addProcessor(content);
			inputHandlers.addProcessor(menu);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setColor(Color c) {
		this.connectionStatus.setColor(c);
	}

	@Override
	public void resize(int width, int height) { }

	@Override
	public void render() {
       // Gdx.gl.glViewport(0, 0, width, height);
		Gdx.gl.glClearColor(0.8f, 0.8f, 0.947f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		
		// Draw background
		batch.begin();
		// batch.draw(background, 0, 0);
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(),
				0, 0, background.getWidth(), background.getHeight(), false, false);
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
	public void resume() { }

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
