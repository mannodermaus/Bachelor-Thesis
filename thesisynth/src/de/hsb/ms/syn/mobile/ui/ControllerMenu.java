package de.hsb.ms.syn.mobile.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

public class ControllerMenu extends Stage {

	private Table contents;

	/**
	 * Constructor
	 * @param buttons			Array of Buttons to add to the menu
	 * @param connectButton		Connect button (will be added on the far right)
	 */
	public ControllerMenu(Button[] buttons, Button connectButton) {
		contents = new Table();
		contents.align(Align.bottom | Align.left);

		// Fill the contents with the buttons
		int width = (Gdx.graphics.getWidth() - 50) / buttons.length;
		for (Button button : buttons)
			contents.add(button).minHeight(50).minWidth(width);
		contents.add(connectButton).minHeight(50).minWidth(50);
		this.addActor(contents);
	}

}
