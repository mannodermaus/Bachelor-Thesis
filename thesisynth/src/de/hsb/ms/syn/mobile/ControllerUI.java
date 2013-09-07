package de.hsb.ms.syn.mobile;

import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

import de.hsb.ms.syn.common.audio.Properties;
import de.hsb.ms.syn.common.interfaces.AndroidConnection;
import de.hsb.ms.syn.common.interfaces.Connection;
import de.hsb.ms.syn.common.net.NetMessage;

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
	private static Skin skin;
	protected Table contents;
	
	protected AndroidConnection connection;
	protected ControllerProcessor processor;
	
	private SynthesizerController context;
	
	protected static final int WIDTH = 800;
	protected static final int HEIGHT = 480;
	protected static final int MENUHEIGHT = 50;

	// Logic
	protected static Map<Integer, Properties> mNodePropertiesMap = null;
	protected static int mSelectedNodePropertiesIndex = -1;

	/**
	 * Initialization method
	 */
	public void init(SynthesizerController context) {
		//this.stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		this.context = context;
		this.stage = new Stage(WIDTH, HEIGHT, true);
		this.contents = new Table();
		contents.align(Align.top | Align.left);
		contents.setFillParent(true);
		stage.addActor(contents);
		this.addProcessor(this.stage);
	}
	
	protected SynthesizerController getContext() {
		return context;
	}

	/**
	 * Render the controller's state
	 */
	public void render() {
		getUICamera().update();
		
		stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 60));
		stage.draw();
	}

	public abstract void updateUI();
	
	/**
	 * Disposes of the controller UI
	 */
	public void dispose() {
		skin.dispose();
	}

	/**
	 * Retrieve the Stage containing the UI contents
	 * @return
	 */
	public Stage getUIStage() {
		return this.stage;
	}
	
	/**
	 * Returns UI camera
	 * @return
	 */
	protected Camera getUICamera() {
		return stage.getCamera();
	}
	
	/**
	 * Returns UI sprite batch
	 * @return
	 */
	protected SpriteBatch getUISpriteBatch() {
		return stage.getSpriteBatch();
	}
	
	/**
	 * Returns the ID of the NodeProperties object at the given index inside of the NodeProperties map
	 * @param index
	 * @return
	 */
	protected int getNodeIdAt(int index) {
		return (Integer) mNodePropertiesMap.keySet().toArray()[mSelectedNodePropertiesIndex];
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
	 * @param c
	 */
	public void setConnection(Connection c) {
		if (c instanceof AndroidConnection)
			this.connection = (AndroidConnection) c;
	}
	
	/**
	 * Returns the UI skin to use for designing the UI elements
	 * @return
	 */
	public static Skin getSkin() {
		if (skin == null) {
			ControllerUI.reloadSkin();
		}
		return skin;
	}

	/**
	 * Load the UI skin to use.
	 * This is called by getSkin() if there is no reference yet, so don't worry about it
	 */
	private static void reloadSkin() {
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
