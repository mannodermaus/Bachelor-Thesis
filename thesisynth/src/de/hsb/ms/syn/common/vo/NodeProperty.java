package de.hsb.ms.syn.common.vo;

import java.io.Serializable;

/**
 * Data structure for one property of a DraggableNode's Delegate.
 * This is a basic structure holding three floating-points and a String name
 * that may be used to describe a UI Slider that may alter its value, for instance
 * @author Marcel
 *
 */
public class NodeProperty implements Serializable {
	
	private static final long serialVersionUID = -4510934854178102155L;
	
	// ID constant of this property
	private int id;
	
	// Lower bound of the allowed scale for this property
	private float lo;
	
	// Upper bound of the allowed scale for this property
	private float hi;
	
	// Step size
	private float step;
	
	// Current value of this property
	private float val;
	
	// Name of this property
	private String name;
	
	/**
	 * Constructor
	 * @param name
	 * @param lo
	 * @param hi
	 * @param val
	 */
	public NodeProperty(int id, String name, float lo, float hi, float step, float val) {
		this.id = id;
		this.name = name;
		this.lo = lo;
		this.hi = hi;
		this.step = step;
		this.val = val;
	}
	
	/**
	 * Constructor copying each value from a given NodeProperty while overwriting the actual value
	 * @param copiedFrom
	 * @param newVal
	 */
	public NodeProperty(NodeProperty copiedFrom, float newVal) {
		this(copiedFrom.id(), copiedFrom.name(), copiedFrom.lo(),
			 copiedFrom.hi(), copiedFrom.step(), newVal);
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
	 * toString override
	 */
	public String toString() {
		return String.format("{name=%s,lo=%.1f,hi=%.1f,val=%.1f}", name, lo, hi, val);
	}
	
}
