package Availability;

import java.time.DayOfWeek;
import java.util.logging.Logger;

import driver.Driver;

public class TimeOffset {
	private static final Logger log = Driver.availabilityLog;

	private TimeOffset() {} // prevent instance creation
	
	public static final int SUNDAY_OFFSET    = 0,
            MONDAY_OFFSET    = 1_440,
            TUESDAY_OFFSET   = 2_880,
            WEDNESDAY_OFFSET = 4_320,
            THURSDAY_OFFSET  = 5_760,
            FRIDAY_OFFSET    = 7_200,
            SATURDAY_OFFSET  = 8_640;

	public static DayOfWeek getDayOfWeekFromIndex(int index) {
		// TODO
		return null;
	}
	
	public static int getIndexFromDayOfWeek(DayOfWeek dayOfWeek) {
		switch (dayOfWeek) {
		case MONDAY: return MONDAY_OFFSET;
		case TUESDAY: return TUESDAY_OFFSET;
		case WEDNESDAY: return WEDNESDAY_OFFSET;
		case THURSDAY: return THURSDAY_OFFSET;
		case FRIDAY: return FRIDAY_OFFSET;
		case SATURDAY: return SATURDAY_OFFSET;
		case SUNDAY: return SATURDAY_OFFSET;
		default: return -1;
		}
	}
	
	public static int getIndexENDFromDayOfWeek(DayOfWeek dayOfWeek) {
		switch (dayOfWeek) {
		case MONDAY: return TUESDAY_OFFSET - 1;
		case TUESDAY: return WEDNESDAY_OFFSET - 1;
		case WEDNESDAY: return THURSDAY_OFFSET - 1;
		case THURSDAY: return FRIDAY_OFFSET - 1;
		case FRIDAY: return SATURDAY_OFFSET - 1;
		case SATURDAY: return AvailabilityArray.MAX_INDEX_VALUE - 1;
		case SUNDAY: return MONDAY_OFFSET - 1;
		default: return -1;
		}
	}

}
