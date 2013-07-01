package de.hsb.ms.syn.common.abs;

import java.io.Serializable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import de.hsb.ms.syn.common.util.Constants;
import de.hsb.ms.syn.common.vo.NetMessage;

/**
 * Interface representing a connection endpoint, either implemented
 * by the smart device or the PC end (and by different means depending
 * on the kind of connection that is attempted)
 * @author Marcel
 *
 */
public abstract class Connection implements Serializable {
	
	private static final long serialVersionUID = -5108615709170942504L;
	
	public static final String BLUETOOTH = "bluetooth_%s";
	public static final String SIMON = "simon_%s";
	
	protected String kind;
	
	protected Texture onTexture;
	protected Texture offTexture;
	protected Texture iconTexture;
	
	public void init() {
		String on	= String.format(kind, "on");
		String off	= String.format(kind, "off");
		String icon	= String.format(kind, "icon");
		
		onTexture	= new Texture(Gdx.files.internal(String.format(Constants.PATH_UI, on)));
		offTexture	= new Texture(Gdx.files.internal(String.format(Constants.PATH_UI, off)));
		iconTexture = new Texture(Gdx.files.internal(String.format(Constants.PATH_UI, icon)));
	}
	
	public Texture getIconTexture() {
		return iconTexture;
	}
	
	public Texture getOnTexture() {
		return onTexture;
	}
	
	public Texture getOffTexture() {
		return offTexture;
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
	 * Send a message to the other endpoint
	 * @param message
	 */
	public abstract void send(NetMessage message);
	
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
}
