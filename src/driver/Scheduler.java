package driver;


import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import Availability.SchedulableTimeChunk;
import WorkingSet.AssignmentOperation;
import WorkingSet.OperationStack;
import WorkingSet.QualifiedEmployeeListMap;
import WorkingSet.Schedule;
import WorkingSet.ScheduleSetUp;
import emp.Employee;
import emp.EmployeeSet;
import racer.StopWatch;
import tools.FileTools;
import util.Averager;

public class Scheduler {
	private static final Logger log = Driver.deciderLog;
	
	Averager averager;
	EmployeeSet employees;
	ScheduleSetUp setUp;
	OperationStack opStack;
	QualifiedEmployeeListMap qualMap;
	
	public Scheduler(EmployeeSet employees, ScheduleSetUp setUp) {
		this.employees = employees;
		this.setUp = setUp;
		opStack = new OperationStack();
		qualMap = new QualifiedEmployeeListMap(employees);
		averager = new Averager();
		prepare();
	}
	
	void prepare() {
		log.warning("Preparing to schedule...");
		FileManager.serializeEmployeeSet(FileManager.SF.BEFORE_SCHEDULE, employees);
		FileManager.serializeScheduleSetUp(FileManager.SF.BEFORE_SCHEDULE, setUp);
		log.warning("Properly serialized");
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
		StringBuffer buffer = new StringBuffer("Current Sorted List: ");
		chunks.stream().forEachOrdered(i -> buffer.append(i + "\n"));
		log.finest("Current List: \n" + buffer.toString());
		
		log.info("Begin schedule");
		SchedulableTimeChunk workingChunk;
		int size = chunks.size();
		for (int i = 0; i < size; i++) {
			System.out.println(FileTools.LINE_BREAK 
					+ (i + 1) + "/" + size + " " 
						+ StopWatch.nanosecondsToString(System.nanoTime() - startTime)
					+ FileTools.LINE_BREAK);
			workingChunk = chunks.get(i);
			log.info("ASSIGNING: " + workingChunk);
			opStack.push(new AssignmentOperation(workingChunk, 
					                             qualMap.getList(workingChunk),
					                             averager)).run();
		}
		
		Schedule results = new Schedule(chunks);
		FileManager.serializeSchedule(FileManager.SF.AFTER_SCHEDULE, results);
		
		long endTime = System.nanoTime();
		String timeString = "COMPLETE: Scheduler\n  IN: " 
				+ StopWatch.nanosecondsToString(endTime - startTime);
		log.info(timeString);
		
		// Avoid loitering
		chunks = null;
		return results;
	}
}
