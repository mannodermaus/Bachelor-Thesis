package de.hsb.ms.syn.common.audio;

import java.io.Serializable;

/**
 * This class represents the musical scale corresponding to a GenNode.
 * When changing the pitch of the waveform generated by the GenNode (by a PropertySlider, for instance),
 * this class decides the new frequency according to the Mode that is assigned to the scale.
 * @author Marcel
 *
 */
public class Scale implements Serializable {
	
	private static final long serialVersionUID = 4634708431836211171L;

	/**
	 * Nested class for the Mode of the scale.
	 * @author Marcel
	 *
	 */
	private static class Mode implements Serializable {
		
		private static final long serialVersionUID = -7620524214817940560L;
		
		/** Allowed intervals for this mode */
		private int[] intervals;
		
		/** Name of this mode */
		private String name;
		
		/**
		 * Constructor
		 * @param intervals
		 */
		private Mode(String name, int[] intervals) {
			this.intervals = intervals;
			this.name = name;
		}
		
		/**
		 * Return the interval corresponding to the given place (wraps around)
		 * @param value
		 * @return
		 */
		private int get(int value) {
			return this.intervals[value % length()];
		}
		
		/**
		 * Get the name of this mode
		 * @return
		 */
		private String name() {
			return name;
		}
		
		/**
		 * Return the number of allowed intervals
		 * @return
		 */
		private int length() {
			return this.intervals.length;
		}
	}
	
	/* Base constants */
	
	public static final int 	BASE_C	= 0;	// C
	public static final int 	BASE_CS	= 1;	// C# / Db
	public static final int 	BASE_D	= 2;	// D
	public static final int 	BASE_DS	= 3;	// D# / Eb
	public static final int 	BASE_E	= 4;	// E
	public static final int 	BASE_F	= 5;	// F
	public static final int 	BASE_FS	= 6;	// F# / Gb
	public static final int 	BASE_G	= 7;	// G
	public static final int 	BASE_GS	= 8;	// G# / Ab
	public static final int 	BASE_A	= 9;	// A
	public static final int 	BASE_AS	= 10;	// A# / Bb
	public static final int		BASEB	= 11;	// B
	
	/** String names for the bases */
	private static final String[] baseStrings = new String[] {
								"C", "C#", "D", "D#", "E", "F",
								"F#","G", "G#", "A", "A#", "B"
	};

	/* Mode constants */
	
	/** Major mode (whole octave) */
	public static final Mode	MODE_MAJ_OCTAVE = new Mode("maj",   new int[] {0, 2, 4, 5, 7, 9, 11});
	/** Minor mode (whole octave) */
	public static final Mode	MODE_MIN_OCTAVE = new Mode("min",   new int[] {0, 2, 3, 5, 7, 9, 10});
	/** Major mode (pentatonic scale) */
	public static final Mode	MODE_MAJ_PENTA	= new Mode("majpt", new int[] {0, 2, 4, 7, 9});
	/** Minor mode (pentatonic scale) */
	public static final Mode	MODE_MIN_PENTA	= new Mode("minpt", new int[] {0, 3, 5, 7, 10});
	/** Chromatic mode */
	public static final Mode	MODE_CHROMATIC	= new Mode("chrom", new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11});
	
	/**
	 * Frequency data from C2 to B5 (spanning almost four octaves, allowing
	 * for GenNodes to switch within three full octaves)
	 */
	private static final float[] FREQUENCIES = new float[] {
		65.4f,  69.3f,  73.4f,  77.8f,  82.4f,  87.3f,  92.5f,  98.0f,  103.8f, 110.0f, 116.5f, 123.5f,	// C2 - B2
		130.8f, 138.6f, 146.8f, 155.6f, 164.8f, 174.6f, 185.0f, 196.0f, 207.7f, 220.0f, 233.1f, 246.9f, // C3 - B3
		261.6f, 277.2f, 293.7f, 311.1f, 329.6f, 349.2f, 370.0f, 392.0f, 415.3f, 440.0f, 466.2f, 493.9f, // C4 - B4
		523.3f, 554.4f, 587.3f, 622.3f, 659.3f, 698.5f, 740.0f, 784.0f, 830.6f, 880.0f, 932.3f, 987.8f, // C5 - B5
	};
	
	/** Lowest possible octave of a scale */
	private static final int LOWEST_OCTAVE = 2;
	
	/**
	 * Return the number of possible octaves
	 * @return
	 */
	public static int getNumberOfPossibleOctaves() {
		return 3;
	}
	
	// Static stuff ends here
	
	/** Base note of this scale */
	private int base;
	
	/** Mode of this scale */
	private Mode mode;
	
	/**
	 * Constructor
	 * @param base	Base constant referring to the base note of this scale
	 * @param mode	Mode of this scale
	 */
	public Scale(int base, Mode mode) {
		this.setBase(base);
		this.setMode(mode);
	}
	
	/**
	 * Protected constructor for FixedFrequencyScale subclass
	 */
	protected Scale() {}
	
	/**
	 * Set the new base for this scale
	 * @param base
	 */
	public void setBase(int base) {
		this.base = base;
	}
	
	/**
	 * Set the new mode for this scale
	 * @param mode
	 */
	public void setMode(Mode mode) {
		this.mode = mode;
	}
	
	/**
	 * Returns the appropriate frequency of this scale corresponding to a knob value
	 * @param value
	 * @return
	 */
	public float getFrequencyForKnobValue(int value) {
		int octave = value / this.mode.length();
		return FREQUENCIES[this.base + (octave * 12) + this.mode.get(value)];
	}

	/**
	 * Returns the base frequency for this scale
	 * @return
	 */
	public float getBaseFrequency() {
		return FREQUENCIES[this.base];
	}

	/**
	 * Returns the number of nodes inside this scale
	 * @return
	 */
	public int noteCount() {
		return Scale.getNumberOfPossibleOctaves() * mode.length();
	}

	/**
	 * Returns the name of the given note index
	 * @param note
	 * @return
	 */
	public String getNoteName(int note) {
		return Scale.baseStrings[mode.get(note)];
	}
	
	/**
	 * Returns the octave of the given note index
	 * @param note
	 * @return
	 */
	public int getNoteOctave(int note) {
		return (note / mode.length()) + LOWEST_OCTAVE;
	}
	
	/**
	 * Returns the name of this scale
	 * @return
	 */
	public String getName() {
		return this.getNoteName(this.base) + mode.name();
	}
}
