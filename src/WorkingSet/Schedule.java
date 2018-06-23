package WorkingSet;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;

import driver.Driver;
import emp.Employee;
import emp.EmployeeSet;
import restaurant.PositionID;
import tools.FileTools;

public class Schedule {
	public boolean valid;
	private List<PositionID<? extends Employee>> completedIDs;
	
	private final String numberedSchedule;
	
	public Schedule(Collection<PositionID<? extends Employee>> completedIDs) {
		Driver.deciderLog.config("Called constructor for Schedule");
		if (completedIDs.isEmpty()) {
			IllegalArgumentException e = new IllegalArgumentException("Passed in an empty collection of positionIDs to Schedule Constructor");
			Driver.deciderLog.log(Level.SEVERE, e.getMessage(), e);
			throw e;
		}
		this.completedIDs = new Vector<>(completedIDs);
		numberedSchedule = generateNumberedSchedule();
//		if (!verify()) {
//			if (Driver.debugging) System.out.println("WARNING: Schedule is invald!");
//		}
	}
	
	public boolean verify() {
		boolean completeSuccess = true;
		Driver.deciderLog.finer("Verifying Schedule");
		if (!verifiyQualifiation()) completeSuccess = false;
		if (!verifyAvailabilty()) completeSuccess = false;
		Driver.deciderLog.log(Level.CONFIG, "Evaluated the schedule as {0}", completeSuccess);
		return completeSuccess;
	}
	
	boolean verifiyQualifiation() {
		boolean toReturn = completedIDs.stream()
			.allMatch(ID -> {
				boolean test = ID.getEmplyee().qualifiedFor(ID);
				if (!test) Driver.deciderLog.severe(ID.getEmplyee() + " FAILED the qual test for " + ID);
				else Driver.deciderLog.finest(ID.getEmplyee() + " passed the qual test for " + ID);
				return test;
			});
		return toReturn;
	}
	
	boolean verifyAvailabilty() {
		boolean toReturn = completedIDs.stream()
			.allMatch(ID -> {
				boolean test = ID.getEmplyee().everAvailableFor(ID);
				if (!test) Driver.deciderLog.severe(ID.getEmplyee() + " FAILED the avail test for " + ID);
				else Driver.deciderLog.finest(ID.getEmplyee() + " passed the avail test for " + ID);
				return test;
			});
		return toReturn;
	}
	
	private String generateNumberedSchedule() {
		StringBuffer buffer = new StringBuffer();
		completedIDs
			.stream()
			.sorted(PositionID.DESCENDING_PRIORITY_ORDER)
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
				buffer.append(id.toCSVWithEmployee() + "\n" );
			});
		return buffer.toString();
	}
	
	public static <E extends Employee> Schedule fromCSV(String[] csv, EmployeeSet<E> list) {
		Driver.fileManagerLog.entering(Schedule.class.getName(), "fromCSV");
		List<PositionID<? extends Employee>> positionIDs = new Vector<>();
		PositionID<E> tempID;
		String[] components;
		for (String str: csv) {
			components = str.split(":");
			Driver.fileManagerLog.log(Level.FINE,
					                  "Split {0} into {1}", 
					                  new Object[] {str, Arrays.toString(components)});
			if (components.length != 2) 
				Driver.fileManagerLog.severe("DID NOT FIND THE CORRECT LENGTH. Expected: 2 Actual: " + components.length);
			tempID = PositionID.fromCSV(components[0]);
			tempID.assignEmployee(list.findEmployee(Integer.parseInt(components[1])));
			
			String toCompareTo = tempID.toCSVWithEmployee();
			Object[] toLog = {str, toCompareTo};
			if (toCompareTo.equals(str)) {
				Driver.fileManagerLog.log(Level.FINE,
						                  "Correctly parsed {0} to {1} ",
						                  toLog);
			} else {
				Driver.fileManagerLog.log(Level.SEVERE,
						                  "FAILED TO VALIDLY PARSE SCHEDULE: {0} != {1}",
						                  toLog);
			}
			
			positionIDs.add(tempID);
		}
		Driver.fileManagerLog.exiting(Schedule.class.getName(), "fromCSV");
		return new Schedule(positionIDs);
	}
	
	public String toString() {
		// TODO
		return null;
	}
	
	public boolean containsAll(Schedule that) {
		return completedIDs.containsAll(that.completedIDs);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (o == null) return false;
		
		if (!Schedule.class.equals(o.getClass())) return false;
		
		if (!containsAll((Schedule) o)) return false;
		
		return true;
	}
	
}
