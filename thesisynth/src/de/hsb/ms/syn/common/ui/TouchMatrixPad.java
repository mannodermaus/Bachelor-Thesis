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

import de.hsb.ms.syn.common.util.Constants;

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
	
	/** Style of the touch matrix (will eventually be changed to its own style class) */
	private TextFieldStyle style;
	/** Shape renderer for the touch matrix' grid */
	private ShapeRenderer lineRenderer;
	
	/** Listener list for TouchMatrixEvents */
	private List<TouchMatrixListener> listeners;
	
	/** Coordinates of the last registered touch */
	private float touchX, touchY;
	
	/** Percentage values of the last registered touch (out of [0.0f, 1.0f]) */
	private float percX, percY;
	
	/** True if currently touched, false if not */
	private boolean touched;
	
	/** Axes labels */
	private String xAxisLabel, yAxisLabel;
	
	/** Transform matrix used for drawing vertical or horizontal text */
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
	
	/**
	 * Sets the current touch point of this TouchMatrixPad by percentage ([0.0f, 1.0f])
	 * @param x
	 * @param y
	 */
	public void setTouchPointByPercentage(float x, float y) {
		this.setTouchPointByPixels(x * getWidth(), y * getHeight());
	}
	
	/**
	 * Sets the current touch point of this TouchMatrixPad by absolute pixels
	 * @param px
	 * @param py
	 */
	public void setTouchPointByPixels(float px, float py) {
		touchX = px;
		touchY = py;
	}
	
	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		Drawable background = style.background;
		
		// TODO Select colors from TouchMatrixPadStyle
		// Color color = getColor();
		Color color = Constants.COLOR_GRID1;
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
			lineRenderer.setColor(Constants.COLOR_GRADIENT1);
			lineRenderer.begin(ShapeType.Line);
			lineRenderer.line(x + 20, y + 15, x + width - 120, y + 15, Constants.COLOR_GRADIENT1, Constants.COLOR_GRADIENT2);
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
			lineRenderer.setColor(Constants.COLOR_GRADIENT1);
			lineRenderer.begin(ShapeType.Line);
			lineRenderer.line(x + width - 13, y + 100, x + width - 13, y + height - 20, Constants.COLOR_GRADIENT1, Constants.COLOR_GRADIENT2);
			lineRenderer.end();
			batch.begin();
		}

		// Reset transform matrix
		matrix.idt();
		batch.setTransformMatrix(matrix);
		
		// Draw additional stuff depending on whether the pad is being touched or not
		if (touched) {
			batch.end();
			lineRenderer.setColor(Constants.COLOR_GRID1);
			lineRenderer.begin(ShapeType.Line);
			
			// Horizontal lines
			lineRenderer.line(x, (touchY + y),
							 (x + touchX), (touchY + y),
							 Constants.COLOR_GRID1, Constants.COLOR_GRID2);
			lineRenderer.line((x + touchX), (touchY + y),
							 (x + width), (touchY + y),
							 Constants.COLOR_GRID2, Constants.COLOR_GRID1);
			
			// Vertical lines
			lineRenderer.line((touchX + x), y,
							 (touchX + x), (touchY + y),
							 Constants.COLOR_GRID1, Constants.COLOR_GRID2);
			lineRenderer.line((touchX + x), (y + touchY),
							 (touchX + x), (y + height),
							 Constants.COLOR_GRID2, Constants.COLOR_GRID1);
			
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
			lineRenderer.setColor(Constants.COLOR_HIGHLIGHT);
			lineRenderer.begin(ShapeType.Filled);
			
			// Point indicator of touch
			lineRenderer.circle(touchX + x, touchY + y, 5f);
			
			lineRenderer.end();
			batch.begin();
		}
	}
	
	/**
	 * Sets the style of this TouchMatrixPad
	 * @param style
	 */
	public void setStyle(TextFieldStyle style) {
		this.style = style;
	}

	/**
	 * Sets the labels for the pad's axes to be displayed on the bottom right
	 * @param xAxisName
	 * @param yAxisName
	 */
	public void setAxisLabels(String xAxisName, String yAxisName) {
		this.xAxisLabel = xAxisName;
		this.yAxisLabel = yAxisName;
	}
	
	/**
	 * Adds a listener for TouchMatrixEvents
	 * @param tml
	 */
	public void addTouchMatrixListener(TouchMatrixListener tml) {
		this.listeners.add(tml);
	}
	
	/**
	 * Removes a listener for TouchMatrixEvents
	 * @param tml
	 */
	public void removeTouchMatrixListener(TouchMatrixListener tml) {
		if (this.listeners.contains(tml))
			this.listeners.remove(tml);
	}
	
	/**
	 * Listener class for TouchMatrixEvents. Objects can register
	 * themselves to be notified whenever a change occurs in the
	 * TouchMatrixPad they are listening to.
	 * @author Marcel
	 *
	 */
	public static abstract class TouchMatrixListener implements EventListener {
		/**
		 * Callback method for when a change has occurred
		 * @param tme
		 * @param actor
		 */
		public abstract void touchMatrixChanged(TouchMatrixEvent tme, Actor actor);
		
		/**
		 * Handle implementation. If the event is a TouchMatrixEvent, let the listener handle it
		 * and mark it as handled. If it isn't, return false
		 */
		public boolean handle(Event event) {
			if (event instanceof TouchMatrixEvent) {
				this.touchMatrixChanged((TouchMatrixEvent) event, event.getListenerActor());
				return true;
			}
			return false;
		}
	}
	
	/**
	 * Event class for TouchMatrixEvents.
	 * Instances of this class are created when a change in the touch position
	 * of a TouchMatrixPad occurs.
	 * @author Marcel
	 *
	 */
	public static class TouchMatrixEvent extends Event {
		/**  Absolute values of the touch matrix */
		private float xval, yval;
		
		/** Percentage values of the touch matrix */
		private float xpercentage, ypercentage;
		
		/**
		 * Constructor
		 * @param xval
		 * @param yval
		 * @param xperc
		 * @param yperc
		 */
		public TouchMatrixEvent(float xval, float yval, float xperc, float yperc) {
			super();
			
			this.xval = xval;
			this.yval = yval;
			
			this.xpercentage = xperc;
			this.ypercentage = yperc;
		}
		
		/**
		 * Returns the x value
		 * @return
		 */
		public float getXval() {
			return this.xval;
		}
		
		/**
		 * Returns the y value
		 * @return
		 */
		public float getYval() {
			return this.yval;
		}
		
		/**
		 * Returns the x percentage ([0.0f, 1.0f])
		 * @return
		 */
		public float getXpercentage() {
			return this.xpercentage;
		}

		/**
		 * Returns the y percentage ([0.0f, 1.0f])
		 * @return
		 */
		public float getYpercentage() {
			return this.ypercentage;
		}
		
		@Override
		public String toString() {
			return "TouchMatrixEvent (" + getXpercentage() + "," + getYpercentage() + ")";
		}
	}
}
