package de.hsb.ms.syn.mobile.ui;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.lights.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.lights.Lights;
import com.badlogic.gdx.graphics.g3d.materials.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.materials.Material;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import de.hsb.ms.syn.common.util.NetMessageFactory;
import de.hsb.ms.syn.common.util.NetMessages;
import de.hsb.ms.syn.common.util.NetMessages.Command;
import de.hsb.ms.syn.common.util.Utils;
import de.hsb.ms.syn.common.vo.NetMessage;
import de.hsb.ms.syn.common.vo.NodeProperties;
import de.hsb.ms.syn.mobile.abs.ControllerUI;

/**
 * Orientation 3D Sensor UI
 * 
 * Third iteration of Controller UI. This final UI allows the user to
 * use the device's orientation and accelerometer sensors to change Node parameters
 * 
 * @author Marcel
 * 
 */
public class Orientation3DSensorUI extends ControllerUI {

	// UI components
	private Table listPanel;
	private List nodeList;
	private NumberFormat format;
	private BitmapFont font;
	
	// 3D Rendering
	private Lights lights;
	private PerspectiveCamera cam;
	private ModelBatch modelBatch;
	private Model model;
	private ModelInstance instance;
	
	private ModelInstance axisX;
	private ModelInstance axisY;
	private ModelInstance axisZ;
	
	private Quaternion rotation = new Quaternion();
	
	// Logic
	private NodeProperties selectedNodeProperties;

	
	@Override
	public void init() {
		super.init();
		
		this.processor = new Orientation3DSensorProcessor();
		
		// Initialize 3D Rendering
		lights = new Lights();
		lights.ambientLight.set(0.4f, 0.4f, 0.4f, 1f);
		lights.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

		modelBatch = new ModelBatch();

		cam = new PerspectiveCamera(67, WIDTH, HEIGHT);
		cam.position.set(15f, 10f, 10f);
		cam.lookAt(0, 0, 5);
		cam.near = 0.1f;
		cam.far = 300f;
		cam.update();
		
		float radius = 12.5f;
		
		ModelBuilder modelBuilder = new ModelBuilder();
        model = modelBuilder.createBox(radius, radius, radius, new Material(ColorAttribute.createDiffuse(Color.ORANGE)), Usage.Position | Usage.Normal);
        instance = new ModelInstance(model);
        axisX = new ModelInstance(modelBuilder.createCylinder(8, 0.2f, 0.2f, 3, new Material(ColorAttribute.createDiffuse(Color.GREEN)), Usage.Position | Usage.Normal));
        axisY = new ModelInstance(modelBuilder.createCylinder(0.2f, 8, 0.2f, 3, new Material(ColorAttribute.createDiffuse(Color.RED)), Usage.Position | Usage.Normal));
        axisZ = new ModelInstance(modelBuilder.createCylinder(0.2f, 0.2f, 8, 3, new Material(ColorAttribute.createDiffuse(Color.BLUE)), Usage.Position | Usage.Normal));
        
		// Initialize UI
		int h = HEIGHT - MENUHEIGHT;

		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setDecimalSeparator('.');
		format = new DecimalFormat("#.##", symbols);
		format.setGroupingUsed(true);
		font = new BitmapFont();
		
		listPanel = new Table();
		// Nest ListPanel inside of a ScrollPane
		ScrollPane scroll = new ScrollPane(listPanel, getSkin());
		listPanel.align(Align.top | Align.left);
		scroll.setOverscroll(false, false);
		scroll.setSmoothScrolling(true);
		scroll.setScrollingDisabled(true, false);
		scroll.setScrollbarsOnTop(true);
		
		// Fill the list panel
		nodeList = new List(new String[] { "" }, getSkin());
		listPanel.add(nodeList);
		
		contents.add(scroll).minHeight(h).maxHeight(h).minWidth(200).left();
		
		nodeList.addListener(new ChangeListener() {
			public void changed(ChangeEvent ev, Actor ac) {
				int selected = ((List) ac).getSelectedIndex();
				selectNode(selected);
				// Send a SELECTNODE message to Desktop side
				NetMessage msg = NetMessageFactory.create(Command.SELECTNODE, (Integer) mNodePropertiesMap.keySet().toArray()[mSelectedNodePropertiesIndex]);
				connection.send(msg);
			}
		});
	}
	
	private Vector3 point = Vector3.Zero;
	private Vector3 axis = new Vector3(0, 1, 0);
	
	private float[] matrix = new float[] {1, 0, 0, 0,
										  0, 1, 0, 0,
										  0, 0, 1, 0,
										  0, 0, 0, 1 };
	
	@Override
	public void render() {
		super.render();
		

 		// Get rotational values
// 		float p = Gdx.input.getPitch();
// 		float r = Gdx.input.getRoll();
// 		float a = Gdx.input.getAzimuth();
 		
 		Gdx.input.getRotationMatrix(matrix);
 		// TODO ???
		cam.lookAt(point);
		cam.combined.set(matrix);
		cam.update();
		
        modelBatch.begin(cam);
        modelBatch.render(axisX, lights);
        modelBatch.render(axisY, lights);
        modelBatch.render(axisZ, lights);
//        modelBatch.render(instance, lights);
        modelBatch.end();
        
//        // Get accelerometer values
// 		float x = Gdx.input.getAccelerometerX();
// 		float y = Gdx.input.getAccelerometerY();
// 		float z = Gdx.input.getAccelerometerZ();
//
// 		// Convert accelerometer values from their usual scale to an RGB scale
// 		// [0,1]
// 		float cx = Float.parseFloat(format.format(Utils.getScaleConvertedValue(
// 				x, -10, 10, 0, 1)));
// 		float cy = Float.parseFloat(format.format(Utils.getScaleConvertedValue(
// 				y, -10, 10, 0, 1)));
// 		float cz = Float.parseFloat(format.format(Utils.getScaleConvertedValue(
// 				z, -10, 10, 0, 1)));
// 		
 		// Draw sensor data
		SpriteBatch batch = getSpriteBatch();
		batch.begin();
//		font.draw(batch, String.format("[x] %f (PITCH)", p), 50, 75);
//		font.draw(batch, String.format("[y] %f (ROLL)", r), 50, 100);
//		font.draw(batch, String.format("[z] %f (AZIMUTH)", a), 50, 125);
		font.draw(batch, rotation.toString(), 50, 100);

//		font.draw(batch, String.format("[Accelerometer x] %f", x), 50, 150);
//		font.draw(batch, String.format("[Accelerometer y] %f", y), 50, 175);
//		font.draw(batch, String.format("[Accelerometer z] %f", z), 50, 200);
//
//		font.draw(batch, String.format("[Converted x] %.2f", cx), 50, 250);
//		font.draw(batch, String.format("[Converted y] %.2f", cy), 50, 275);
//		font.draw(batch, String.format("[Converted z] %.2f", cz), 50, 300);
		batch.end();
	}
	
	@Override
	public void dispose() {
		super.dispose();
		
		modelBatch.dispose();
		model.dispose();
	}
	
	private void selectNode(int index) {
		mSelectedNodePropertiesIndex = index;
		
		if (index > -1) {
			nodeList.setSelectedIndex(index);
			
			int id = (Integer) mNodePropertiesMap.keySet().toArray()[mSelectedNodePropertiesIndex];
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
			if (extras.contains(NetMessages.CMD_SENDID)) {
				int id = message.getInt(NetMessages.EXTRA_CONNID);
				Utils.log("Got my ID from the Desktop Synthesizer. It is " + id);
				connection.setID(id);

				// Send a "HELLO" message to the desktop
				Utils.log("Connected.");
				NetMessage m = NetMessageFactory.create(Command.HELLO);
				m.addExtra(NetMessages.CMD_HELLO, "");
				connection.send(m);
			}
			
			// Send Nodes message: Update the property Tables etc.
			if (extras.contains(NetMessages.CMD_SENDNODES)) {
				@SuppressWarnings("unchecked")
				HashMap<Integer, NodeProperties> props = (HashMap<Integer, NodeProperties>) message.getExtra(NetMessages.EXTRA_NODESTRUCTURE);
				mNodePropertiesMap = props;
				
				updateNodeList();

				// Auto-select the first item if none is selected at the moment
				if (mSelectedNodePropertiesIndex == -1 && mNodePropertiesMap.size() > 0) {
					selectNode(0);
					NetMessage msg = NetMessageFactory.create(Command.SELECTNODE, (Integer) mNodePropertiesMap
							.keySet().toArray()[mSelectedNodePropertiesIndex]);
					connection.send(msg);
				} else if (mNodePropertiesMap.size() == 0) {
					// If no nodes remain on the synthesizer surface, delete the slider table
					selectNode(-1);
				}
			}
		}

	}
}
