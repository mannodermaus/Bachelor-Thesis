package de.hsb.ms.syn.desktop.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import de.hsb.ms.syn.desktop.Synthesizer;
import de.hsb.ms.syn.desktop.SynthesizerRenderer;

public class ConnectionOpenedWindow extends Window {
	private Stage ui;

	private boolean opened;

	public ConnectionOpenedWindow(Stage ui, Skin skin) {
		super(Synthesizer.connection.getDescription() + " connection", skin);
		this.ui = ui;

		this.build(skin);
	}

	private void build(Skin skin) {
		int w = Gdx.graphics.getWidth();
		int h = Gdx.graphics.getHeight();
		
		setSize(256, 128);
		setPosition(w/2 - getWidth()/2, h/2 - getHeight()/2);
		
		Label label = new Label("Connect smartphone now...", skin);
		TextButton button = new TextButton("Abort", skin);
		
		this.add(label);
		this.row();
		this.add(button).bottom();
		
		button.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Synthesizer.connection.close();
				SynthesizerRenderer.getInstance().closeConnectionWindow();
			}
		});
		
		setModal(true);
	}

	public void open() {
		this.opened = true;
		ui.addActor(this);
	}

	public void close() {
		if (this.opened)
			this.remove();
		this.opened = false;
	}

}
