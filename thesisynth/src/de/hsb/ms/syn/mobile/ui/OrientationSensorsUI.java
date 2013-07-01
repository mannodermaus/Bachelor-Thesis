package de.hsb.ms.syn.mobile.ui;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

import de.hsb.ms.syn.common.abs.ControllerUI;
import de.hsb.ms.syn.common.util.Utils;
import de.hsb.ms.syn.common.vo.NetMessage;

/**
 * Orientation Sensors UI
 * 
 * Test for smartphone sensor data processing.
 * 
 * @author Marcel
 *
 */
public class OrientationSensorsUI extends ControllerUI {

	private BitmapFont font;
	private NumberFormat format;
	
	@Override
	public void init() {
		super.init();
		this.processor = new OrientationSensorsController();
		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setDecimalSeparator('.');
		format = new DecimalFormat("#.##", symbols);
		format.setGroupingUsed(true);
		
		Table wrapper = new Table();
		wrapper.setFillParent(true);
		wrapper.pad(50);
		wrapper.align(Align.top | Align.left);
		
		stage.addActor(wrapper);
		
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
		
		// Convert accelerometer values from their usual scale to an RGB scale [0,1]
		float cx = Float.parseFloat(format.format(Utils.getScaleConvertedValue(x, -10, 10, 0, 1)));
		float cy = Float.parseFloat(format.format(Utils.getScaleConvertedValue(y, -10, 10, 0, 1)));
		float cz = Float.parseFloat(format.format(Utils.getScaleConvertedValue(z, -10, 10, 0, 1)));
		
		// Clear color
		Gdx.gl.glClearColor(cx, cy, cz, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		// Draw sensor data
		SpriteBatch batch = stage.getSpriteBatch();
		batch.begin();
		font.draw(batch, String.format("[x] %f (PITCH)"  , p), 50, 50);
		font.draw(batch, String.format("[y] %f (ROLL)"   , r), 50, 75);
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
	public void handle(NetMessage message) {
		
	}
	
	private class OrientationSensorsController extends ControllerProcessor {

		@Override
		public void process(NetMessage m) {
			
		}
		
	}

}
