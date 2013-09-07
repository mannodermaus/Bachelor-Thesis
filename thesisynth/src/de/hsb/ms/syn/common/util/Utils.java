package de.hsb.ms.syn.common.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

import de.hsb.ms.syn.common.audio.Properties;
import de.hsb.ms.syn.common.vo.DraggableNode;
import de.hsb.ms.syn.common.vo.Node;

/**
 * Utility class providing several different services that are
 * useful in different aspects of the application
 * @author Marcel
 *
 */
public abstract class Utils {

	/**
	 * Compute a random position on the synthesizer's surface
	 * @return
	 */
	public static Vector2 randomPosition() {
		int w = Gdx.graphics.getWidth();
		int h = Gdx.graphics.getHeight();
		return new Vector2((int) (((Math.random() * w) - (w / 2)) * 0.75),
				           (int) (((Math.random() * h) - (h / 2)) * 0.75));
	}
	
	/**
	 * Compute a random frequency
	 * @return
	 */
	public static int randomFrequency() {
		return (int) (110 + 110 * (Math.random() * 6) / Math.ceil((Math.random() * 4)));
	}
	
	/**
	 * Retrieve a list of Class objects refering to each object in the given list,
	 * e.g. [2, "hello"] -> [Integer.class, String.class]
	 * @param list	List of Class objects, or null if the list is empty
	 * @return
	 */
	public static Class<?>[] getClassesFromListArguments(List<?> list) {
		
		// Class array
		Class<?>[] classes;
		
		if (list.size() > 0) {
			// If the list is not empty, fill the Class array with ... classes
			classes = new Class[list.size()];
			for (int i = 0; i < classes.length; i++)
				classes[i] = list.get(i).getClass();
		} else
			// If it is empty, set it to null
			classes = null;
		
		return classes;
	}
	
	/**
	 * Log a message using the application's Log Tag to the console/Logcat
	 * @param message
	 */
	public static void log(Object message) {
		Gdx.app.log(Constants.LOG_TAG, message.toString());
	}

	/**
	 * Converts a data point "val" in some scale [osl, osh]
	 * to a data point in some other scale [nsl, nsh]
	 * using a linear mathematical function f(x) = m * x + b.
	 * 
	 * @param val	Value to be converted from the old scale
	 * @param osl	Low boundary of the old scale
	 * @param osh	High boundary of the old scale
	 * @param nsl	Low boundary of the new scale
	 * @param nsh	High boundary of the new scale
	 * @return		Value in the new scale
	 */
	public static float getScaleConvertedValue(float val, float osl, float osh, float nsl, float nsh) {
		// m
		float delta = (nsh - nsl) / (osh - osl);
		// b
		float abs = nsl - (delta * osl);
		// f(val) = m * val + b
		return (delta * val) + abs;
	}
	
	/**
	 * Print the contents of the given array of float values
	 * @param array
	 */
	public static void printArray(float[] array) {
		String s = "printArray: [";
		for (int i = 0 ; i < array.length ; i++)
			s += array[i] + " ";
		s+= "]";
		Utils.log(s);
	}

	/**
	 * Create a map of NodeProperties objects out of a map of Node objects.
	 * This is used whenever the Desktop host needs to SENDNODES the current
	 * synthesizer state to a mobile device
	 * @param nodes
	 * @return
	 */
	public static HashMap<Integer, Properties> makeNodePropertyStructure(Map<Integer, Node> nodes) {
		HashMap<Integer, Properties> props = new HashMap<Integer, Properties>();
		for (Integer i : nodes.keySet()) {
			Node n = nodes.get(i);
			// Only include DraggableNode objects (exclude CenterNode)
			if (n instanceof DraggableNode) {
				props.put(i, ((DraggableNode) n).getAlgorithm().getProperties().copy());
			}
		}
		return props;
	}
	
	/**
	 * Compute Euclidian distance between two two-dimensional points
	 * (2D version of Vector3.dst(x1, y1, z1, x2, y2, z2))
	 * @param fromX
	 * @param fromY
	 * @param toX
	 * @param toY
	 * @return
	 */
	public static float distance2d(float fromX, float fromY, float toX, float toY) {
		float a = toX - fromX;
		float b = toY - fromY;
		return (float) Math.sqrt(a * a + b * b);
	}
}
