package de.hsb.ms.syn.common.audio;

import java.io.Serializable;

/**
 * Data structure for one property of a DraggableNode's AudioAlgorithm.
 * This is a basic structure holding three floating-points and a String name
 * that may be used to describe a UI Slider that may alter its value, for instance
 * @author Marcel
 *
 */
public class Property implements Serializable {
	
	private static final long serialVersionUID = -4510934854178102155L;
	
	/** ID constant of this property */
	private int id;
	
	/** Lower bound of the allowed scale for this property */
	private float lo;
	
	/** Upper bound of the allowed scale for this property */
	private float hi;
	
	/** Step size */
	private float step;
	
	/** Current value of this property */
	private float val;
	
	/** Name of this property */
	private String name;
	
	/** Optional extra object for this property */
	private Object extra;
	
	/** Is this property hidden or not? */
	private boolean hidden;
	
	/**
	 * Constructor
	 * @param name
	 * @param lo
	 * @param hi
	 * @param val
	 */
	public Property(int id, String name, float lo, float hi, float step, float val) {
		this.id = id;
		this.name = name;
		this.lo = lo;
		this.hi = hi;
		this.step = step;
		this.val = val;
		this.hidden = false;
	}
	
	/**
	 * Constructor copying each value from a given Property while overwriting the actual value
	 * @param copiedFrom
	 * @param newVal
	 */
	public Property(Property copiedFrom, float newVal) {
		this(copiedFrom.id(), copiedFrom.name(), copiedFrom.lo(),
			 copiedFrom.hi(), copiedFrom.step(), newVal);
		this.setExtra(copiedFrom.extra());
	}
	
	/**
	 * Get ID
	 * @return
	 */
	public int id() {
		return id;
	}
	
	/**
	 * Get lower bound
	 * @return
	 */
	public float lo() {
		return lo;
	}
	
	/**
	 * Get upper bound
	 * @return
	 */
	public float hi() {
		return hi;
	}
	
	/**
	 * Get step size
	 * @return
	 */
	public float step() {
		return step;
	}
	
	/**
	 * Get current value
	 * @return
	 */
	public float val() {
		return val;
	}
	
	/**
	 * Set additional object
	 * @param extra
	 */
	public void setExtra(Object extra) {
		this.extra = extra;
	}
	
	/**
	 * Get additional object
	 * @return
	 */
	public Object extra() {
		return this.extra;
	}
	
	/**
	 * Set a new value within the bounds of the property
	 * @param val
	 */
	public void setVal(float val) {
		this.val = Math.min(Math.max(lo, val), hi);
	}
	
	/**
	 * Get name
	 * @return
	 */
	public String name() {
		return name;
	}
	
	/**
	 * Hide this property, preventing it from being sent over network
	 */
	public void hide() {
		this.hidden = true;
	}
	
	/**
	 * Unhide this property, making it available for being sent over network
	 * @return
	 */
	public boolean isHidden() {
		return this.hidden;
	}
	
	@Override
	public String toString() {
		return String.format("{name=%s,lo=%.1f,hi=%.1f,val=%.1f}", name, lo, hi, val);
	}
}
