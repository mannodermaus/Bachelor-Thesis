package de.hsb.ms.syn.mobile.ui;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

import de.hsb.ms.syn.common.util.Utils;
import de.hsb.ms.syn.common.vo.NetMessage;
import de.hsb.ms.syn.mobile.abs.ControllerUI;

/**
 * Orientation Sensors UI
 * 
 * Test for smartphone sensor data processing.
 * 
 * @author Marcel
 * 
 */
public class Sensor3DMatrixUI extends ControllerUI implements GestureListener {

	private BitmapFont font;
	private NumberFormat format;

	@Override
	public void init() {
		super.init();
		this.processor = new OrientationSensorsController();

		// Add a gesture detector to the input handler
		this.addProcessor(new GestureDetector(this));

		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setDecimalSeparator('.');
		format = new DecimalFormat("#.##", symbols);
		format.setGroupingUsed(true);

		Table wrapper = new Table();
		wrapper.setFillParent(true);
		wrapper.pad(50);
		wrapper.align(Align.top | Align.left);

		contents.addActor(wrapper);

		font = new BitmapFont();
	}

	@Override
	public void render() {
		// Get accelerometer values
		float x = Gdx.input.getAccelerometerX();
		float y = Gdx.input.getAccelerometerY();
		float z = Gdx.input.getAccelerometerZ();

		// Get rotational values
		float p = Gdx.input.getPitch();
		float r = Gdx.input.getRoll();
		float a = Gdx.input.getAzimuth();

		// Convert accelerometer values from their usual scale to an RGB scale
		// [0,1]
		float cx = Float.parseFloat(format.format(Utils.getScaleConvertedValue(
				x, -10, 10, 0, 1)));
		float cy = Float.parseFloat(format.format(Utils.getScaleConvertedValue(
				y, -10, 10, 0, 1)));
		float cz = Float.parseFloat(format.format(Utils.getScaleConvertedValue(
				z, -10, 10, 0, 1)));

		// Clear color
		Gdx.gl.glClearColor(cx, cy, cz, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		// Draw sensor data
		SpriteBatch batch = getSpriteBatch();
		batch.begin();
		font.draw(batch, String.format("[x] %f (PITCH)", p), 50, 50);
		font.draw(batch, String.format("[y] %f (ROLL)", r), 50, 75);
		font.draw(batch, String.format("[z] %f (AZIMUTH)", a), 50, 100);

		font.draw(batch, String.format("[Accelerometer x] %f", x), 50, 150);
		font.draw(batch, String.format("[Accelerometer y] %f", y), 50, 175);
		font.draw(batch, String.format("[Accelerometer z] %f", z), 50, 200);

		font.draw(batch, String.format("[Converted x] %.2f", cx), 50, 250);
		font.draw(batch, String.format("[Converted y] %.2f", cy), 50, 275);
		font.draw(batch, String.format("[Converted z] %.2f", cz), 50, 300);
		batch.end();
	}

	@Override
	public void dispose() {
		super.dispose();
		font.dispose();
	}

	@Override
	public void handle(NetMessage message) {

	}

	@Override
	public boolean fling(float velocityX, float velocityY, int button) {
		Utils.log(String.format("Fling (%.2f,%.2f) (BUTTON=%d)", velocityX,
				velocityY, button));
		if (velocityX > 750) {
			Utils.log("Yup, that's the right stuff.");
			return true;
		}
		return false;
	}

	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
		// camera.position.add(-deltaX, deltaY, 0);
		return true;
	}

	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		return false;
	}

	@Override
	public boolean tap(float x, float y, int count, int button) {
		return false;
	}

	@Override
	public boolean longPress(float x, float y) {
		return false;
	}

	@Override
	public boolean zoom(float initialDistance, float distance) {
		return false;
	}

	@Override
	public boolean pinch(Vector2 ip1, Vector2 ip2, Vector2 p1, Vector2 p2) {
		return false;
	}

	private class OrientationSensorsController extends ControllerProcessor {

		@Override
		public void process(NetMessage m) {

		}

	}
}
