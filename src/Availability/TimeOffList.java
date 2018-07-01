package Availability;

import java.util.Comparator;

import racer.StopWatch;

import java.util.*;
import java.time.*;

import time.Availability_Status;
import time.LocalDateInterval;
import time.Week;

public class TimeOffList extends AbstractIntervalList<LocalDateInterval> {
	private static final long serialVersionUID = 3838845954801022396L;

	public TimeOffList() {
		super(LocalDateInterval.NATURAL_ORDER);
	}
	
	List<LocalDate> getDaysOff(Week week){
		NavigableSet<LocalDateInterval> intervals = 
				new TreeSet<LocalDateInterval>(list).subSet(LocalDateInterval.ofSingleton(Availability_Status.AVAILABLE, week.firstDayOfWeek), true,
						       LocalDateInterval.ofSingleton(Availability_Status.AVAILABLE, week.dates[6]), true);
		
		if (intervals.isEmpty()) {
			log.config("FOUND NO TIME OFF REQUESTS: " + week.firstDayOfWeek + " - " + week.dates[6]);
			return null;
		}
		
		List<LocalDate> toReturn = new ArrayList<>();
		for (LocalDateInterval interval: intervals) {
			toReturn.addAll(new TreeSet<LocalDate>(interval.extractLocalDates()).subSet(week.firstDayOfWeek, true,
					                                                                    week.dates[6], true));
		}
		
		log.config("FOUND TIME OFF REQUESTS:\n\t" + toReturn);
		return toReturn;
	}
	
	// Expected result: [2018-02-03, 2018-02-04, 2018-02-05, 2018-02-08, 2018-02-09]
	private static void testGetDaysOff() {
		LocalDateInterval one = new LocalDateInterval(Availability_Status.OUTSIDE_AVAILABILITY, 
				                                      LocalDate.of(2018, 2, 1),
				                                      LocalDate.of(2018, 2, 5)),
						two = new LocalDateInterval(Availability_Status.OUTSIDE_AVAILABILITY, 
		                        LocalDate.of(2018, 2, 8),
		                        LocalDate.of(2018, 2, 10));
		TimeOffList test = new TimeOffList();
		test.add(one);
		test.add(two);
		
		Week week = new Week(LocalDate.of(2018, 2, 3));
		List<LocalDate> daysOff;
		long start, end;
		start = System.nanoTime();
		daysOff = test.getDaysOff(week);
		end = System.nanoTime();
		System.out.println(daysOff + "\nIn: " + StopWatch.nanosecondsToString(end - start));
	}

	@Override
	public boolean condense() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean validate() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void archive() {
		// TODO Auto-generated method stub
		
	}
	
	public static void main(String[] args) {
		testGetDaysOff();
	}

}
