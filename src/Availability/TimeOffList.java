package Availability;

import java.util.Comparator;
import java.util.function.Predicate;
import java.util.logging.Logger;

import driver.Driver;
import racer.StopWatch;

import java.util.*;
import java.io.Serializable;
import java.time.*;

import time.LocalDateInterval;
import time.Week;

public class TimeOffList implements Serializable {
	private static final long serialVersionUID = 3838845954801022396L;
	private static final Logger log = Driver.availabilityLog;
	
	private NavigableSet<LocalDate> datesOff;
	
	TimeOffList() {
		datesOff = new TreeSet<>();
		log.finer("Created new TimeOffList");
	}
	
	private boolean testAndLog(Predicate<LocalDate> predicate, LocalDate date, String methodName) {
		log.entering(this.getClass().getName(), methodName);
		if (predicate.test(date)){
			log.finer("SUCCESS: " + methodName);
			return true;
		} else {
			log.warning("FAILURE: " + methodName);
			return false;
		}
	}
	
	private boolean testAllAndLog(Predicate<LocalDate> predicate, LocalDateInterval interval, String methodName) {
		log.entering(this.getClass().getName(), methodName);
		boolean toReturn = true;
		for (LocalDate date : interval.extractLocalDates()) {
			if (!predicate.test(date)) toReturn = false;
		}
		return toReturn;
	}
	
	public boolean add(LocalDate date) {
		return testAndLog(d -> datesOff.add(d), date, "add(" + date + ")");
	}
	
	public boolean addAll(LocalDateInterval interval) {
		return testAllAndLog(d -> datesOff.add(d), interval, "addAll(" + interval + ")");
	}

	public boolean remove(LocalDate date) {
		return testAndLog(d -> datesOff.remove(d), date, "remove(" + date + ")");
	}
	
	public boolean removeAll(LocalDateInterval interval) {
		return testAllAndLog(d -> datesOff.remove(d), interval, "removeAll(" + interval + ")");
	}
	
	public boolean contains(LocalDate date) {
		return testAndLog(d -> datesOff.contains(d), date, "contains(" + date + ")");
	}
	
	public boolean containsAll(LocalDateInterval interval) {
		return testAllAndLog(d -> datesOff.contains(d), interval, "containsAll(" + interval + ")");
	}
	
	public Set<LocalDate> getAvailableDays(Week week) {
		log.entering(this.getClass().getName(), "getAvailableDays(" + week.toCondensedString() + ")");
		Set<LocalDate> tempSet = datesOff.subSet(week.firstDayOfWeek, true, week.dates[6], true);
		Set<LocalDate> toReturn = week.getDatesAsSet();
		toReturn.removeAll(tempSet);
		log.finer("RETURNING FROM " + this.getClass().getName() + ".getDaysOff()"
				+ "\n\tSTART: " + week.getDatesAsSet()
				+ "\n\tMINUS: " + tempSet
				+ "\n\tEQUALS: " + toReturn);
		tempSet = null;
		return toReturn;
	}
	
	public Set<LocalDate> getDaysOff(Week week) {
		log.entering(this.getClass().getName(), "getDaysOff(" + week.toCondensedString() + ")");
		return datesOff.subSet(week.firstDayOfWeek, true, week.dates[6], true);
	}
	
	public int size() {
		return datesOff.size();
	}
	
	public void archive() {
		// TODO
	}

//	public TimeOffList() {
//		super(LocalDateInterval.NATURAL_ORDER);
//	}
//	
//	List<LocalDate> getDaysOff(Week week){
//		NavigableSet<LocalDateInterval> intervals = 
//				new TreeSet<LocalDateInterval>(list).subSet(LocalDateInterval.ofSingleton(Availability_Status.AVAILABLE, week.firstDayOfWeek), true,
//						       LocalDateInterval.ofSingleton(Availability_Status.AVAILABLE, week.dates[6]), true);
//		
//		if (intervals.isEmpty()) {
//			log.config("FOUND NO TIME OFF REQUESTS: " + week.firstDayOfWeek + " - " + week.dates[6]);
//			return null;
//		}
//		
//		List<LocalDate> toReturn = new ArrayList<>();
//		for (LocalDateInterval interval: intervals) {
//			toReturn.addAll(new TreeSet<LocalDate>(interval.extractLocalDates()).subSet(week.firstDayOfWeek, true,
//					                                                                    week.dates[6], true));
//		}
//		
//		log.config("FOUND TIME OFF REQUESTS:\n\t" + toReturn);
//		return toReturn;
//	}
//	
//	// Expected result: [2018-02-03, 2018-02-04, 2018-02-05, 2018-02-08, 2018-02-09]
//	private static void testGetDaysOff() {
//		LocalDateInterval one = new LocalDateInterval(Availability_Status.OUTSIDE_AVAILABILITY, 
//				                                      LocalDate.of(2018, 2, 1),
//				                                      LocalDate.of(2018, 2, 5)),
//						two = new LocalDateInterval(Availability_Status.OUTSIDE_AVAILABILITY, 
//		                        LocalDate.of(2018, 2, 8),
//		                        LocalDate.of(2018, 2, 10));
//		TimeOffList test = new TimeOffList();
//		test.add(one);
//		test.add(two);
//		
//		Week week = new Week(LocalDate.of(2018, 2, 3));
//		List<LocalDate> daysOff;
//		long start, end;
//		start = System.nanoTime();
//		daysOff = test.getDaysOff(week);
//		end = System.nanoTime();
//		System.out.println(daysOff + "\nIn: " + StopWatch.nanosecondsToString(end - start));
//		
//		TreeSet<LocalDate> simpleSet = new TreeSet<>();
//		LocalDate date = LocalDate.of(2015, 5, 2);
//		LocalDate date2 = LocalDate.of(2015, 5, 3);
//		LocalDate date3 = LocalDate.of(2015, 5, 4);
//		simpleSet.add(date);
//		simpleSet.add(date2);
//		simpleSet.add(date3);
//		Week week2 = new Week(LocalDate.of(2015, 5, 3));
//		List<LocalDate> offDays;
//		start = System.nanoTime();
//		offDays = new ArrayList<>(simpleSet.subSet(week2.firstDayOfWeek,  true, week2.dates[6], true));
//		end = System.nanoTime();
//		System.out.println(offDays + "\n\tIn: " +  StopWatch.nanosecondsToString(end - start));
//		
//	}
	
	public static void main(String[] args) {
//		testGetDaysOff();
	}

}
