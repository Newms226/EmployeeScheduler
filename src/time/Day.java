package time;

import java.util.logging.Level;

import driver.Driver;
import tools.DriverTools;
import tools.NumberTools;

public enum Day {
	SUNDAY (7, "Su"),
	MONDAY (6, "Mo"),
	TUESDAY (4, "Tu"),
	WEDNESDAY (5, "We"),
	THURSDAY (3, "Th"),
	FRIDAY (1, "Fr"),
	SATURDAY (2, "Sa");
	
	private int priority;
	public final String abreviation;
	
	private Day(int priority, String abreviation) {
		this.priority = priority;
		this.abreviation = abreviation;
	}
	
	public int getPriority() {
		return priority;
	}
	
	public void setPriority(int priority) {
		Driver.setUpLog.log(Level.FINER, "Setting priority of " + this.name() + " to {0}", priority);
		try {
			NumberTools.assertWithinRange1_10(priority);
		} catch (IllegalArgumentException e) {
			Driver.setUpLog.log(Level.SEVERE, "Priority out of range: " + e.getMessage(), e);
		}
		this.priority = priority;
		Driver.setUpLog.exiting(this.getClass().getName(), "setPriority()");
	}
	
	public static String getAbreviationFromInt(int x) {
		Driver.setUpLog.entering(Day.class.getName(), "static getAbreviationFromInt(" + x + ")");
		try {
			return values()[x].abreviation;
		} catch (ArrayIndexOutOfBoundsException e) {
			Driver.setUpLog.log(Level.SEVERE, e.getMessage(), e);
			throw e;
		}	
	}
	
	public static Day build() {
		while (true) {
			try {
				return Day.valueOf(DriverTools.generateString("Please enter a day:").toUpperCase());
			} catch (IllegalArgumentException | NullPointerException e) {
				System.out.println(e.getMessage() + "\nTry again");
			}
		}
	}
	
	public static Day parse(String str) {
		return Day.valueOf(str.toUpperCase());
	}
	
	public static Day parse(int dayOfWeek0Based) {
		return Day.values()[dayOfWeek0Based];
	}
	
	public static void main(String[] args) {
		System.out.println(Day.build().name());
	}

}
