package de.hsb.ms.syn.common.ui;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

/**
 * Custom UI widget for the 2D Touch Matrix screen.
 * It provides functionality similar to the famous Korg KaossPad controllers.
 * Users may assign Node parameters to the two axes of the touch matrix and
 * manipulate these values using touch input.
 * 
 * @author Marcel
 *
 */
public class TouchMatrixPad extends Widget {

	private TextFieldStyle style;
	private List<TouchMatrixListener> listeners;
	private ShapeRenderer lineRenderer;
	
	private float touchX;
	private float touchY;
	private boolean touched;
	
	public TouchMatrixPad(Skin skin) {
		this.lineRenderer = new ShapeRenderer();
		this.listeners = new ArrayList<TouchMatrixListener>();
		this.setStyle(skin.get(TextFieldStyle.class));
		this.setWidth(300);
		this.setHeight(300);
		this.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				TouchMatrixPad.this.touched = true;
				this.updatePosition(x, y);
				return true;
			}
			@Override
			public void touchDragged(InputEvent event, float x, float y,
					int pointer) {
				this.updatePosition(x, y);
			}
			
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				TouchMatrixPad.this.touched = false;
				this.updatePosition(x, y);
			}
			
			private void updatePosition(float x, float y) {
				TouchMatrixPad.this.touchX = Math.max(Math.min(x, getWidth()), 0);
				TouchMatrixPad.this.touchY = Math.max(Math.min(y, getHeight()), 0);
				
				// Notify listeners of the change
				TouchMatrixEvent tme = new TouchMatrixEvent(touchX, 0, getWidth(), touchY, 0, getHeight());
				for (TouchMatrixListener tml : listeners) {
					tml.touchMatrixChanged(tme);
				}
			}
			
		});
	}
	
	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		Drawable background = style.background;
		
		Color color = getColor();
		float x = getX();
		float y = getY();
		float width = getWidth();
		float height = getHeight();

		batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
		if (background != null)
			background.draw(batch, x, y, width, height);
		if (touched) {
			batch.end();
			lineRenderer.begin(ShapeType.Line);
			lineRenderer.line(x, touchY + y, x + width, touchY + y);
			lineRenderer.line(touchX + x, y, touchX + x, y + height);
			lineRenderer.end();
			lineRenderer.begin(ShapeType.Box);
			lineRenderer.box(touchX + x - 10, touchY + y - 5, 0, 20, 10, 0);
			lineRenderer.end();
			batch.begin();
			style.font.draw(batch, "" + touchX + "," + touchY, x + 10, y + 50);
		}
	}
	
	public void setStyle(TextFieldStyle style) {
		this.style = style;
	}
	
	public void addTouchMatrixListener(TouchMatrixListener tml) {
		this.listeners.add(tml);
	}
	
	public void removeTouchMatrixListener(TouchMatrixListener tml) {
		if (this.listeners.contains(tml))
			this.listeners.remove(tml);
	}
	
	public static abstract class TouchMatrixListener {
		public abstract void touchMatrixChanged(TouchMatrixEvent tme);
	}
	
	public static class TouchMatrixEvent {
		// Absolute values of the touch matrix
		private float xval;
		private float yval;
		
		// Percentage values of the touch matrix
		private float xpercentage;
		private float ypercentage;
		
		public TouchMatrixEvent(float xval, float xmin, float xmax, float yval, float ymin, float ymax) {
			this.xval = xval;
			this.yval = yval;
			
			this.xpercentage = xval / (xmin + xmax);
			this.ypercentage = yval / (ymin + ymax);
		}
		
		public float getXval() {
			return this.xval;
		}
		
		public float getYval() {
			return this.yval;
		}
		
		public float getXpercentage() {
			return this.xpercentage;
		}
		
		public float getYpercentage() {
			return this.ypercentage;
		}
		
		
	}
}
