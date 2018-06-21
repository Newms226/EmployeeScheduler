package WorkingSet;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import driver.Driver;
import emp.Employee;
import restaurant.PositionID;
import tools.FileTools;

public class Schedule {
	public boolean valid;
	private Set<PositionID<? extends Employee>> completedIDs;
	
	private final String numberedSchedule;
	
	public Schedule(Collection<PositionID<? extends Employee>> completedIDs) {
		Driver.driverLog.config("Called constructor for Schedule");
		this.completedIDs = new HashSet<>(completedIDs);
		numberedSchedule = generateNumberedSchedule();
//		if (!verify()) {
//			if (Driver.debugging) System.out.println("WARNING: Schedule is invald!");
//		}
	}
	
	public boolean verify() {
		boolean completeSuccess = true;
		Driver.driverLog.finer("Verifying Schedule");
		if (!verifiyQualifiation()) completeSuccess = false;
		if (!verifyAvailabilty()) completeSuccess = false;
		Driver.driverLog.log(Level.CONFIG, "Evaluated the schedule as {0}", completeSuccess);
		return completeSuccess;
	}
	
	boolean verifiyQualifiation() {
		boolean toReturn = completedIDs.stream()
			.allMatch(ID -> {
				boolean test = ID.getEmplyee().qualifiedFor(ID);
				if (!test) Driver.driverLog.severe(ID.getEmplyee() + " FAILED the qual test for " + ID);
				else Driver.driverLog.finest(ID.getEmplyee() + " passed the qual test for " + ID);
				return test;
			});
		return toReturn;
	}
	
	boolean verifyAvailabilty() {
		boolean toReturn = completedIDs.stream()
			.allMatch(ID -> {
				boolean test = ID.getEmplyee().everAvailableFor(ID);
				if (!test) Driver.driverLog.severe(ID.getEmplyee() + " FAILED the avail test for " + ID);
				else Driver.driverLog.finest(ID.getEmplyee() + " passed the avail test for " + ID);
				return test;
			});
		return toReturn;
	}
	
	private String generateNumberedSchedule() {
		StringBuffer buffer = new StringBuffer();
		completedIDs
			.stream()
			.sorted(PositionID.DAY_ORDER)
			.forEach(s -> {
				buffer.append(s + " TYPE: " + s.employeeType + "\n");
			});
		return buffer.toString();
	}
	
	public String viewAll() {
		return numberedSchedule;
	}
	
	public int getPositionIDCount() {
		return completedIDs.size();
	}
	
	public String toCSV() {
		StringBuffer buffer = new StringBuffer();
		completedIDs
			.stream()
			.forEach(id ->{
				buffer.append(id.toCSV() + "," + id.getEmplyee().ID + "\n" );
			});
		return buffer.toString();
	}
	
	public static Schedule fromCSV(String csv) {
		// TODO
		return null;
	}
	
	public String toString() {
		// TODO
		return null;
	}
	
}
