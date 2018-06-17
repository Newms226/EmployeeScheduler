package driver;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import database.PositionID;
import emp.Employee;
import tools.FileTools;

public class Schedule {
	public boolean valid;
	private Set<PositionID<? extends Employee>> completedIDs;
	
	public Schedule(Collection<PositionID<? extends Employee>> completedIDs) {
		this.completedIDs = new HashSet<>(completedIDs);
//		if (!verify()) {
//			if (Driver.debugging) System.out.println("WARNING: Schedule is invald!");
//		}
	}
	
	public boolean verify() {
		boolean completeSuccess = true;
		if (Driver.debugging) System.out.println(FileTools.LINE_BREAK + "Verifing Schedule..." );
		if (!verifiyQualifiation()) completeSuccess = false;
		else {
			if (Driver.debugging) System.out.println("PASSED: Qual Test");
		}
		if (!verifyAvailabilty()) completeSuccess = false;
		else {
			if (Driver.debugging) System.out.println("PASSED: Qual Test");
		}
		return completeSuccess;
	}
	
	boolean verifiyQualifiation() {
		boolean toReturn = completedIDs.stream()
			.allMatch(ID -> {
				boolean test = ID.getEmplyee().qualifiedFor(ID);
				if (Driver.debugging) {
					if (!test) System.out.println("ERROR" + ID.getEmplyee() + " FAILED the qual test for " + ID);
					else System.out.println("  " + ID.getEmplyee() + " passed the qual test for " + ID);
				}
				return test;
			});
		return toReturn;
	}
	
	boolean verifyAvailabilty() {
		boolean toReturn = completedIDs.stream()
			.allMatch(ID -> {
				boolean test = ID.getEmplyee().everAvailableFor(ID);
				if (Driver.debugging) {
					if (!test) System.out.println("ERROR" + ID.getEmplyee() + " FAILED the avail test for " + ID);
					else System.out.println("  " + ID.getEmplyee() + " passed the avail test for " + ID);
				}
				return test;
			});
		return toReturn;
	}
	
	public String viewAll() {
		StringBuffer buffer = new StringBuffer();
		completedIDs
			.stream()
			.sorted(PositionID.DAY_ORDER)
			.forEach(s -> {
				buffer.append(s + " TYPE: " + s.employeeType + "\n");
			});
		return buffer.toString();
	}
	
	public int getPositionIDCount() {
		return completedIDs.size();
	}
	
	public String toCSV() {
		StringBuffer buffer = new StringBuffer("{");
		completedIDs
			.stream()
			.forEach(id -> buffer.append("[" + id.toCSV() + "," + id.getEmplyee().ID + "]," ));
		
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
