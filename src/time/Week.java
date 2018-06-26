package time;

import java.io.Serializable;
import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

import driver.Driver;

public class Week implements Serializable {
	private static final long serialVersionUID = -8732566734072764466L;
	private static final Logger log = Driver.setUpLog;
	
	public static final int DEFAULT_DAY_COUNT = 7;
	public static final DateTimeFormatter formatter =
			DateTimeFormatter.ofPattern("EEEE, MMMM d, uuuu");
	
	public final LocalDate[] dates;
	public final LocalDate firstDayOfWeek;
	public final int dayCount;
	public final String SUMMARY;
	
	public Week(LocalDate firstDayOfWeek) {
		this(firstDayOfWeek, DEFAULT_DAY_COUNT);
	}
	
	public Week(LocalDate firstDayOfWeek, int dayCount) {
		if (0 >= dayCount || dayCount > 9) {
			IllegalArgumentException e = 
					new IllegalArgumentException("INVALID DAY COUNT: " + 
									dayCount + " Excepted Range: [1-9]");
			log.log(Level.SEVERE, e.getMessage(), e);
		}
		this.firstDayOfWeek = firstDayOfWeek;
		this.dayCount = dayCount;
		dates = fillWeek();
		SUMMARY = generateString();
		log.fine("Generated " + toString());
	}
	
	private LocalDate[] fillWeek() {
		LocalDate[] dates = new LocalDate[dayCount];
		dates[0] = firstDayOfWeek;
		
		for (int i = 1; i < dayCount; i++) {
			dates[i] = dates[i - 1].plusDays(1);
		}
		
		return dates;
	}
	
	public LocalDate getDayOfWeek(DayOfWeek dayOfWeek) {
		for(LocalDate date: dates) {
			if (date.getDayOfWeek().equals(dayOfWeek)) {
				return date;
			}
		}
		DateTimeException e = new DateTimeException(dayOfWeek.name() + " is not supported by this week");
		log.log(Level.SEVERE, e.getMessage(), e);
		throw e;
	}
	
	private String generateString() {
		StringBuffer buffer = new StringBuffer(dayCount + " day week beginning on " 
										+ formatter.format(firstDayOfWeek) + "\n");
		
		for (int i = 0; i < dayCount; i++) {
			buffer.append("   " + (i+1) + ": " + formatter.format(dates[i]) + "\n");
		}
		
		return buffer.toString();
	}
	
	public String toString() {
		return SUMMARY;
	}
	
	public static void main(String[] args) {
		Week week = new Week(LocalDate.now());
		System.out.println(week.toString());
	}
}
