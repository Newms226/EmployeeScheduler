package Availability;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import driver.Driver;
import time.LocalTimeInterval;
import time.Interval_SF;

class AvailabilityList {
	protected List<LocalTimeInterval> list;
	protected final Interval_SF statusFlag;
	private static Logger log = Driver.availabilityLog;
	
	public static List<LocalTimeInterval> splitForSchedule(LocalTimeInterval setToSchedule, LocalTimeInterval baseInterval) {
		log.log(Level.FINER,
				"ENTERING STATIC: Split for schedule:\n\tBase: {0}\n\tSetTo:{1}",
				new Object[] {baseInterval, setToSchedule});
		if (!baseInterval.contains(setToSchedule)) {
			IllegalArgumentException e = new IllegalArgumentException(baseInterval + " does not contain " + setToSchedule);
			log.log(Level.SEVERE, e.getMessage(), e);
			throw e;
		}
		
		List<LocalTimeInterval> toReturn = new ArrayList<LocalTimeInterval>();
		LocalTimeInterval frontAvoid, backAvoid, frontAvail, backAvail,
				scheduled = new LocalTimeInterval(Interval_SF.SCHEDULED, setToSchedule);
		log.log(Level.FINEST, 
				"Intitalized Interval objects and set scheduled to "
						+ "\n\t{0}",
				scheduled);
		toReturn.add(scheduled);
		
		Duration frontPeriod = Duration.between(baseInterval.start.localTime, 
		                                        setToSchedule.start.localTime),
				 backPeriod = Duration.between(setToSchedule.end.localTime,
						                            baseInterval.end.localTime);
		log.finest("Duration creation:"
				 + "\n\tfront: " + frontPeriod
				 + "\n\tback: " + backPeriod);
		
		long frontMinutes = frontPeriod.get(ChronoUnit.MINUTES),
			 backMinutes = backPeriod.get(ChronoUnit.MINUTES);
		if (!frontPeriod.isZero()) {
			log.fine("Prior period is non-zero, contains " + frontMinutes + " minutes");
			if (frontMinutes <= Availability.AVOID_MINUTE_AMOUNT) {
				frontAvoid = new LocalTimeInterval(Interval_SF.AVIOD, 
						                           baseInterval.start,
						                           setToSchedule.start);
				log.info("Prior period was less than default avoid amount:"
						+ "\n\t" + frontMinutes + " < " + Availability.AVOID_MINUTE_AMOUNT
						+ "\n\tNO FRONT_AVAIL OBJECT");
				log.log(Level.CONFIG,
						"Prior period is simply avoid \n\t{0}",
						frontAvoid);
				
				toReturn.add(frontAvoid);
			} else {
				frontAvoid = new LocalTimeInterval(Interval_SF.AVIOD, 
                                                   setToSchedule.start.minusMinutes(),
                                                   setToSchedule.start);
				frontMinutes -= Availability.AVOID_MINUTE_AMOUNT;
				frontAvail = new LocalTimeInterval(Interval_SF.AVAILABLE, 
                                                    baseInterval.start,
                                                    frontAvoid.start);
				log.log(Level.CONFIG,
						"Prior period split into avoid & available"
						+ "\n\tAvailable (" + frontMinutes + ") : {0}"
						+ "\n\tAvoid: {1} ",
						new Object[] {frontAvail, frontAvoid});
				
				toReturn.add(frontAvail);
				toReturn.add(frontAvoid);
			}
		} else if (frontPeriod.isNegative()) {
			log.severe("Prior period was negative!\n\t" + frontMinutes);
		} else {
			log.config("Prior period was 0: Verification: " + frontMinutes);
		}
		
		if (!backPeriod.isZero()) {
			log.fine("Following period is non-zero, contains " + backMinutes + " minutes");
			if (backMinutes <= Availability.AVOID_MINUTE_AMOUNT) {
				backAvoid = new LocalTimeInterval(Interval_SF.AVIOD, 
						                          setToSchedule.end,
						                          baseInterval.end);
				log.info("Following period was less than default avoid amount:"
						+ "\n\t" + backMinutes + " < " + Availability.AVOID_MINUTE_AMOUNT
						+ "\n\tNO BACK_AVAIL OBJECT");
				log.log(Level.CONFIG,
						"Following period is simply avoid \n\t{0}",
						backAvoid);
				
				toReturn.add(backAvoid);
			} else {
				backAvoid = new LocalTimeInterval(Interval_SF.AVIOD, 
                                                  setToSchedule.start,
                                                  setToSchedule.start.plusMinutes());
				backMinutes -= Availability.AVOID_MINUTE_AMOUNT;
				backAvail = new LocalTimeInterval(Interval_SF.AVAILABLE, 
                                                   backAvoid.end,
                                                   baseInterval.end);
				log.log(Level.CONFIG,
						"Following period split into avoid & available"
						+ "\n\tAvailable (" + backMinutes + ") : {0}"
						+ "\n\tAvoid: {1} ",
						new Object[] {backAvail, backAvoid});
				
				toReturn.add(backAvail);
				toReturn.add(backAvoid);
			}
		} else if (frontPeriod.isNegative()) {
			log.severe("Following period was negative!\n\t" + frontPeriod);
		} else {
			log.config("Prior period was 0: Verification: " + frontMinutes);
		}
		
		log.fine("Generated:\n\t" + Arrays.toString(toReturn.toArray()));
		
		return toReturn;
	}
	
	AvailabilityList(Interval_SF statusFlag) {
		list = new ArrayList<>();
		this.statusFlag = statusFlag;
		if (statusFlag.equals(Interval_SF.AVAILABLE)) {
			list.add(LocalTimeInterval.getAlwaysAvailable());
		}
	}
	
	// Note: This performs a shallow clone
	AvailabilityList(Collection<LocalTimeInterval> toAdd, Interval_SF statusFlag) {
		list = new ArrayList<>();
		this.statusFlag = statusFlag;
	}
	
	boolean add(LocalTimeInterval interval) {
		log.entering(this.getClass().getName(), "add: SF = " + statusFlag.name());
		NotEqualException.assertEqual(statusFlag, interval.getStatusFlag());
		
		if (contains(interval)) {
			log.log(Level.SEVERE,
					"Attempted to add {0} when it was already present",
					interval);
			return false;
		}
		
		// else
		log.log(Level.FINE,
				"Added {0}",
				interval);
		list.add(interval);
		return true;
	}
	
	Interval_SF getStatusFlag() {
		return statusFlag;
	}
	
	boolean contains(LocalTimeInterval chunk) {
		// TODO
		return false;
	}
}
