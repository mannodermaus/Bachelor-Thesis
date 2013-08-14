package de.hsb.ms.syn.desktop;

import java.util.Collection;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import de.hsb.ms.syn.common.ui.ConnectionStatusIcon;
import de.hsb.ms.syn.common.ui.TouchMatrixPad;
import de.hsb.ms.syn.common.ui.TouchMatrixPad.TouchMatrixEvent;
import de.hsb.ms.syn.common.ui.TouchMatrixPad.TouchMatrixListener;
import de.hsb.ms.syn.common.util.Constants;
import de.hsb.ms.syn.common.util.Utils;
import de.hsb.ms.syn.common.vo.NodesStage;
import de.hsb.ms.syn.common.vo.Scale;
import de.hsb.ms.syn.common.vo.fx.LFO;
import de.hsb.ms.syn.common.vo.fx.TapDelay;
import de.hsb.ms.syn.common.vo.gen.Sawtooth;
import de.hsb.ms.syn.common.vo.gen.Sinewave;
import de.hsb.ms.syn.common.vo.gen.Square;
import de.hsb.ms.syn.common.vo.gen.Triangle;
import de.hsb.ms.syn.common.vo.nodes.FXNode;
import de.hsb.ms.syn.common.vo.nodes.GenNode;
import de.hsb.ms.syn.desktop.abs.Node;

/**
 * Rendering unit of the Synthesizer.
 * This Singleton is responsible for the correct display of
 * relevant components, such as the Node graph structure.
 * @author Marcel
 *
 */
public class SynRenderer {
	
	// Singleton instance
	private static SynRenderer instance;
	
	private static Skin skin;
	
	// Graphical elements of the renderer
	private SpriteBatch batch;
	private OrthographicCamera camera;
	
	// Background textures
	private Texture background;
	private Texture shine;
	private float bgScrollX;
	private int wrapThreshold;
	
	// Stages: One for UI, one for Node graph
	private NodesStage stage;
	private Stage ui;
	private ConnectionStatusIcon connectionStatus;

	/**
	 * Constructor
	 */
	private SynRenderer() {
		float w = 800;
		float h = 600;

		// Init graphical elements
		camera = new OrthographicCamera(w, h);
		camera.update();
		
		// Init background textures
		background = new Texture(Gdx.files.internal(String.format(Constants.PATH_UI, "bg")));
		shine = new Texture(Gdx.files.internal(String.format(Constants.PATH_UI, "shine")));
		bgScrollX = 0;
		wrapThreshold = background.getWidth() - Gdx.graphics.getWidth();
		
		// Init stages
		stage = new NodesStage(w, h, true);
		stage.setCamera(camera);
		ui = new Stage(w, h, true);
		
		connectionStatus = new ConnectionStatusIcon(Synthesizer.connection);
		int x = (Gdx.graphics.getWidth() / 2) - connectionStatus.getWidth();
		int y = (Gdx.graphics.getHeight() / 2) - connectionStatus.getHeight();
		connectionStatus.setPosition(x, y);
		
		batch = stage.getSpriteBatch();
		
		// Init user interface components
		this.initUI();
	}
	
	/**
	 * Initialization of UI components
	 */
	private void initUI() {
		
		Table wrapper = new Table();
		wrapper.setFillParent(true);
		wrapper.align(Align.top | Align.left);
		wrapper.pad(50);
		wrapper.row().fill();

		skin = new Skin(Gdx.files.internal("data/ui.json"));
		//skin = new Skin(Gdx.files.internal("data/pack.json"));
		
		ui.addActor(wrapper);
		
		final TextButton addButtonsq = new TextButton("Square", skin);
		final TextButton addButtonsw = new TextButton("Sinewave", skin);
		final TextButton addButtonst = new TextButton("Sawtooth", skin);
		final TextButton addButtontr = new TextButton("Triangle", skin);
		final TextButton addButtonfx = new TextButton("LFO (Sinewave)", skin);
		final TextButton addButtonfx2 = new TextButton("LFO (Sawtooth)", skin);
		final TextButton addButtondl = new TextButton("Tap Delay", skin);
		final TextButton removeButton = new TextButton("Remove last Node", skin);
		final TextButton mapButton = new TextButton("Print Node map", skin);
		final ImageButton connectButton = new ImageButton(skin);
		
//		// TODO delete me
//		final TextField moveFieldTest = new TextField("Drag me to hell, dude", skin);
//		ui.addActor(moveFieldTest);
//		moveFieldTest.setWidth(400);
//		moveFieldTest.setPosition(300, 300);
//		moveFieldTest.addListener(new DragListener() {
//			private float startx;
//			private float starty;
//			private long starttime;
//			
//			@Override
//			public void dragStart(InputEvent event, float x, float y, int pointer) {
//				startx = x;
//				starty = y;
//				starttime = System.currentTimeMillis();
//			}
//			
//			@Override
//			public void drag(InputEvent event, float x, float y, int pointer) {
//				float minx = Math.min(Math.max(event.getStageX() - startx, 250), 350);
//				float miny = Math.min(Math.max(event.getStageY() - starty, 250), 350);
//				moveFieldTest.setPosition(minx, miny);
//			}
//			
//			@Override
//			public void dragStop(InputEvent event, float x, float y, int pointer) {
//				super.dragStop(event, x, y, pointer);
//				long now = System.currentTimeMillis();
//				long delta = now - starttime;
//				Utils.log("That drag took " + delta + "ms");
//				Utils.log("Moving back to " +startx+","+starty);
//				moveFieldTest.setPosition(startx, starty);
//			}
//		});
		
		// TODO delete me
		TouchMatrixPad pad = new TouchMatrixPad(skin);
		pad.setPosition(100, 100);
		pad.addTouchMatrixListener(new TouchMatrixListener() {

			@Override
			public void touchMatrixChanged(TouchMatrixEvent tme) {
				Utils.log("Yo I got this TouchMatrixEvent: " + tme.getXpercentage() + "," + tme.getYpercentage());
			}
			
		});
		ui.addActor(pad);
		
		connectButton.add(new Image(Synthesizer.connection.getIconTexture()));
		
		addButtonsq.addListener(new ChangeListener() {
			public void changed(ChangeEvent ev, Actor ac) {
				GenNode n = new GenNode(0, Utils.randomPosition());
				n.setDelegate(new Square(new Scale(Scale.BASE_C, Scale.MODE_MAJ_PENTA)));
				SynAudioProcessor.getInstance().addNode(n);
			}
		});
		
		wrapper.add(addButtonsq);
		wrapper.row().fill();

		addButtonsw.addListener(new ChangeListener() {
			public void changed(ChangeEvent ev, Actor ac) {
				GenNode n = new GenNode(0, Utils.randomPosition());
				n.setDelegate(new Sinewave(new Scale(Scale.BASE_C, Scale.MODE_MAJ_OCTAVE)));
				SynAudioProcessor.getInstance().addNode(n);
			}
		});
		
		wrapper.add(addButtonsw);
		wrapper.row().fill();

		addButtonst.addListener(new ChangeListener() {
			public void changed(ChangeEvent ev, Actor ac) {
				GenNode n = new GenNode(0, Utils.randomPosition());
				n.setDelegate(new Sawtooth(new Scale(Scale.BASE_A, Scale.MODE_MIN_OCTAVE)));
				SynAudioProcessor.getInstance().addNode(n);
			}
		});
		
		wrapper.add(addButtonst);
		wrapper.row().fill();

		addButtontr.addListener(new ChangeListener() {
			public void changed(ChangeEvent ev, Actor ac) {
				GenNode n = new GenNode(0, Utils.randomPosition());
				n.setDelegate(new Triangle(new Scale(Scale.BASE_A, Scale.MODE_MIN_OCTAVE)));
				SynAudioProcessor.getInstance().addNode(n);
			}
		});
		
		wrapper.add(addButtontr);
		wrapper.row().fill();

		addButtonfx.addListener(new ChangeListener() {
			public void changed(ChangeEvent ev, Actor ac) {
				FXNode n = new FXNode(2, Utils.randomPosition());
				n.setDelegate(new LFO(0.66f, Sinewave.class));
				SynAudioProcessor.getInstance().addNode(n);
			}
		});
		
		wrapper.add(addButtonfx);
		wrapper.row().fill();

		addButtonfx2.addListener(new ChangeListener() {
			public void changed(ChangeEvent ev, Actor ac) {
				FXNode n = new FXNode(2, Utils.randomPosition());
				n.setDelegate(new LFO(2f, Sawtooth.class));
				SynAudioProcessor.getInstance().addNode(n);
			}
		});
		
		wrapper.add(addButtonfx2);
		wrapper.row().fill();

		addButtondl.addListener(new ChangeListener() {
			public void changed(ChangeEvent ev, Actor ac) {
				FXNode n = new FXNode(2, Utils.randomPosition());
				n.setDelegate(new TapDelay(0.5f, 0.6f, 0.4f));
				SynAudioProcessor.getInstance().addNode(n);
			}
		});
		
		wrapper.add(addButtondl);
		wrapper.row().fill();
		
		removeButton.addListener(new ChangeListener() {
			public void changed(ChangeEvent ev, Actor ac) {
				Collection<Node> nodes = SynAudioProcessor.getInstance().getNodes().values();
				if (nodes.size() > 0) {
					SynAudioProcessor.getInstance().removeLastNode();
				}
			}
		});
		
		wrapper.add(removeButton);
		wrapper.row().fill();
		
		mapButton.addListener(new ChangeListener() {
			public void changed(ChangeEvent ev, Actor ac) {
				SynAudioProcessor.getInstance().printNodeMap();
			}
		});
		
		wrapper.add(mapButton);
		wrapper.row().fill();
	}
	
	/**
	 * Singleton access method
	 * @return
	 */
	public static SynRenderer getInstance() {
		if (instance == null)
			instance = new SynRenderer();
		return instance;
	}
	
	/**
	 * Render method.
	 * The heart of the rendering unit. Update camera and matrices,
	 * order the stage to draw itself and draw the UI on top.
	 */
	public void render() {
		// OGL clear commands
		Gdx.gl.glClearColor(0.8f, 0.8f, 0.947f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		// Update camera and projection matrices
		camera.update();
		batch.setProjectionMatrix(camera.combined);

		// Draw moving background (with wrap-around)
		batch.begin();
		batch.draw(background, (-Gdx.graphics.getWidth()/2) - bgScrollX, -Gdx.graphics.getHeight()/2);
		// If the scrolling summand exceeds the texture's right bound, draw another instance to fill the gap
		bgScrollX = (bgScrollX > background.getWidth()) ? 0 : bgScrollX + 1;
		if (bgScrollX > wrapThreshold) {
			float diff = bgScrollX - wrapThreshold;
			batch.draw(background, (Gdx.graphics.getWidth()/2) - diff, -Gdx.graphics.getHeight()/2);
		}
		batch.draw(shine, -shine.getWidth()/2, -shine.getHeight()/2);
		batch.end();
		
		// Draw the stage and handle input events
		stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 60));
		// Draw connections between Nodes
		stage.drawConnections();
		// Draw Nodes themselves
		stage.draw();
		
		// Draw the UI on top
		ui.draw();
		
		// Draw the connection status icon
		connectionStatus.draw(batch);
	}
	
	/**
	 * Get the Node graph Stage
	 * @return
	 */
	public Stage getNodesStage() {
		return stage;
	}
	
	/**
	 * Get the UI Stage
	 * @return
	 */
	public Stage getUIStage() {
		return ui;
	}
}
