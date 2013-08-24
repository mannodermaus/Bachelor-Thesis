package de.hsb.ms.syn.mobile.abs;

import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import de.hsb.ms.syn.common.abs.AndroidConnection;
import de.hsb.ms.syn.common.abs.Connection;
import de.hsb.ms.syn.common.util.NetMessageFactory;
import de.hsb.ms.syn.common.util.NetMessages.Command;
import de.hsb.ms.syn.common.util.Utils;
import de.hsb.ms.syn.common.vo.NetMessage;
import de.hsb.ms.syn.common.vo.NodeProperties;

/**
 * Interface for SynthesizerController user interfaces. Extensions of this
 * abstract class experiment with different uses of the Smartphone screen and
 * each implementation represents one hybrid component of UI and Logic.
 * 
 * Every extension of ControllerUI should have a nested class to process
 * incoming network messages. It is advised that the nested processing class for
 * XXXUI is called XXXProcessor.
 * 
 * @author Marcel
 * 
 */
public abstract class ControllerUI extends InputMultiplexer {

	private Stage stage;
	
	protected Table contents;
	protected AndroidConnection connection;
	protected ControllerProcessor processor;
	
	protected static Skin skin;
	
	protected static final int WIDTH = 800;
	protected static final int HEIGHT = 480;
	protected static final int MENUHEIGHT = 50;

	// Logic
	protected static Map<Integer, NodeProperties> mNodePropertiesMap = null;
	protected static int mSelectedNodePropertiesIndex = -1;

	/**
	 * Initialization method
	 */
	public void init() {
		//this.stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		this.stage = new Stage(WIDTH, HEIGHT, true);
		this.contents = new Table();
		contents.align(Align.top | Align.left);
		contents.setFillParent(true);
		stage.addActor(contents);
		this.addProcessor(this.stage);
	}

	/**
	 * Render the controller's state
	 */
	public void render() {
		getCamera().update();
		
		stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 60));
		stage.draw();
	}
	
	/**
	 * Disposes of the controller UI
	 */
	public void dispose() {
		skin.dispose();
	}
	
	protected Camera getCamera() {
		return stage.getCamera();
	}
	
	protected SpriteBatch getSpriteBatch() {
		return stage.getSpriteBatch();
	}

	/**
	 * Handle incoming NetMessages (will be delegated from
	 * SynthesizerController)
	 * 
	 * @param message
	 */
	public void handle(NetMessage message) {
		this.processor.process(message);
	}

	/**
	 * Set the Connection endpoint
	 * 
	 * @param c
	 */
	public void setConnection(Connection c) {
		if (c instanceof AndroidConnection)
			this.connection = (AndroidConnection) c;
	}

	/**
	 * Retrieve the Stage containing the UI contents
	 * 
	 * @return
	 */
	public Stage getUIStage() {
		return this.stage;
	}
	
	/**
	 * @deprecated
	 * @return
	 */
	public ChangeListener createAddListener() {
		return new ChangeListener() {
			public void changed(ChangeEvent ev, Actor ac) {
				// Send message to add a new Node
				if (connection.isConnected()) {
					NetMessage m = NetMessageFactory.create(Command.METHOD, "addNodeAtPosition", Utils.randomPosition());
					connection.send(m);
				} else {
					Utils.log("Not connected.");
				}
			}
		};
	}

	/**
	 * @deprecated
	 * @return
	 */
	public ChangeListener createClearListener() {
		return new ChangeListener() {
			public void changed(ChangeEvent ev, Actor ac) {
				// Send message to add a new Node
				if (connection.isConnected()) {
					NetMessage m = NetMessageFactory.create(Command.METHOD, "removeAllNodes");
					connection.send(m);
				} else {
					Utils.log("Not connected.");
				}
			}
		};
	}
	
	public static Skin getSkin() {
		if (skin == null) {
			ControllerUI.reloadSkin();
		}
		return skin;
	}

	public static void reloadSkin() {
		skin = new Skin(Gdx.files.internal("data/ui.json"));
		//skin = new Skin(Gdx.files.internal("data/pack.json"));
	}

	/**
	 * Nested processor class for ControllerUI
	 * 
	 * @author Marcel
	 * 
	 */
	protected abstract class ControllerProcessor {

		/** Process the given NetMessage. @param m */
		public abstract void process(NetMessage m);
	}
}
