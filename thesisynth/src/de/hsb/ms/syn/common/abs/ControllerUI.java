package de.hsb.ms.syn.common.abs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.scenes.scene2d.Stage;

import de.hsb.ms.syn.common.interfaces.Connection;
import de.hsb.ms.syn.common.util.GdxConfiguration;
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
	protected Connection connection;
	protected ControllerProcessor processor;

	/**
	 * Initialization method
	 */
	public void init() {
		this.stage = new Stage(Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight(), true);
		
		this.addProcessor(this.stage);
	}

	/**
	 * Render the controller's state
	 */
	public abstract void render();
	
	/**
	 * Returns the GdxConfiguration for the ControllerUI
	 * (some may require Accelerometer access etc.)
	 * @return
	 */
	public abstract GdxConfiguration getConfiguration();

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
	 * Nested processor class for ControllerUI
	 * @author Marcel
	 *
	 */
	protected abstract class ControllerProcessor {
		
		/** Process the given NetMessage. @param m */
		public abstract void process(NetMessage m);
	}
}
