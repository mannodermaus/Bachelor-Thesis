package de.hsb.ms.syn.common.abs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import de.hsb.ms.syn.common.util.NetMessages;
import de.hsb.ms.syn.common.util.Utils;
import de.hsb.ms.syn.common.vo.NetMessage;

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

	protected Stage stage;
	protected Table contents;
	protected Connection connection;
	protected ControllerProcessor processor;
	
	protected static Skin skin;

	/**
	 * Initialization method
	 */
	public void init() {
		this.stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		this.contents = new Table();
		contents.align(Align.top | Align.left);
		contents.setFillParent(true);
		stage.addActor(contents);
		this.addProcessor(this.stage);
	}

	/**
	 * Render the controller's state
	 */
	public abstract void render();

	/**
	 * Handle incoming NetMessages (will be delegated from
	 * SynthesizerController)
	 * 
	 * @param message
	 */
	public abstract void handle(NetMessage message);

	/**
	 * Set the Connection endpoint
	 * 
	 * @param c
	 */
	public void setConnection(Connection c) {
		this.connection = c;
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
					NetMessage m = new NetMessage();
					m.addExtra(NetMessages.CMD_METHOD,
							NetMessages.ARG_ADDNODEATPOSITION);
					m.addExtra(NetMessages.EXTRA_ARGS, Utils.randomPosition());
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
					NetMessage m = new NetMessage();
					m.addExtra(NetMessages.CMD_METHOD,
							NetMessages.ARG_CLEARNODES);
					connection.send(m);
				} else {
					Utils.log("Not connected.");
				}
			}
		};
	}
	
	public static Skin getSkin() {
		if (skin == null)
			skin = new Skin(Gdx.files.internal("data/ui.json"));
		return skin;
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
