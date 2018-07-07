package Availability;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import driver.Driver;
import time.Week;

public class Availability {
	private static final Logger log = Driver.availabilityLog;
	
	
	PersistantWeeklyAvailability persistantAvail;
	TimeOffList timeOff;
	
	public Availability() {
		// TODO Auto-generated constructor stub
	}
	
	public WeekAvailability getWeekAvailability(Week week) {
		// TODO
		return null;
	}
	
	public boolean setTimeOff() {
		// TODO
		return false;
	}
	
	public boolean inTimeOff() {
		// TODO
		return false;
	}
	
	public boolean setPersistantAvailability() {
		// TODO
		return false;
	}
	
	public boolean everAvailable() {
		// TODO
		return false;
	}
}
