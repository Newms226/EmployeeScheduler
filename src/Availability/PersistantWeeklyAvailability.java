package Availability;

import tools.NumberTools;

class PersistantWeeklyAvailability extends AvailabilityArray {

	private static final long serialVersionUID = 3045147335727365848L;

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
		for (int i = chunk.indexStart; i < chunk.indexEnd; i++) {
			if (availability[i] != AVAILABLE) modifications++;
			availability[i] = AVAILABLE;
		}
		log.finer("RETURNING: Made" + NumberTools.format(modifications) + " changes.");
		return true;
	}

	@Override
	boolean toSTRICTLYAvailable(TimeChunk chunk) {
		log.warning("UNSUPPORTED: Called toSTRICTLY Available from " + getClass().getName());
		return false;
	}

	@Override
	boolean toNotAvailable(TimeChunk chunk) {
		log.warning("UNSUPPORTED: Called toNotAvailable from " + getClass().getName()
				+ ". Recommended call: toNEVERAvailable.");
		return false;
	}

	@Override
	boolean toNEVERAvailable(TimeChunk chunk) {
		log.entering(getClass().getName(), "toNEVERAvailable(" + chunk + ")");
		int modifications = 0;
		for (int i = chunk.indexStart; i < chunk.indexEnd; i++) {
			if (availability[i] != NEVER_AVAILABLE) modifications++;
			availability[i] = NEVER_AVAILABLE;
		}
		log.finer("RETURNING: Made" + NumberTools.format(modifications) + " changes.");
		return true;
	}

}
