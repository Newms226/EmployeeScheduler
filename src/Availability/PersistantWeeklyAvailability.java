package Availability;

class PersistantWeeklyAvailability {
	private byte[][][] persistantAvailability;
	
	PersistantWeeklyAvailability() {
		// TODO Auto-generated constructor stub
		persistantAvailability = new byte[Availability.TOTAL_DAYS][Availability.TOTAL_HOURS][Availability.TOTAL_MINUTES];
	}
	
	byte[][][] getWeek() {
		// TODO
		return null;
	}
	
	boolean setAvailability(TimeChunk chunk) {
//		return AvailabilityStatus.fillArray(persistantAvailability, chunk);
		// TODO
		return false;
	}
	
	boolean inAvailability(TimeChunk chunk) {
		// TODO
		return false;
	}
}
