package assignment;

import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Logger;

import driver.Driver;
import emp.Employee;
import emp.EmployeeSet;
import emp.QualifiedEmployeeList;
import tools.FileTools;

public class ProcessLog {
	private static final Logger log = Driver.deciderLog;
	
	public final String summary,
	                    fileName;
	public final int assignmentIndex;

	<BUFFER extends CharSequence & Appendable & Serializable> ProcessLog
				(int i, Employee chosen, QualifiedEmployeeList sortedList, 
				EmployeeSet allEmployees, BUFFER buffer) throws IOException {
		// TODO Auto-generated constructor stub
		assignmentIndex = i;
		fileName = generateFileName();
		summary = generateLog(chosen, sortedList, allEmployees, buffer);
		
		log.finest(summary);
	}
	
	private <BUFFER extends CharSequence & Appendable & Serializable> 
				String generateLog(Employee chosen, QualifiedEmployeeList workingList, 
				EmployeeSet allEmployees, BUFFER buffer) throws IOException {
		int startIndex = buffer.length();
		
		buffer.append(assignmentIndex + ": " + workingList.chunk.getInfoString() 
		+ "\nSELECTION: " + chosen.NAME + "\n");
		chosen.appendCurrentStatusString(buffer);
		
		buffer.append(FileTools.LINE_BREAK + "\nLIST STATUS: " + workingList.getStatusFlag() + "\n");
		workingList.appendProcessString(buffer);
		
		buffer.append("\nEMPLOYEE LIST:\n");
		allEmployees.appendProcessString(buffer);
		
		return buffer.subSequence(startIndex, buffer.length()).toString();
	}
	
	private String generateFileName() {
		// TODO
		return null;
	}
	
	public String toString() {
		return summary;
	}

}
