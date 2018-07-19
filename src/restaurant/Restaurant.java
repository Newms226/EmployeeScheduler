package restaurant;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class Restaurant implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3222029558593377007L;

	public static final Restaurant HACIENDA = new Restaurant("Hacienda", 
													LocalDate.of(2000, 1, 1),
													60,
													LocalTime.of(5, 0),
													LocalTime.MAX);
	
	public final LocalDate OPEN_DAY;
	private int daysSinceOpen;
	private boolean validDaysSinceOpenCount;
	public final String NAME;
	public final int globalMaxHours;
	public final LocalTime dayStart, dayEnd;
	
	public Restaurant(String name, LocalDate openDay, int globalMaxHours,
					  LocalTime dayStart, LocalTime dayEnd) {
		OPEN_DAY = openDay;
		NAME = name.trim();
//		Driver.setUpLog.config(name + " has been open for " 
//					+ NumberTools.format(getDaysSinceOpen()) + " days");
		this.globalMaxHours = globalMaxHours;
		this.dayEnd = dayEnd;
		this.dayStart = dayStart;
	}
	
	public int getDaysSinceOpen() {
		if (!validDaysSinceOpenCount) {
//			Driver.setUpLog.warning("Invalid daysSinceOpenCount. Revalidating");
			daysSinceOpen = (int) OPEN_DAY.until(LocalDate.now(), ChronoUnit.DAYS);
			validDaysSinceOpenCount = true;
		}
		return daysSinceOpen;
	}
	
	public int getGlobalMaxHours() {
		return globalMaxHours;
	}
	
//	public String toCSV() {
//		return NAME + "," + OPEN_DAY + "," + globalMaxHours;
//	}
//	
//	public Restaurant fromCSV(String csvStr) {
//		String[] components = csvStr.split(",");
//		return new Restaurant(components[0],
//				              LocalDate.parse(components[1]), 
//				              Integer.parseInt(components[2]));
//	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (null == o) return false;
		
		Restaurant that = (Restaurant) o;
		if (!NAME.equals(that.NAME)) return false;
		if (!OPEN_DAY.equals(that.OPEN_DAY)) return false;
		if (globalMaxHours != that.globalMaxHours) return false;
		if (!dayStart.equals(that.dayStart)) return false;
		if (!dayEnd.equals(that.dayEnd)) return false;
		
		return true;
	}

	public static void main(String[] args) {
		System.out.println(LocalDate.of(2017, 1, 1).until(LocalDate.of(2018, 1, 1), ChronoUnit.DAYS));

	}

}
