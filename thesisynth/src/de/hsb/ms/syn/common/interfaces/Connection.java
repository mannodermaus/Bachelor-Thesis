package de.hsb.ms.syn.common.interfaces;

import java.io.Serializable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import de.hsb.ms.syn.common.net.NetMessage;
import de.hsb.ms.syn.common.util.Constants;

/**
 * Facade representing a connection endpoint, either implemented
 * by the smart device or the PC end (and by different means depending
 * on the kind of connection that is attempted)
 * @author Marcel
 *
 */
public abstract class Connection implements Serializable {
	
	private static final long serialVersionUID = -5108615709170942504L;
	
	/** Bluetooth connection type */
	public static final String BLUETOOTH = "bluetooth_%s";
	/** SIMON connection type */
	public static final String SIMON = "simon_%s";
	
	/** The type of this connection */
	protected String kind;
	/** The identifier of this connection */
	protected int id;
	
	/** The texture of this connection when it is "connected" */
	protected Texture onTexture;
	/** The texture of this connection when it is "not connected" */
	protected Texture offTexture;
	/** The texture of this connection's icon (for UI elements) */
	protected Texture iconTexture;
	
	/**
	 * Initialization method
	 */
	public void init() {
		String on	= String.format(kind, "on");
		String off	= String.format(kind, "off");
		String icon	= String.format(kind, "icon");
		
		onTexture	= new Texture(Gdx.files.internal(String.format(Constants.PATH_UI, on)));
		offTexture	= new Texture(Gdx.files.internal(String.format(Constants.PATH_UI, off)));
		iconTexture = new Texture(Gdx.files.internal(String.format(Constants.PATH_UI, icon)));
	}
	
	/**
	 * Returns the icon texture
	 * @return
	 */
	public Texture getIconTexture() {
		return iconTexture;
	}
	
	/**
	 * Returns the on texture
	 * @return
	 */
	public Texture getOnTexture() {
		return onTexture;
	}
	
	/**
	 * Returns the off texture
	 * @return
	 */
	public Texture getOffTexture() {
		return offTexture;
	}
	
	/**
	 * Sets this connection's ID
	 * @param val
	 */
	public void setID(int val) {
		this.id = val;
	}
	
	/**
	 * Returns this connection's ID
	 * @return
	 */
	public int getID() {
		return id;
	}
	
	/**
	 * Check if the connection's infrastructure is available
	 * @return
	 */
	public abstract boolean isAvailable();
	
	/**
	 * Connect to other endpoint using this connection
	 */
	public abstract void connect();
	
	/**
	 * Check if the connection is currently established
	 * @return
	 */
	public abstract boolean isConnected();
	
	/**
	 * Receive a message from the other endpoint
	 * @param message
	 */
	public abstract void receive(NetMessage message);
	
	/**
	 * Close the connection
	 */
	public abstract void close();
	
	/**
	 * Get description of this connection's type
	 */
	public abstract String getDescription();
	
	/**
	 * Get name of the device
	 * @return
	 */
	public abstract String getDeviceName();
}
