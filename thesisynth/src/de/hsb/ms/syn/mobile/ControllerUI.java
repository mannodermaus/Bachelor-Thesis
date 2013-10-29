package de.hsb.ms.syn.mobile;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

import de.hsb.ms.syn.common.audio.Properties;
import de.hsb.ms.syn.common.interfaces.AndroidConnection;
import de.hsb.ms.syn.common.interfaces.Connection;
import de.hsb.ms.syn.common.net.NetMessage;
import de.hsb.ms.syn.common.net.NetMessage.Command;
import de.hsb.ms.syn.common.net.NetMessageFactory;
import de.hsb.ms.syn.common.ui.ConnectingDialog;
import de.hsb.ms.syn.common.ui.ConnectionFailedDialog;
import de.hsb.ms.syn.common.util.Utils;

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

	/** Base stage where everything is added to */
	private Stage stage;
	/** Table containing the content of the ControllerUI */
	protected Table contents;
	/** List panel for the Node list */
	protected Table listPanel;
	/** Node list */
	protected List nodeList;

	/** Connection for network access */
	protected AndroidConnection connection;
	/** Processor of network messages for this ControllerUI */
	protected ControllerProcessor processor;

	/** SynthesizerController back-reference */
	private SynthesizerController context;

	/** Skin to use for UI elements */
	private static Skin skin;
	
	/** Height of the ControllerMenu bar */
	protected static int MENUHEIGHT;
	
	/** Width of the List panel */
	protected static int LISTPANELWIDTH;

	/**
	 * Map relating Node ID numbers to their stripped-down algorithm's
	 * Properties
	 */
	protected static Map<Integer, Properties> nodePropMap = null;
	/** Selected Properties index in the node list */
	protected static int selectedPropIndex = -1;

	/**
	 * Initialization method
	 * 
	 * @param context
	 */
	public void init(SynthesizerController context) {
		// this.stage = new Stage(Gdx.graphics.getWidth(),
		// Gdx.graphics.getHeight(), true);
		this.context = context;
		this.stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		this.contents = new Table();
		
		LISTPANELWIDTH = Gdx.graphics.getWidth() / 4;
		MENUHEIGHT = (int) ((float) Gdx.graphics.getHeight() / 9.6f);
		
		contents.align(Align.top | Align.left);
		contents.setFillParent(true);
		stage.addActor(contents);
		this.addProcessor(this.stage);
	}

	/**
	 * Returns the ControllerUI's context
	 * 
	 * @return
	 */
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

	/**
	 * Disposes of the controller UI
	 */
	public void dispose() {
		skin.dispose();
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
	 * Returns UI camera
	 * 
	 * @return
	 */
	protected Camera getUICamera() {
		return stage.getCamera();
	}

	/**
	 * Returns UI sprite batch
	 * 
	 * @return
	 */
	protected SpriteBatch getUISpriteBatch() {
		return stage.getSpriteBatch();
	}

	/**
	 * Returns the ID of the NodeProperties object at the given index inside of
	 * the NodeProperties map
	 * 
	 * @param index
	 * @return
	 */
	protected int getNodeIdAt(int index) {
		return (Integer) nodePropMap.keySet().toArray()[selectedPropIndex];
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
	 * Updates the node list
	 */
	protected void updateNodeList() {
		if (nodePropMap != null) {
			String[] items = new String[nodePropMap.size()];
			Iterator<Integer> IDiter = nodePropMap.keySet().iterator();

			for (int index = 0; index < items.length; index++) {
				int id = IDiter.next();
				// Update UI list
				items[index] = String.format("%s%d",
						nodePropMap.get(id).name(), id);
			}
			nodeList.setItems(items);
		}
	}

	/**
	 * Returns the UI skin to use for designing the UI elements
	 * 
	 * @return
	 */
	public static Skin getSkin() {
		if (skin == null) {
			ControllerUI.reloadSkin();
		}
		return skin;
	}

	/**
	 * Load the UI skin to use. This is called by getSkin() if there is no
	 * reference yet, so don't worry about it
	 */
	private static void reloadSkin() {
		skin = new Skin(Gdx.files.internal("data/ui.json"));
		// skin = new Skin(Gdx.files.internal("data/pack.json"));
		
		// Up-scale the font depending on the display's density
		BitmapFont defFont = skin.getFont("default-font");
		float density = Gdx.graphics.getDensity();
		float fontScale = density * (2.0f / 3.0f);
		defFont.setScale(fontScale);
	}

	/**
	 * Updates the ControllerUI state. Will be called upon switching to the UI
	 * again
	 */
	public abstract void updateUI();

	/**
	 * Nested processor class for ControllerUI
	 * 
	 * @author Marcel
	 * 
	 */
	protected abstract class ControllerProcessor {
		
		private ConnectingDialog connectingDialog = new ConnectingDialog(skin);
		private ConnectionFailedDialog connectionFailedDialog = new ConnectionFailedDialog(skin, contents);
		
		
		/**
		 * Process the given NetMessage.
		 * 
		 * @param message
		 * */
		public void process(NetMessage message) {
			// Access the message's extras
			Set<String> extras = message.getExtras();

			// Send ID message: The SynConnectionManager has sent an ID for this
			// device's connection
			if (extras.contains(NetMessage.CMD_SENDID)) {
				int id = message.getInt(NetMessage.EXTRA_CONNID);
				Utils.log("Got my ID from the Desktop Synthesizer. It is " + id);
				connection.setID(id);

				float[] colorVals = (float[]) message
						.getExtra(NetMessage.EXTRA_COLORVALS);
				Color color = new Color(colorVals[0], colorVals[1],
						colorVals[2], 1.0f);
				getContext().setColor(color);

				// Send a "HELLO" message to the desktop
				Utils.log("Connected.");
				NetMessage m = NetMessageFactory.create(Command.HELLO,
						connection.getDeviceName());
				connection.send(m);
			}
			
			// Show "connecting..." window
			if (extras.contains(NetMessage.CMD_SHOWCONNECTING)) {
				connectingDialog.setWidth(Gdx.graphics.getWidth() / 3.0f);
				connectingDialog.setHeight(Gdx.graphics.getHeight() / 6.0f);
				connectingDialog.setX(LISTPANELWIDTH + ((Gdx.graphics.getWidth() - LISTPANELWIDTH) / 2.0f) - (connectingDialog.getWidth() / 2.0f));
				connectingDialog.setY((Gdx.graphics.getHeight() / 2.0f) - (connectingDialog.getHeight() / 2.0f));
				contents.addActor(connectingDialog);
			}
			
			// Hide "connecting..." window
			if (extras.contains(NetMessage.CMD_HIDECONNECTING)) {
				contents.removeActor(connectingDialog);
			}
			
			// Show "connection failed" window
			if (extras.contains(NetMessage.CMD_SHOWCONNECTIONFAILED)) {
				contents.removeActor(connectingDialog);
				connectionFailedDialog.setWidth(Gdx.graphics.getWidth() / 3.0f);
				connectionFailedDialog.setHeight(Gdx.graphics.getHeight() / 4.0f);
				connectionFailedDialog.setX(LISTPANELWIDTH + ((Gdx.graphics.getWidth() - LISTPANELWIDTH) / 2.0f) - (connectingDialog.getWidth() / 2.0f));
				connectionFailedDialog.setY((Gdx.graphics.getHeight() / 2.0f) - (connectingDialog.getHeight() / 2.0f));
				contents.addActor(connectionFailedDialog);
			}
		}
	}
}