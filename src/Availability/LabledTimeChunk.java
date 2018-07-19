package Availability;

import java.util.logging.Level;

public class LabledTimeChunk extends TimeChunk {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8649563322347330320L;

	/******************************************************************************
	 *                                                                            *
	 *                                                                            *
	 *                               Static Methods                               *
	 *                                                                            *
	 *                                                                            *
	 ******************************************************************************/
	
	public static LabledTimeChunk fromIndex(int indexSTART, int indexEND, byte label){
		return new LabledTimeChunk(TimeChunk.fromIndex(indexSTART, indexEND), label);
	}
	
	/******************************************************************************
	 *                                                                            *
	 *                                                                            *
	 *                        Instance Fields and Methods                         *
	 *                                                                            *
	 *                                                                            *
	 ******************************************************************************/
	
	private byte label;
	
	private LabledTimeChunk(TimeChunk chunk, byte label) {
		super(chunk);
		
		try {
			AvailabilityArray.assertValidStatusByte(label);
			this.label = label;
		} catch (IllegalArgumentException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			this.label = -1;
		}
	}
	
	public byte getLabel() {
		if (label == -1) {
			log.warning("Attempted to get a label byte when none was possible");
		}
		
		return label;
	}
	
	public String getLabelString() {
		if (label == -1) {
			log.warning("Attempted to get a label string when none was possible");
			return null;
		}
		
		// else
		return AvailabilityArray.statusByteToString(label);
	}
	
	@Override
	public String toString() {
		return getLabelString() + ": " + super.toString();
	}
}
