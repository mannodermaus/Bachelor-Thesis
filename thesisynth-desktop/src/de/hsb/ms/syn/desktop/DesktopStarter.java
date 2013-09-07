package de.hsb.ms.syn.desktop;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import de.hsb.ms.syn.common.interfaces.Connection;
import de.hsb.ms.syn.common.interfaces.NetCapableApplicationListener;
import de.hsb.ms.syn.desktop.net.DesktopBluetoothConnection;

public class DesktopStarter {
    
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Thesis Synthesizer";
		cfg.useGL20 = true;
		cfg.width = 800;
		cfg.height = 600;
		cfg.addIcon("data/desktop_icon.png", FileType.Internal);
		
		// Initialize LibGDX program (special NetCapableListener class!)
		NetCapableApplicationListener program = new Synthesizer();
		// Initialize connection and feed in the LibGDX program (NetCapableListener is also a NetMessageReceiver)
		// Connection connection = new DesktopSimonConnection(program);
		Connection connection = new DesktopBluetoothConnection(program);
		// Provide the LibGDX with the obtained connection (may invoke connect() at will)
		program.setConnection(connection);
		// Init LibGDX application with given program
		new LwjglApplication(program, cfg);
	}
}
