package de.hsb.ms.syn.common.ui;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
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
	
	// Style of the touch matrix (will eventually be changed to its own style class)
	private TextFieldStyle style;
	// Shape renderer for the touch matrix' grid
	private ShapeRenderer lineRenderer;
	
	// Listener list for TouchMatrixEvents
	private List<TouchMatrixListener> listeners;
	
	// Coordinates of the last registered touch
	private float touchX;
	private float touchY;
	
	private float percX;
	private float percY;
	
	// True if touched, false if not
	private boolean touched;
	
	private Color colorHighlight = new Color(0.6f, 0.7f, 0.3f, 1.0f);
	private Color colorGrid1 = new Color(0.8f, 0.7f, 0.85f, 1.0f);
	private Color colorGrid2 = new Color(0.5f, 0.41f, 0.975f, 1.0f);
	private Color colorGrad1 = new Color(0.23f, 0.31f, 0.22f, 1.0f);
	private Color colorGrad2 = new Color(0.85f, 0.1f, 0.56f, 1.0f);
	private String xAxisLabel;
	private String yAxisLabel;
	
	private Matrix4 matrix;
	
	/**
	 * Constructor
	 * @param skin
	 */
	public TouchMatrixPad(Skin skin) {
		this.lineRenderer = new ShapeRenderer();
		this.listeners = new ArrayList<TouchMatrixListener>();
		this.setStyle(skin.get(TextFieldStyle.class));
		this.setWidth(300);
		this.setHeight(300);
		this.matrix = new Matrix4();
		this.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				touched = true;
				this.updatePosition(x, y);
				return true;
			}
			@Override
			public void touchDragged(InputEvent event, float x, float y, int pointer) {
				this.updatePosition(x, y);
			}
			
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				touched = false;
			}
			
			private void updatePosition(float x, float y) {
				
				touchX = Math.max(Math.min(x, getWidth()), 0);
				touchY = Math.max(Math.min(y, getHeight()), 0);
				
				percX = touchX / getWidth();
				percY = touchY / getHeight();
				
				// Notify listeners of the change
				TouchMatrixEvent tme = new TouchMatrixEvent(touchX, touchY, percX, percY);
				for (TouchMatrixListener tml : listeners) {
					tml.touchMatrixChanged(tme, TouchMatrixPad.this);
				}
			}
			
		});
	}
	
	public void setTouchPointByPercentage(float x, float y) {
		this.setTouchPointByPixels(x * getWidth(), y * getHeight());
	}
	
	public void setTouchPointByPixels(float px, float py) {
		touchX = px;
		touchY = py;
	}
	
	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		Drawable background = style.background;
		
		// TODO Select colors from TouchMatrixPadStyle
		// Color color = getColor();
		Color color = colorGrid1;
		float x = getX();
		float y = getY();
		float width = getWidth();
		float height = getHeight();
		
		// Reset transform matrix
		matrix.idt();

		// Draw background
		batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
		if (background != null)
			background.draw(batch, x, y, width, height);
		
		// Draw axis labels
		if (xAxisLabel != null) {
			// Text
			style.font.draw(batch, xAxisLabel, x + width - 105, y + 22);
			batch.end();
			// Horizontal decoration line
			lineRenderer.setColor(colorGrad1);
			lineRenderer.begin(ShapeType.Line);
			lineRenderer.line(x + 20, y + 15, x + width - 120, y + 15, colorGrad1, colorGrad2);
			lineRenderer.end();
			batch.begin();
		}
		if (yAxisLabel != null) {
			// Translate transform matrix to draw vertical text
			matrix.translate(x + width - 25, y + 30, 0);
			matrix.rotate(0, 0, 1, 90);
			matrix.translate(-x, -y, 0);
			batch.setTransformMatrix(matrix);
			// Text
			style.font.draw(batch, yAxisLabel, x, y);
			batch.end();
			// Vertical decoration line
			lineRenderer.setColor(colorGrad1);
			lineRenderer.begin(ShapeType.Line);
			lineRenderer.line(x + width - 13, y + 100, x + width - 13, y + height - 20, colorGrad1, colorGrad2);
			lineRenderer.end();
			batch.begin();
		}

		// Reset transform matrix
		matrix.idt();
		batch.setTransformMatrix(matrix);
		
		// Draw additional stuff depending on whether the pad is being touched or not
		if (touched) {
			batch.end();
			lineRenderer.setColor(colorGrid1);
			lineRenderer.begin(ShapeType.Line);
			
			// Horizontal lines
			lineRenderer.line(x, (touchY + y),
							 (x + touchX), (touchY + y),
							 colorGrid1, colorGrid2);
			lineRenderer.line((x + touchX), (touchY + y),
							 (x + width), (touchY + y),
							 colorGrid2, colorGrid1);
			
			// Vertical lines
			lineRenderer.line((touchX + x), y,
							 (touchX + x), (touchY + y),
							 colorGrid1, colorGrid2);
			lineRenderer.line((touchX + x), (y + touchY),
							 (touchX + x), (y + height),
							 colorGrid2, colorGrid1);
			
			lineRenderer.end();
			lineRenderer.begin(ShapeType.Line);
			
			// Box around touch position
			lineRenderer.box(touchX + x - 10, touchY + y - 5, 0, 20, 10, 0);
			
			lineRenderer.end();
			batch.begin();
			
			// Touch position text
			style.font.draw(batch, String.format("[%.2f, %.2f]", percX, percY),
							touchX + x + 7, touchY + y + 25);
		} else {
			batch.end();
			lineRenderer.setColor(colorHighlight);
			lineRenderer.begin(ShapeType.Filled);
			
			// Point indicator of touch
			lineRenderer.circle(touchX + x, touchY + y, 5f);
			
			lineRenderer.end();
			batch.begin();
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
	
	public static abstract class TouchMatrixListener implements EventListener {
		public abstract void touchMatrixChanged(TouchMatrixEvent tme, Actor actor);
		
		public boolean handle(Event event) {
			if (event instanceof TouchMatrixEvent) {
				this.touchMatrixChanged((TouchMatrixEvent) event, event.getListenerActor());
				return true;
			}
			return false;
		}
	}
	
	public static class TouchMatrixEvent extends Event {
		// Absolute values of the touch matrix
		private float xval;
		private float yval;
		
		// Percentage values of the touch matrix
		private float xpercentage;
		private float ypercentage;
		
		public TouchMatrixEvent(float xval, float yval, float xperc, float yperc) {
			super();
			
			this.xval = xval;
			this.yval = yval;
			
			this.xpercentage = xperc;
			this.ypercentage = yperc;
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
		
		@Override
		public String toString() {
			return "TouchMatrixEvent (" + getXpercentage() + "," + getYpercentage() + ")";
		}
	}

	public void setAxisLabels(String xAxisName, String yAxisName) {
		this.xAxisLabel = xAxisName;
		this.yAxisLabel = yAxisName;
	}
}
