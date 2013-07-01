package de.hsb.ms.syn.mobile.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

public class ControllerMenu extends Stage {

	private Table contents;

	public ControllerMenu(TextButton[] buttons) {
		contents = new Table();
		contents.align(Align.bottom | Align.left);

		// Fill the contents with the buttons
		int width = Gdx.graphics.getWidth() / buttons.length;
		for (TextButton button : buttons)
			contents.add(button).minHeight(50).minWidth(width);
		this.addActor(contents);
	}

}
