package assignment;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import driver.Driver;
import emp.Employee;
import emp.EmployeeSet;
import emp.QualifiedEmployeeList;
import tools.FileTools;

public class ProcessLogSet implements Serializable {
	private static final long serialVersionUID = -502099698426709413L;
	private static final Logger log = Driver.deciderLog;
	
	public static final int FLUSH_BUFFER_AFTER = 15;
	
	private List<ProcessLog> processLogs;
	private StringBuilder buffer;
	
	ProcessLogSet() {}
	
	ProcessLogSet(int size){
		buffer = new StringBuilder();
		processLogs = new ArrayList<>(size);
	}
	
	void log(int i, Employee chosen, QualifiedEmployeeList sortedList, 
				EmployeeSet allEmployees) {
		log.entering(getClass().getName(), "log()");
		
		if (i % FLUSH_BUFFER_AFTER == 0) {
			log.info("FLUSHING BUFFER...");
			buffer = new StringBuilder();
		}
		
		try {
			processLogs.add(new ProcessLog(i, chosen, sortedList, allEmployees, buffer));
		} catch (IOException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}
		
		log.exiting(getClass().getName(), "log()");
	}

	public List<ProcessLog> getProcessLogs(){
		return Collections.unmodifiableList(processLogs);
	}
	
	void examineProcessLogs() {
		if (processLogs.size() == 0) {
			System.out.println("Process logs never created");
			return;
		}
		
		Scanner scanner = new Scanner(System.in);
		
		int input = 0;
		int index = 0;
		int maxIndex = processLogs.size() - 1;
		boolean continueOn = true;
		do {
			try {
				System.out.println(processLogs.get(index) + FileTools.LINE_BREAK + "1 for backwards, 2 for forwards. 0 to exit"
						+ "\nCurrent index: " + index + "\nPlease enter an Integer:");
				input = scanner.nextInt();
				
				if (input == 1) {
					if (index == 0) {
						System.out.println("Cannot go backwards when index is 0");
					} else {
						index--;
					}
				} else if (input == 2) {
					if (index == maxIndex) {
						System.out.println("Cannot go fowards when index is size (" + maxIndex + ")");
					} else {
						index++;
					}
				} else if (input == 0) {
					System.out.println("Returning...");
					continueOn = false;
				} else {
					System.out.println("Invalid input");
				}
			} catch (InputMismatchException e) { // THERE ARE OTHER EXCEPTIONS POSSIBLE HERE TOO BTW
				System.out.println("Invalid input");
			}
		} while (continueOn);
		
		scanner.close();
	}
	
	String printProcessLogs() {
		StringBuffer buffer = new StringBuffer();
		
		for (ProcessLog l: processLogs) {
			buffer.append(l + FileTools.LINE_BREAK + FileTools.LINE_BREAK);
		}
		
		return buffer.toString();
	}
}
