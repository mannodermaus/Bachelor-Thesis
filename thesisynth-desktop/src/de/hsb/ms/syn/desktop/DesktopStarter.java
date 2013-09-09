package de.hsb.ms.syn.desktop;

import javax.swing.JOptionPane;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import de.hsb.ms.syn.common.interfaces.Connection;
import de.hsb.ms.syn.common.interfaces.NetCapableApplicationListener;
import de.hsb.ms.syn.desktop.net.DesktopBluetoothConnection;

/**
 * Desktop starter class for Thesisynth.
 * Using the LWJGL backend of the framework, it initializes an instance of the Synthesizer ApplicationListener
 * inside of a LibGDX context.
 * @author Marcel
 *
 */
public class DesktopStarter {
    
	/**
	 * Desktop-sided entry point
	 * @param args
	 */
	public static void main(String[] args) {
		// Make a Configuration
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
		// Check if bluetooth is available on this host device. If not, show a dialog to the user first
		if (!connection.isAvailable())
			JOptionPane.showMessageDialog(null,
					connection.getDescription() + " is not available. Mobile clients won't be able to connect...",
					connection.getDescription() + " error", JOptionPane.ERROR_MESSAGE);
		// Init LibGDX application with given program
		new LwjglApplication(program, cfg);
	}
}
