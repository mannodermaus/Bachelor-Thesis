package de.hsb.ms.syn.common.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import de.hsb.ms.syn.common.abs.Connection;
import de.hsb.ms.syn.common.abs.DesktopConnection;

public class ConnectionStatusIcon {

	private int x;
	private int y;
	
	private BitmapFont font;
	private Connection connection;
	private Texture on;
	private Texture off;
	
	public ConnectionStatusIcon(Connection connection) {
		this.connection = connection;
		this.font = new BitmapFont();
		this.on = connection.getOnTexture();
		this.off = connection.getOffTexture();
		this.setPosition(0, 0);
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
