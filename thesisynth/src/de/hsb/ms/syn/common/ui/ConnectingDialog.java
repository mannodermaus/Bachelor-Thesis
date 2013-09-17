package de.hsb.ms.syn.common.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * Custom dialog that displays a connection attempt going on...
 * @author Marcel
 *
 */
public class ConnectingDialog extends Dialog {
	
	/**
	 * Constructor
	 * @param skin
	 */
	public ConnectingDialog(Skin skin) {
		super("Connecting...", skin);
		this.row().fill();
		Label label = new Label("Attempting to connect to host...", skin);
		this.add(label);
	}
}
