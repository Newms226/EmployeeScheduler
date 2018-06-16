package driver;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import tools.NumberTools;

public class Restaurant {
	public static final Restaurant HACIENDA = new Restaurant("Hacienda", LocalDate.of(2000, 1, 1), 60);
	
	public final LocalDate OPEN_DAY;
	private int daysSinceOpen;
	private boolean validDaysSinceOpenCount;
	public final String NAME;
	private int globalMaxHours;
	
	public Restaurant(String name, LocalDate openDay, int globalMaxHours) {
		OPEN_DAY = openDay;
		NAME = name;
		if (Driver.debugging) {
			System.out.println(LocalDate.now().toString());
			System.out.println(name + " has been open for " + NumberTools.format(getDaysSinceOpen()) + " days");
		}
		this.globalMaxHours = globalMaxHours;
	}
	
	public int getDaysSinceOpen() {
		if (Driver.debugging) System.out.println("In getDaysSinceOpen");
		if (!validDaysSinceOpenCount) {
			if (Driver.debugging) System.out.println("Invalid count. Revalidating");
			daysSinceOpen = (int) OPEN_DAY.until(LocalDate.now(), ChronoUnit.DAYS);
			validDaysSinceOpenCount = true;
		}
		return daysSinceOpen;
	}
	
	public int getGlobalMaxHours() {
		return globalMaxHours;
	}

	public static void main(String[] args) {
		System.out.println(LocalDate.of(2017, 1, 1).until(LocalDate.of(2018, 1, 1), ChronoUnit.DAYS));

	}

}
