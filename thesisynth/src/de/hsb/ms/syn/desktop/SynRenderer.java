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
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import de.hsb.ms.syn.common.ui.ConnectionStatusIcon;
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
	
	private float width = 800;
	private float height = 600;

	/**
	 * Constructor
	 */
	private SynRenderer() {

		// Init graphical elements
		camera = new OrthographicCamera(width, height);
		camera.update();
		
		// Init background textures
		background = new Texture(Gdx.files.internal(String.format(Constants.PATH_UI, "bg")));
		shine = new Texture(Gdx.files.internal(String.format(Constants.PATH_UI, "shine")));
		bgScrollX = 0;
		wrapThreshold = background.getWidth() - Gdx.graphics.getWidth();
		
		// Init stages
		stage = new NodesStage(width, height, true);
		stage.setCamera(camera);
		ui = new Stage(width, height, true);
		
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

		skin = new Skin(Gdx.files.internal("data/ui.json"));
		//skin = new Skin(Gdx.files.internal("data/pack.json"));
		
		// Initialize table wrapper
		Table wrapper = new Table();
		wrapper.setFillParent(true);
		wrapper.align(Align.bottom | Align.left);
		wrapper.pad(0);
		wrapper.row().fill();
		ui.addActor(wrapper);
		
		// Initialize buttons
		final ImageButton addButtonSq = new ImageButton(skin);
		addButtonSq.add(new Image(new Texture(String.format(Constants.PATH_NODE, "node_square"))));
		addButtonSq.row();
		addButtonSq.add(new Label("Square", skin));
		
		final ImageButton addButtonSw = new ImageButton(skin);
		addButtonSw.add(new Image(new Texture(String.format(Constants.PATH_NODE, "node_sinewave"))));
		addButtonSw.row();
		addButtonSw.add(new Label("Sine", skin));
		
		final ImageButton addButtonSt = new ImageButton(skin);
		addButtonSt.add(new Image(new Texture(String.format(Constants.PATH_NODE, "node_sawtooth"))));
		addButtonSt.row();
		addButtonSt.add(new Label("Saw", skin));
		
		final ImageButton addButtonTr = new ImageButton(skin);
		addButtonTr.add(new Image(new Texture(String.format(Constants.PATH_NODE, "node_triangle"))));
		addButtonTr.row();
		addButtonTr.add(new Label("Tri", skin));
		
		final ImageButton addButtonLfoSw = new ImageButton(skin);
		addButtonLfoSw.add(new Image(new Texture(String.format(Constants.PATH_NODE, "node_lfo"))));
		addButtonLfoSw.row();
		addButtonLfoSw.add(new Label("LFO Sine", skin));
		
		final ImageButton addButtonLfoSt = new ImageButton(skin);
		addButtonLfoSt.add(new Image(new Texture(String.format(Constants.PATH_NODE, "node_lfo"))));
		addButtonLfoSt.row();
		addButtonLfoSt.add(new Label("LFO Saw", skin));
		
		final ImageButton addButtonDl = new ImageButton(skin);
		addButtonDl.add(new Image(new Texture(String.format(Constants.PATH_NODE, "node_delay"))));
		addButtonDl.row();
		addButtonDl.add(new Label("Tap Delay", skin));
		
		final TextButton removeButton = new TextButton("Undo", skin);
		
		// Setup UI
		float segWidth = width / 8;
		wrapper.add(addButtonSq).minWidth(segWidth).maxWidth(segWidth);
		wrapper.add(addButtonSw).minWidth(segWidth).maxWidth(segWidth);
		wrapper.add(addButtonSt).minWidth(segWidth).maxWidth(segWidth);
		wrapper.add(addButtonTr).minWidth(segWidth).maxWidth(segWidth);
		wrapper.add(addButtonLfoSw).minWidth(segWidth).maxWidth(segWidth);
		wrapper.add(addButtonLfoSt).minWidth(segWidth).maxWidth(segWidth);
		wrapper.add(addButtonDl).minWidth(segWidth).maxWidth(segWidth);
		wrapper.add(removeButton).minWidth(segWidth).maxWidth(segWidth);
		
		// Initialize listeners
		addButtonSq.addListener(new ChangeListener() {
			public void changed(ChangeEvent ev, Actor ac) {
				GenNode n = new GenNode(0, Utils.randomPosition());
				n.setDelegate(new Square(new Scale(Scale.BASE_C, Scale.MODE_MAJ_PENTA)));
				SynAudioProcessor.getInstance().addNode(n);
			}
		});

		addButtonSw.addListener(new ChangeListener() {
			public void changed(ChangeEvent ev, Actor ac) {
				GenNode n = new GenNode(0, Utils.randomPosition());
				n.setDelegate(new Sinewave(new Scale(Scale.BASE_C, Scale.MODE_MAJ_OCTAVE)));
				SynAudioProcessor.getInstance().addNode(n);
			}
		});

		addButtonSt.addListener(new ChangeListener() {
			public void changed(ChangeEvent ev, Actor ac) {
				GenNode n = new GenNode(0, Utils.randomPosition());
				n.setDelegate(new Sawtooth(new Scale(Scale.BASE_A, Scale.MODE_MIN_OCTAVE)));
				SynAudioProcessor.getInstance().addNode(n);
			}
		});
		
		addButtonTr.addListener(new ChangeListener() {
			public void changed(ChangeEvent ev, Actor ac) {
				GenNode n = new GenNode(0, Utils.randomPosition());
				n.setDelegate(new Triangle(new Scale(Scale.BASE_A, Scale.MODE_MIN_OCTAVE)));
				SynAudioProcessor.getInstance().addNode(n);
			}
		});
		addButtonLfoSw.addListener(new ChangeListener() {
			public void changed(ChangeEvent ev, Actor ac) {
				FXNode n = new FXNode(Constants.LFO_INPUTS, Utils.randomPosition());
				n.setDelegate(new LFO(0.66f, Sinewave.class));
				SynAudioProcessor.getInstance().addNode(n);
			}
		});

		addButtonLfoSt.addListener(new ChangeListener() {
			public void changed(ChangeEvent ev, Actor ac) {
				FXNode n = new FXNode(Constants.LFO_INPUTS, Utils.randomPosition());
				n.setDelegate(new LFO(2f, Sawtooth.class));
				SynAudioProcessor.getInstance().addNode(n);
			}
		});

		addButtonDl.addListener(new ChangeListener() {
			public void changed(ChangeEvent ev, Actor ac) {
				FXNode n = new FXNode(Constants.TAPDELAY_INPUTS, Utils.randomPosition());
				n.setDelegate(new TapDelay(0.5f, 0.6f, 0.4f));
				SynAudioProcessor.getInstance().addNode(n);
			}
		});
		
		removeButton.addListener(new ChangeListener() {
			public void changed(ChangeEvent ev, Actor ac) {
				Collection<Node> nodes = SynAudioProcessor.getInstance().getNodes().values();
				if (nodes.size() > 0) {
					SynAudioProcessor.getInstance().removeLastNode();
				}
			}
		});
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
