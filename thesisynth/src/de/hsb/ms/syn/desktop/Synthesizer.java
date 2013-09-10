package de.hsb.ms.syn.desktop;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.FPSLogger;

import de.hsb.ms.syn.common.interfaces.Connection;
import de.hsb.ms.syn.common.interfaces.DesktopConnection;
import de.hsb.ms.syn.common.interfaces.NetCapableApplicationListener;
import de.hsb.ms.syn.common.net.NetMessage;

/**
 * Desktop-sided synthesizer module
 * @author Marcel
 *
 */
public class Synthesizer implements NetCapableApplicationListener {

	@SuppressWarnings("unused")
	/** FPS logger */
	private FPSLogger fps;
	
	/** Audio processing unit */
	private SynthesizerAudioProcessor audioProcessor;
	
	/** State rendering unit */
	private SynthesizerRenderer renderer;
	
	/** Input multiplexer for the synthesizer */
	private InputMultiplexer input;
	
	/** Connection */
	private static DesktopConnection connection;
	
	/** Network processing unit */
	private SynthesizerNetworkProcessor netProcessor;
	
	@Override
	public void create() {
		
		// FPS logger
		fps = new FPSLogger();

		// Connection
		connection.init();
		connection.connect();
		
		// Synthesizer processing unit
		audioProcessor = SynthesizerAudioProcessor.getInstance();
		
		// Input multiplexer
		input = new InputMultiplexer();
		Gdx.input.setInputProcessor(input);
		
		// Rendering of state (special getInstance method is used here. It provides the connection for the status icon)
		renderer = SynthesizerRenderer.getInstance(connection);
		
		// Processor handles Nodes, renderer renders them - both need the Stage to act upon in their respective field!
		audioProcessor.setStage(renderer.getNodesStage());
		
		// Add processors to input multiplexer:
		// The UI has its own processor
		input.addProcessor(renderer.getUIStage());
		// The renderer's Stage handles Node clicking and dragging
		input.addProcessor(renderer.getNodesStage());
		
		// Initialize logic processor
		audioProcessor.init();
		
		// Initialize net message processor
		netProcessor = new SynthesizerNetworkProcessor(audioProcessor);
	}

	@Override
	public void resize(int width, int height) { }

	@Override
	public void render() {
		
		// Display FPS in console
		// fps.log();
		
		// Check for a new net message & process it
		netProcessor.processNetMessage();
		
		// Process a step (audio etc)
		audioProcessor.processAudio();
		
		// Render the state
		renderer.render();
	}

	@Override
	public void pause() { }

	@Override
	public void resume() { }

	@Override
	public void dispose() { }

	@Override
	public void onNetMessageReceived(NetMessage message) {
		// Set the new message.
		// The new message will be consumed by the NetMessageProcessor
		// during the next render() cycle
		netProcessor.setMessage(message);
	}

	@Override
	public void setConnection(Connection c) {
		if (c instanceof DesktopConnection)
			connection = (DesktopConnection) c;
	}

	/**
	 * Broadcast a NetMessage to all mobile devices, excluding those specified in excludeIDs
	 * @param m
	 * @param excludeIDs
	 */
	public static void broadcast(NetMessage m, Integer... excludeIDs) {
		connection.broadcast(m, excludeIDs);
	}
	
	/**
	 * Send a NetMessage to the mobile device with the given ID
	 * @param m
	 * @param toID
	 */
	public static void send(NetMessage m, int toID) {
		connection.send(m, toID);
	}
	
	/**
	 * Disconnect the mobile device with the given ID
	 * @param id
	 */
	public static void disconnect(int id) {
		try {
			connection.disconnect(id);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
