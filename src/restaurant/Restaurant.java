package restaurant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import driver.Driver;
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
		NAME = name.trim();
		Driver.setUpLog.config(name + " has been open for " + NumberTools.format(getDaysSinceOpen()) + " days");
		this.globalMaxHours = globalMaxHours;
	}
	
	public int getDaysSinceOpen() {
		if (!validDaysSinceOpenCount) {
			Driver.setUpLog.config("Invalid daysSinceOpenCount. Revalidating");
			daysSinceOpen = (int) OPEN_DAY.until(LocalDate.now(), ChronoUnit.DAYS);
			validDaysSinceOpenCount = true;
		}
		return daysSinceOpen;
	}
	
	public int getGlobalMaxHours() {
		return globalMaxHours;
	}
	
	public String toCSV() {
		return NAME + "," + OPEN_DAY + "," + globalMaxHours;
	}
	
	public Restaurant fromCSV(String csvStr) {
		String[] components = csvStr.split(",");
		return new Restaurant(components[0],
				              LocalDate.parse(components[1]), 
				              Integer.parseInt(components[2]));
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (null == o) return false;
		
		if (!toCSV().equals(((Restaurant) o).toCSV())) return false;
		
		return true;
	}

	public static void main(String[] args) {
		System.out.println(LocalDate.of(2017, 1, 1).until(LocalDate.of(2018, 1, 1), ChronoUnit.DAYS));

	}

}
