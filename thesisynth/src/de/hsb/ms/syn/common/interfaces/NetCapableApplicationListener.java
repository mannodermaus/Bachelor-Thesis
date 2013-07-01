package de.hsb.ms.syn.common.interfaces;

import com.badlogic.gdx.ApplicationListener;

import de.hsb.ms.syn.common.abs.Connection;

/**
 * Extended LibGDX application listener interface
 * with net communication support
 * @author Marcel
 *
 */
public interface NetCapableApplicationListener extends ApplicationListener, NetMessageReceiver {

	/**
	 * Set the connection for this application listener
	 * @param c	Connection interface
	 */
	public void setConnection(Connection c);
}
