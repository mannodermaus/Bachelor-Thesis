package de.hsb.ms.syn.common.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

/**
 * Custom dialog that displays the failure of a connection attempt
 * @author Marcel
 *
 */
public class ConnectionFailedDialog extends Dialog {
	
	/**
	 * Constructor
	 * @param skin
	 * @param parent
	 */
	public ConnectionFailedDialog(Skin skin, final Table parent) {
		super("Error", skin);
		
		this.row().fill();
		Label label = new Label("Connection attempt failed.", skin);
		Button button = new TextButton("OK", skin);
		
		this.add(label).row();
		this.add(button).row();
		
		button.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				parent.removeActor(ConnectionFailedDialog.this);
			}
		});
	}

}
