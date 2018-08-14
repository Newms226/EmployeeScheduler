package assignment;


import java.util.ArrayList;
import java.util.Collections;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

import Availability.SchedulableTimeChunk;
import WorkingSet.Schedule;
import WorkingSet.ScheduleSetUp;
import driver.Driver;
import driver.FileManager;
import driver.FileManager.SF;
import emp.EmployeeSet;
import emp.QualifiedEmployeeListMap;
import racer.StopWatch;
import sun.misc.JavaSecurityProtectionDomainAccess.ProtectionDomainCache;
import time.Week;
import tools.FileTools;
import util.Averager;

public class Scheduler {
	private static final Logger log = Driver.deciderLog;
	
	EmployeeSet employees;
	ScheduleSetUp setUp;
	OperationStack opStack;
	QualifiedEmployeeListMap qualMap;
	Week week;
	ProcessLogSet processLogs;
	
	public Scheduler(EmployeeSet employees, ScheduleSetUp setUp, Week week) {
		this.employees = employees;
		this.setUp = setUp;
		this.week = week;
		opStack = new OperationStack();
		qualMap = new QualifiedEmployeeListMap(employees);
		processLogs = new ProcessLogSet(setUp.size());
		prepare();
	}
	
	private void prepare() {
		log.info("Preparing to schedule...");
		FileManager.serializeEmployeeSet(FileManager.SF.BEFORE_SCHEDULE, employees);
		FileManager.serializeScheduleSetUp(FileManager.SF.BEFORE_SCHEDULE, setUp);
		log.info("Properly serialized");
		employees.stream().forEach(emp -> emp.prepare(week));
		log.info("PREPARED");
	}
	
	public Schedule run() {
		log.entering(this.getClass().getName(), "run");
		
		long startTime = System.nanoTime();
		List<SchedulableTimeChunk> chunks = setUp.getPositionIDsCOPY();
		if (chunks.isEmpty()) {
			log.severe("FAILURE: PositionIDs are empty: Likely copy failed");
			return null;
		} else {
			log.finest("SUCCESS: Copied PositionIDs");
		}
		
		chunks.sort(SchedulableTimeChunk.PRIORITY_ORDER);
		StringBuffer buffer = new StringBuffer("TimeChunks:\n");
		chunks.stream().forEachOrdered(i -> buffer.append(i + "\n"));
		buffer.append("\nEmployees:\n");
		employees.stream().forEachOrdered(e -> buffer.append(e + "\n"));
		log.finest(buffer.toString());
		
		log.info("Begin schedule");
		SchedulableTimeChunk workingChunk;
		int size = chunks.size();
		String progress;
		AssignmentOperation currentOperation;
		for (int i = 0; i < size; i++) {
			progress = FileTools.LINE_BREAK 
					+ (i + 1) + "/" + size + " " 
						+ StopWatch.nanosecondsToString(System.nanoTime() - startTime)
					+ FileTools.LINE_BREAK;
			System.out.println(progress);
			log.info(progress);
			
			workingChunk = chunks.get(i);
			log.info("ASSIGNING: " + workingChunk);
			currentOperation = new AssignmentOperation(workingChunk, 
                                                       qualMap.getList(workingChunk));
			currentOperation.run();
			opStack.push(currentOperation);
			processLogs.log(i, 
			                currentOperation.getEmployee(), 
			                currentOperation.getQualifiedEmployeeList(), 
			                employees);
		}
		
		Schedule results = new Schedule(chunks);
		FileManager.serializeSchedule(SF.AFTER_SCHEDULE, results);
		FileManager.serializeOperationsStack(SF.AFTER_SCHEDULE, opStack);
		FileManager.serializeProcessLogs(processLogs);
		
		long endTime = System.nanoTime();
		String timeString = "COMPLETE: Scheduler\n  IN: " 
				+ StopWatch.nanosecondsToString(endTime - startTime);
		log.info(timeString);
		System.out.println(timeString);
		
		// Avoid loitering
		chunks = null;
		return results;
	}
	
	public List<ProcessLog> getProcessLogs(){
		return processLogs.getProcessLogs();
	}
}
