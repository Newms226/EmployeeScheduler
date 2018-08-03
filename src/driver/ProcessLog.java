package driver;

import java.util.logging.Logger;

import emp.Employee;
import emp.EmployeeSet;
import emp.QualifiedEmployeeList;
import tools.FileTools;

public class ProcessLog {
	private static final Logger log = Driver.deciderLog;
	
	public final String summary,
	                    fileName;
	public final int index;

	public ProcessLog(int i, Employee chosen, QualifiedEmployeeList sortedList, EmployeeSet allEmployees) {
		// TODO Auto-generated constructor stub
		fileName = generateFileName(i);
		summary = generateLog(i, chosen, sortedList, allEmployees);
		index = i;
		log.finest(summary);
	}
	
	private String generateLog(int i, Employee chosen, QualifiedEmployeeList sortedList, EmployeeSet allEmployees) {
		return new StringBuffer(i + ": " + sortedList.chunk.getInfoString() 
			+ "\nSELECTION: " + chosen.NAME
			+ "\n" + chosen.getCurrentStatusString() 
			+ FileTools.LINE_BREAK 
			+ "\nLIST STATUS: " + sortedList.getStatusFlag()
			+ "\n" + sortedList.getProcessString()
			+ "\nEMPLOYEE LIST:\n" + allEmployees.getProcessString()).toString();
	}
	
	private String generateFileName(int i ) {
		// TODO
		return null;
	}
	
	public String toString() {
		return summary;
	}

}
