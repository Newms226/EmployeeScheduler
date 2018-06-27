package time;

import java.time.DayOfWeek;
import java.util.Arrays;

import tools.DriverTools;
import tools.NumberTools;

public class DayOfWeekBuilder {

	public static final DayOfWeek[] DEFAULT_DAY_OF_WEEK_ORDER = {DayOfWeek.FRIDAY,
			                                                     DayOfWeek.SATURDAY,
			                                                     DayOfWeek.THURSDAY,
			                                                     DayOfWeek.TUESDAY,
			                                                     DayOfWeek.WEDNESDAY,
			                                                     DayOfWeek.MONDAY,
			                                                     DayOfWeek.SUNDAY};
	
	private DayOfWeekBuilder() {}
	
	private static class PrioritizedDayOfWeek implements Comparable<PrioritizedDayOfWeek> {
		final DayOfWeek dayOfWeek;
		final int priority;
		
		private PrioritizedDayOfWeek(DayOfWeek dayOfWeek, int priority) {
			this.dayOfWeek = dayOfWeek;
			this.priority = priority;
		}

		@Override
		public int compareTo(PrioritizedDayOfWeek o) {
			if (priority < o.priority) return -1;
			if (priority > o.priority) return 1;
			return 0;
		}
	}
	
	public static DayOfWeek[] buildDayOfWeekPriorityArray(Week week) {
		int size = (week.dayCount >= 7) ? 7 : week.dayCount;
		PrioritizedDayOfWeek[] ordered = new PrioritizedDayOfWeek[size];
		int i = 0;
		for (DayOfWeek day: week.getDaysOfWeek()) {
			ordered[i] = new PrioritizedDayOfWeek(day, NumberTools.generateInt("Please enter a priority", false, 1, 10));
			i++;
		}
		Arrays.sort(ordered);
		
		DayOfWeek[] toReturn = new DayOfWeek[size];
		for(int k = 0; k < size; k++) {
			toReturn[k] = ordered[k].dayOfWeek;
		}
		return toReturn;
	}
	
	public static DayOfWeek build() {
		while (true) {
			try {
				return DayOfWeek.valueOf(DriverTools.generateString("Please enter a day:").toUpperCase());
			} catch (IllegalArgumentException | NullPointerException e) {
				System.out.println(e.getMessage() + "\nTry again");
			}
		}
	}
	
	public static DayOfWeek parse(String str) {
		return DayOfWeek.valueOf(str.toUpperCase());
	}
	
	public static DayOfWeek parse(int dayOfWeek0Based) {
		return DayOfWeek.values()[dayOfWeek0Based];
	}
}
