package de.hsb.ms.syn.common.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import de.hsb.ms.syn.common.abs.Connection;
import de.hsb.ms.syn.common.abs.DesktopConnection;

public class ConnectionStatusIcon {

	private int x;
	private int y;
	
	private BitmapFont font;
	private Connection connection;
	private Texture on;
	private Texture off;
	
	private ShapeRenderer shapeRenderer;
	private Color connectionColor;
	
	public ConnectionStatusIcon(Connection connection) {
		this.connection = connection;
		this.font = new BitmapFont();
		this.on = connection.getOnTexture();
		this.off = connection.getOffTexture();
		this.shapeRenderer = new ShapeRenderer();
		this.setPosition(0, 0);
	}
	
	public void setColor(Color color) {
		this.connectionColor = color;
	}
	
	public Color getColor() {
		return this.connectionColor;
	}
	
	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getWidth() {
		return off.getWidth();
	}
	
	public int getHeight() {
		return off.getHeight();
	}
	
	public void draw(SpriteBatch b) {
		// Draw a colored indicator if any color is set
		if (this.connectionColor != null) {
			shapeRenderer.begin(ShapeType.Line);
			shapeRenderer.setColor(Color.BLACK);
			shapeRenderer.circle(x + 30, y + 30, 24);
			shapeRenderer.end();
			
			shapeRenderer.begin(ShapeType.Filled);
			shapeRenderer.setColor(connectionColor);
			shapeRenderer.circle(x + 30, y + 30, 20);
			shapeRenderer.end();
		}
		b.begin();
		if (connection.isConnected())
			b.draw(on, x, y);
		else
			b.draw(off, x, y);
		if (connection instanceof DesktopConnection)
			font.draw(b, "" + ((DesktopConnection) connection).getConnectedCount(), x + 40, y + 25);
		b.end();
	}
}
