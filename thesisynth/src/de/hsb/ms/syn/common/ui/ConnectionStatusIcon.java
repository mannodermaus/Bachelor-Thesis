package de.hsb.ms.syn.common.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import de.hsb.ms.syn.common.interfaces.Connection;
import de.hsb.ms.syn.common.interfaces.DesktopConnection;

/**
 * UI element indicator for the status of a device's connection
 * @author Marcel
 *
 */
public class ConnectionStatusIcon {

	/** Positional values */
	private int x, y;
	
	/** Font to use */
	private BitmapFont font;
	/** Connection to represent */
	private Connection connection;
	/** Texture to use when the connection is available and active */
	private Texture on;
	/** Texture to use when the connection is not available or inactive */
	private Texture off;
	/** ShapeRenderer to use for color rendering */
	private ShapeRenderer shapeRenderer;
	/** Color to render the device's ID color in */
	private Color connectionColor;
	
	/**
	 * Constructor
	 * @param connection
	 */
	public ConnectionStatusIcon(Connection connection) {
		this.setConnection(connection);
		this.font = new BitmapFont(Gdx.files.internal("data/robotocondensed.fnt"), false);
		this.on = connection.getOnTexture();
		this.off = connection.getOffTexture();
		this.shapeRenderer = new ShapeRenderer();
		this.setPosition(0, 0);
	}
	
	/**
	 * Set the connection of this icon
	 * @param c
	 */
	public void setConnection(Connection c) {
		this.connection = c;
	}
	
	/**
	 * Set the color of this icon
	 * @param color
	 */
	public void setColor(Color color) {
		this.connectionColor = color;
	}
	
	/**
	 * Returns the color of this icon
	 * @return
	 */
	public Color getColor() {
		return this.connectionColor;
	}
	
	/**
	 * Set the position of this icon
	 * @param x
	 * @param y
	 */
	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Returns the width of this icon
	 * @return
	 */
	public int getWidth() {
		return off.getWidth();
	}
	
	/**
	 * Returns the height of this icon
	 * @return
	 */
	public int getHeight() {
		return off.getHeight();
	}
	
	/**
	 * Draw method
	 * @param b
	 */
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
		// Draw the on or off texture depending on the connection status
		b.begin();
		if (connection.isConnected())
			b.draw(on, x, y);
		else
			b.draw(off, x, y);
		// For the Desktop host, draw the number of connected devices as well
		if (connection instanceof DesktopConnection)
			font.draw(b, "" + ((DesktopConnection) connection).getConnectedCount(), x + 40, y + 25);
		b.end();
	}
}
