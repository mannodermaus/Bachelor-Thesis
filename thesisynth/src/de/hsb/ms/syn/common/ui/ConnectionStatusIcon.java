package de.hsb.ms.syn.common.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import de.hsb.ms.syn.common.interfaces.Connection;
import de.hsb.ms.syn.common.util.Constants;

public class ConnectionStatusIcon {

	private int x;
	private int y;
	
	private Connection connection;
	private static Texture bluetoothOn;
	private static Texture bluetoothOff;
	
	public ConnectionStatusIcon(Connection connection) {
		this.connection = connection;
		
		this.setPosition(0, 0);
		
		if (bluetoothOff == null) {
			bluetoothOff = new Texture(Gdx.files.internal(String.format(Constants.PATH_UI, "bluetooth_off")));
			bluetoothOn	 = new Texture(Gdx.files.internal(String.format(Constants.PATH_UI, "bluetooth_on")));
		}
	}
	
	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}
	public int getWidth() {
		return bluetoothOff.getWidth();
	}
	
	public int getHeight() {
		return bluetoothOff.getHeight();
	}
	
	public void draw(SpriteBatch b) {
		b.begin();
		if (connection.isConnected())
			b.draw(bluetoothOn, x, y);
		else
			b.draw(bluetoothOff, x, y);
		b.end();
	}
}
