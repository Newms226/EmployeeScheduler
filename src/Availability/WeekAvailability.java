package Availability;

import java.time.LocalDate;
import java.util.Map;

import time.Availability_Status;

public class WeekAvailability {
	Map <LocalDate, Map<Availability_Status, AvailabilityList>> map;
}
