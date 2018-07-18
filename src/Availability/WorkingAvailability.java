package Availability;

import tools.NumberTools;

public class WorkingAvailability extends AvailabilityArray {

	private static final long serialVersionUID = 8800343351835372328L;
	
	boolean schedule(TimeChunk chunk) {
		log.info("SCHEDULE: " + chunk);
		return toNotAvailable(chunk) && toSTRICTLYAvailableFORWARD(chunk.indexEnd);
	}
	
	boolean toSTRICTLYAvailableFORWARD(int startIndexINCLUSIVE) {
		log.entering(getClass().getName(), "toSTRICTLYAvailableFORWARD(" + startIndexINCLUSIVE + ")");
		int modifications = 0;
		byte testByte;
		boolean pass = true;
		for (int i = startIndexINCLUSIVE; i < TimeChunk.AVOID_MINUTE_COUNT; i++) {
			testByte = availability[i];
			if (testByte == AVAILABLE) {
				modifications++;
				availability[i] = STRICTLY_AVAILABLE;
			} else if (testByte == NEVER_AVAILABLE) {
				log.warning("FAILURE: Availability bit at " + i + 
						" is marked as NEVER_AVAILABLE. Cannot be set to Strictly Available");
				pass = false;
			}
		}
		log.finer("RETURNING: Made" + NumberTools.format(modifications) + " changes."
				+ "\n\t:Pass: " + pass + 
				(pass ? "" : " " + NumberTools.formatPercent((double)modifications/TimeChunk.AVOID_MINUTE_COUNT)));
		return pass;
	}
	
	boolean toNotAvailableIGNORE_STRICT(TimeChunk chunk) {
		log.entering(getClass().getName(), "toNotAvailableIGNORE_STRICT(" + chunk + ")");
		int modifications = 0;
		byte testByte;
		boolean pass = true;
		for (int i = chunk.indexStart; i < chunk.indexEnd; i++) {
			testByte = availability[i];
			if (testByte == AVAILABLE || testByte == STRICTLY_AVAILABLE) {
				modifications++;
				availability[i] = NOT_AVAILABLE;
			} else if (testByte == NEVER_AVAILABLE) {
				log.warning("FAILURE: Availability bit at " + i + 
						" is marked as NEVER_AVAILABLE. Cannot be set to Not Available");
				pass = false;
			}
		}
		log.finer("RETURNING: Made" + NumberTools.format(modifications) + " changes."
				+ "\n\t:Pass: " + pass + 
				(pass ? "" : " " + NumberTools.formatPercent((double)modifications/chunk.getMinutes())));
		return pass;
	}
	
	/******************************************************************************
	 *                                                                            *
	 *                                                                            *
	 *                        Override Abstract Methods                           *
	 *                                                                            *
	 *                                                                            *
	 ******************************************************************************/

	@Override
	boolean toAvailable(TimeChunk chunk) {
		log.entering(getClass().getName(), "toAvailable(" + chunk + ")");
		int modifications = 0;
		byte testByte;
		boolean pass = true;
		for (int i = chunk.indexStart; i < chunk.indexEnd; i++) {
			testByte = availability[i];
			if (testByte == NOT_AVAILABLE || testByte == STRICTLY_AVAILABLE) {
				modifications++;
				availability[i] = AVAILABLE;
			} else if (testByte == NEVER_AVAILABLE) {
				log.warning("FAILURE: Availability bit at " + i + 
						" is marked as NEVER_AVAILABLE. Cannot be set to Available");
				pass = false;
			}
		}
		
		log.finer("RETURNING: Made" + NumberTools.format(modifications) + " changes."
				+ "\n\t:Pass: " + pass + 
				(pass ? "" : " " + NumberTools.formatPercent((double)modifications/chunk.getMinutes())));
		return pass;
	}

	@Override
	boolean toSTRICTLYAvailable(TimeChunk chunk) {
		log.entering(getClass().getName(), "toSTRICTLYAvailable(" + chunk + ")");
		int modifications = 0;
		byte testByte;
		boolean pass = true;
		for (int i = chunk.indexStart; i < chunk.indexEnd; i++) {
			testByte = availability[i];
			if (testByte == AVAILABLE) {
				modifications++;
				availability[i] = STRICTLY_AVAILABLE;
			} else if (testByte == NEVER_AVAILABLE) {
				log.warning("FAILURE: Availability bit at " + i + 
						" is marked as NEVER_AVAILABLE. Cannot be set to Strictly Available");
				pass = false;
			}
		}
		log.finer("RETURNING: Made" + NumberTools.format(modifications) + " changes."
				+ "\n\t:Pass: " + pass + 
				(pass ? "" : " " + NumberTools.formatPercent((double)modifications/chunk.getMinutes())));
		return pass;
	}

	@Override
	boolean toNotAvailable(TimeChunk chunk) {
		log.entering(getClass().getName(), "toNotAvailable(" + chunk + ")");
		int modifications = 0;
		byte testByte;
		boolean pass = true;
		for (int i = chunk.indexStart; i < chunk.indexEnd; i++) {
			testByte = availability[i];
			if (testByte == AVAILABLE) {
				modifications++;
				availability[i] = NOT_AVAILABLE;
			} else if (testByte == NEVER_AVAILABLE) {
				log.warning("FAILURE: Availability bit at " + i + 
						" is marked as NEVER_AVAILABLE. Cannot be set to Not Available");
				pass = false;
			}
		}
		log.finer("RETURNING: Made" + NumberTools.format(modifications) + " changes."
				+ "\n\t:Pass: " + pass + 
				(pass ? "" : " " + NumberTools.formatPercent((double)modifications/chunk.getMinutes())));
		return pass;
	}

	@Override
	boolean toNEVERAvailable(TimeChunk chunk) {
		log.warning("UNSUPPORTED: Called toNEVERAvailable from " + getClass().getName()
				+ ". Recommended call: toNotAvailable.");
		return false;
	}
}
