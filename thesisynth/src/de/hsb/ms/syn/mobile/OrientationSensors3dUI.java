package de.hsb.ms.syn.mobile;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.lights.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.lights.Lights;
import com.badlogic.gdx.graphics.g3d.materials.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.materials.Material;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import de.hsb.ms.syn.common.audio.Properties;
import de.hsb.ms.syn.common.net.NetMessage;
import de.hsb.ms.syn.common.net.NetMessageFactory;
import de.hsb.ms.syn.common.net.NetMessage.Command;
import de.hsb.ms.syn.common.util.Utils;

/**
 * Orientation 3D Sensor UI
 * 
 * Third iteration of Controller UI. This final UI allows the user to
 * use the device's orientation and accelerometer sensors to change Node parameters
 * 
 * @author Marcel
 * 
 */
public class OrientationSensors3dUI extends ControllerUI {

	// UI components
	private Table listPanel;
	private List nodeList;
	
	// 3D Rendering
	private Lights lights;
	private PerspectiveCamera modelCamera;
	private ModelBatch modelBatch;
	
	// Models
	private Model modelBox;
	private Model modelAxisX;
	private Model modelAxisY;
	private Model modelAxisZ;
	
	private ModelInstance instanceBox;
	private ModelInstance instanceAxisX;
	private ModelInstance instanceAxisY;
	private ModelInstance instanceAxisZ;
	
	// Computational temp values
	private Vector2 tempPosition;
	private Vector2 lastPosition;
	private Vector3 lookAtPoint;
	
	private float cameraDistance;
	private float rotationFactor;
	private float rotationThreshold;
	
	// Logic
	private Properties selectedNodeProperties;
	
	@Override
	public void init(SynthesizerController context) {
		super.init(context);
		
		this.processor = new Orientation3DSensorProcessor();
		
		// Initialize models and vector data
		this.modelBatch = new ModelBatch();
		
		this.lookAtPoint = new Vector3(0, 0, 0);
		this.tempPosition = new Vector2();
        this.lastPosition = new Vector2();
		
		float radius = 3.5f;
		ModelBuilder modelBuilder = new ModelBuilder();
		this.modelBox = modelBuilder.createBox(radius, radius, radius, new Material(ColorAttribute.createDiffuse(Color.ORANGE)), Usage.Position | Usage.Normal);
		this.modelAxisX = modelBuilder.createCylinder(8, 0.2f, 0.2f, 3, new Material(ColorAttribute.createDiffuse(Color.GREEN)), Usage.Position | Usage.Normal);
		this.modelAxisY = modelBuilder.createCylinder(0.2f, 8, 0.2f, 3, new Material(ColorAttribute.createDiffuse(Color.RED)), Usage.Position | Usage.Normal);
		this.modelAxisZ = modelBuilder.createCylinder(0.2f, 0.2f, 8, 3, new Material(ColorAttribute.createDiffuse(Color.BLUE)), Usage.Position | Usage.Normal);
        
		this.instanceBox = new ModelInstance(modelBox);
		this.instanceAxisX = new ModelInstance(modelAxisX);
		this.instanceAxisY = new ModelInstance(modelAxisY);
		this.instanceAxisZ = new ModelInstance(modelAxisZ);
		
		// Initialize 3D Rendering
		this.lights = new Lights();
		this.lights.ambientLight.set(0.4f, 0.4f, 0.4f, 1f);
		this.lights.add(new DirectionalLight().set(0.55f, 0.2f, 0.69f, -2f, -1.8f, -1.2f));
		this.lights.add(new DirectionalLight().set(0.56f, 0.7f, 0.32f, 1f, 0.8f, 0.2f));
		
		// Initialize camera to render 3D models
		this.modelCamera = new PerspectiveCamera(67, WIDTH, HEIGHT);
		this.modelCamera.position.set(5, 3, 2);
		this.modelCamera.lookAt(lookAtPoint);
		this.modelCamera.near = 0.1f;
		this.modelCamera.far = 150f;
		this.modelCamera.update();
		
		// Initialize computational values
		this.cameraDistance = Vector3.dst(modelCamera.position.x, modelCamera.position.y, modelCamera.position.z, lookAtPoint.x, lookAtPoint.y, lookAtPoint.z);
		this.rotationFactor = 2.5f;
		this.rotationThreshold = 0.055f;
        
		// Initialize UI
		int h = HEIGHT - MENUHEIGHT;
		this.listPanel = new Table();
		this.listPanel.align(Align.top | Align.left);
		// Nest ListPanel inside of a ScrollPane
		ScrollPane scroll = new ScrollPane(listPanel, getSkin());
		scroll.setOverscroll(false, false);
		scroll.setSmoothScrolling(true);
		scroll.setScrollingDisabled(true, false);
		scroll.setScrollbarsOnTop(true);
		
		// Fill the list panel
		this.nodeList = new List(new String[] { "" }, getSkin());
		this.listPanel.add(nodeList);
		
		this.contents.add(scroll).minHeight(h).maxHeight(h).minWidth(200).left();
		
		// Add listeners
		this.nodeList.addListener(new ChangeListener() {
			public void changed(ChangeEvent ev, Actor ac) {
				int selected = ((List) ac).getSelectedIndex();
				selectNode(selected);
				// Send a SELECTNODE message to Desktop side
				NetMessage msg = NetMessageFactory.create(Command.SELECTNODE, getNodeIdAt(mSelectedNodePropertiesIndex));
				connection.send(msg);
			}
		});
	}
	
	@Override
	public void render() {
		// Compute the device's current orientation sensor data
		tempPosition.set((int) (Gdx.input.getPitch() + 90), (int) (Gdx.input.getRoll() + 180));
		tempPosition.set((float) Math.toRadians(tempPosition.y * rotationFactor),
						 (float) Math.toRadians(tempPosition.x * rotationFactor));
		
		// Using the threshold value, find out if a significant change in orientation has occurred
		// (this prevents twitching because of sensitive sensor data)
		if (Math.abs(tempPosition.x - lastPosition.x) > rotationThreshold ||
			Math.abs(tempPosition.y - lastPosition.y) > rotationThreshold) {
			// Set the camera position vector using spherical to cartesian coordinate conversion:
			// x = dst * cos(roll) * sin(pitch)
			// y = dst * sin(roll) * sin(pitch)
			// z = dst * cos(pitch)
			float cosRoll	= (float) Math.cos(tempPosition.y);
			float sinRoll	= (float) Math.sin(tempPosition.y);
			float cosPitch	= (float) Math.cos(tempPosition.x);
			float sinPitch	= (float) Math.sin(tempPosition.x);
			
			modelCamera.position.set(cameraDistance * cosRoll * sinPitch,
							 		 cameraDistance * sinRoll * sinPitch,
							 		 cameraDistance * cosPitch);
			modelCamera.lookAt(lookAtPoint);
			modelCamera.update();
			
			// Save this position
			lastPosition.set(tempPosition);
		}
		
		// Render models using a smaller GL viewport
        Gdx.gl.glViewport(200, 0, WIDTH - 200, HEIGHT - MENUHEIGHT);
		
		modelBatch.begin(modelCamera);
        modelBatch.render(instanceAxisX, lights);
        modelBatch.render(instanceAxisY, lights);
        modelBatch.render(instanceAxisZ, lights);
        modelBatch.render(instanceBox, lights);
        modelBatch.end();
        
        // Render the UI using the full-screen viewport
        Gdx.gl.glViewport(0, 0, WIDTH, HEIGHT);
		super.render();
	}
	
	@Override
	public void dispose() {
		super.dispose();
		
		modelBatch.dispose();
		modelBox.dispose();
		modelAxisX.dispose();
		modelAxisY.dispose();
		modelAxisZ.dispose();
	}
	
	private void selectNode(int index) {
		mSelectedNodePropertiesIndex = index;
		
		if (index > -1) {
			nodeList.setSelectedIndex(index);
			
			int id = getNodeIdAt(index);
			selectedNodeProperties = mNodePropertiesMap.get(id);
		}
	}
	
	@Override
	public void updateUI() {
		updateNodeList();
		selectNode(mSelectedNodePropertiesIndex);
	}
	
	private void updateNodeList() {
		if (mNodePropertiesMap != null) {
			String[] items = new String[mNodePropertiesMap.size()];
			Iterator<Integer> IDiter = mNodePropertiesMap.keySet().iterator();
	
			for (int index = 0; index < items.length; index++) {
				int id = IDiter.next();
				// Update UI list
				items[index] = String.format("%s%d", mNodePropertiesMap.get(id)
						.name(), id);
			}
			nodeList.setItems(items);
		}
	}

	private class Orientation3DSensorProcessor extends ControllerProcessor {

		@Override
		public void process(NetMessage message) {
			// Access the message's extras
			Set<String> extras = message.getExtras();

			// Send ID message: The SynConnectionManager has sent an ID for this device's connection
			if (extras.contains(NetMessage.CMD_SENDID)) {
				int id = message.getInt(NetMessage.EXTRA_CONNID);
				Utils.log("Got my ID from the Desktop Synthesizer. It is " + id);
				connection.setID(id);
				
				float[] colorVals = (float[]) message.getExtra(NetMessage.EXTRA_COLORVALS);
				Color color = new Color(colorVals[0], colorVals[1], colorVals[2], 1.0f);
				OrientationSensors3dUI.this.getContext().setColor(color);

				// Send a "HELLO" message to the desktop
				Utils.log("Connected.");
				NetMessage m = NetMessageFactory.create(Command.HELLO);
				m.addExtra(NetMessage.CMD_HELLO, "");
				connection.send(m);
			}
			
			// Send Nodes message: Update the property Tables etc.
			if (extras.contains(NetMessage.CMD_SENDNODES)) {
				@SuppressWarnings("unchecked")
				HashMap<Integer, Properties> props = (HashMap<Integer, Properties>) message.getExtra(NetMessage.EXTRA_NODESTRUCTURE);
				mNodePropertiesMap = props;
				
				updateNodeList();

				// Auto-select the first item if none is selected at the moment
				if (mSelectedNodePropertiesIndex == -1 && mNodePropertiesMap.size() > 0) {
					selectNode(0);
					NetMessage msg = NetMessageFactory.create(Command.SELECTNODE, (Integer) getNodeIdAt(mSelectedNodePropertiesIndex));
					connection.send(msg);
				} else if (mNodePropertiesMap.size() == 0) {
					// If no nodes remain on the synthesizer surface, delete the slider table
					selectNode(-1);
				}
			}
		}

	}
}
