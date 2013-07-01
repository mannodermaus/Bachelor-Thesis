package de.hsb.ms.syn.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.FPSLogger;

import de.hsb.ms.syn.common.abs.Connection;
import de.hsb.ms.syn.common.abs.DesktopConnection;
import de.hsb.ms.syn.common.interfaces.NetCapableApplicationListener;
import de.hsb.ms.syn.common.vo.NetMessage;

/**
 * Desktop-sided synthesizer module
 * @author Marcel
 *
 */
public class Synthesizer implements NetCapableApplicationListener {

	// FPS logger
	@SuppressWarnings("unused")
	private FPSLogger fps;
	
	// Synthesizer processing unit
	private SynProcessor processor;
	
	// State rendering unit
	private SynRenderer renderer;
	
	// Input multiplexer for the synthesizer
	private InputMultiplexer input;
	
	// Network
	public static DesktopConnection connection;
	private NetMessageProcessor netMessageProcessor;
	
	@Override
	public void create() {
		
		// FPS logger
		fps = new FPSLogger();

		// Connection
		connection.init();
		connection.connect();
		
		// Synthesizer processing unit
		processor = SynProcessor.getInstance();
		
		// Input multiplexer
		input = new InputMultiplexer();
		Gdx.input.setInputProcessor(input);
		
		// Rendering of state
		renderer = SynRenderer.getInstance();
		
		// Processor handles Nodes, renderer renders them - both need the Stage to act upon in their respective field!
		processor.setStage(renderer.getNodesStage());
		
		// Add processors to input multiplexer:
		// The UI has its own processor
		input.addProcessor(renderer.getUIStage());
		// The renderer's Stage handles Node clicking and dragging
		input.addProcessor(renderer.getNodesStage());
		
		// Initialize logic processor
		processor.init();
		
		// Initialize net message processor
		netMessageProcessor = new NetMessageProcessor(processor);
	}

	@Override
	public void resize(int width, int height) { }

	@Override
	public void render() {
		
		// Display FPS in console
		// fps.log();
		
		// Check for a new net message & process it
		netMessageProcessor.processMessage();
		
		// Process a step (audio etc)
		processor.process();
		
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
		netMessageProcessor.setMessage(message);
	}

	@Override
	public void setConnection(Connection c) {
		if (c instanceof DesktopConnection)
			connection = (DesktopConnection) c;
	}

}
