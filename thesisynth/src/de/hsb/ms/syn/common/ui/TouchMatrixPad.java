package de.hsb.ms.syn.common.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
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
	private InputListener inputListener;
	private ShapeRenderer lineRenderer;
	
	private float touchX;
	private float touchY;
	private boolean touched;
	
	public TouchMatrixPad(Skin skin) {
		this.lineRenderer = new ShapeRenderer();
		this.setStyle(skin.get(TextFieldStyle.class));
		this.setWidth(300);
		this.setHeight(300);
		this.addListener(inputListener = new ClickListener() {
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
}
