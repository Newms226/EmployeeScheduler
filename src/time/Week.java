package time;

import java.io.Serializable;
import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import driver.Driver;

public class Week implements Serializable {
	private static final long serialVersionUID = -8732566734072764466L;
	private static final Logger log = Driver.setUpLog;
	
	public static final DateTimeFormatter formatter =
			DateTimeFormatter.ofPattern("EEEE, MMMM d, uuuu");
	
	public final LocalDate[] dates;
	public final LocalDate firstDayOfWeek;
	public final String SUMMARY;
	
	public Week(LocalDate firstDayOfWeek) {
		this.firstDayOfWeek = firstDayOfWeek;
		dates = fillWeek();
		SUMMARY = generateString();
		log.fine("Generated " + toString());
	}
	
	private LocalDate[] fillWeek() {
		LocalDate[] dates = new LocalDate[7];
		dates[0] = firstDayOfWeek;
		
		for (int i = 1; i < 7; i++) {
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
		StringBuffer buffer = new StringBuffer("Week beginning on " 
										+ formatter.format(firstDayOfWeek) + "\n");
		
		for (int i = 0; i < 7; i++) {
			buffer.append("   " + (i+1) + ": " + formatter.format(dates[i]) + "\n");
		}
		
		return buffer.toString();
	}
	
	public String toString() {
		return SUMMARY;
	}
	
	public String toCondensedString() {
		return formatter.format(firstDayOfWeek) + " - " + formatter.format(dates[6]);
	}
	
	public Set<LocalDate> getDatesAsSet(){
		log.entering(this.getClass().getName(), "getDatesAsSet()");
		Set<LocalDate> toReturn = new HashSet<>();
		for (LocalDate date: dates) {
			toReturn.add(LocalDate.from(date));
		}
		log.log(Level.FINER, "RETURNING: getDatesAsSet()\n\tArray:{0}\n\tSet:{1}",
				new Object[] {Arrays.toString(dates), toReturn.toString()});
		return toReturn;
	}
	
	public static void main(String[] args) {
		Week week = new Week(LocalDate.now());
		System.out.println(week.toString());
	}
}
