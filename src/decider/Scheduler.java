package decider;


import java.util.Iterator;
import java.util.List;

import database.FileManager;
import database.PositionID;
import driver.Driver;
import emp.Employee;
import racer.StopWatch;
import tools.FileTools;
import util.Averager;

public class Scheduler<E extends Employee> {
	WorkingSet<E> workingSet;
	Averager averager;
	
	public Scheduler(WorkingSet<E> workingSet) {
		this.workingSet = workingSet;
	}
	
	
	@SuppressWarnings("unchecked")
	public void run() {
		long startTime = System.nanoTime();
		List<PositionID<? extends Employee>> positionIDs = workingSet.setUp.getPositionIDsCOPY();
		
		if (positionIDs.isEmpty()) {
			throw new IllegalArgumentException("ERROR: PositionIDs is empty!");
		}
		workingSet.save(FileManager.SF.BEFORE_SCHEDULE);
		
		// TODO: This should never happen, ensured by the working set
		// ClassNotEqualException.assertEqual(workingSet.employeeType, positionIDs.get(0).employeeType);
	
		averager = new Averager();
		PositionID<? extends Employee> ID;
		positionIDs.sort(PositionID.DESCENDING_PRIORITY_ORDER);
		if (Driver.debugging) {
			System.out.println("CURRENT LIST: ");
			positionIDs.stream().forEachOrdered(i -> System.out.println(i));
		}
		if (Driver.debugging) System.out.println(FileTools.LINE_BREAK + "BEGIN: Scheudler" + FileTools.LINE_BREAK);
		for (Iterator<PositionID<? extends Employee>> it = positionIDs.iterator(); it.hasNext(); ) {
			ID = it.next();
			if (Driver.debugging) System.out.println("ASSIGNING: " + ID);
			workingSet.opStack.push(new AssignmentOperation(ID, 
					                                        workingSet.queueMap.getList((PositionID<E>) ID),
					                                        averager)).run();
		}
		long endTime = System.nanoTime();
		if (Driver.debugging) System.out.println(FileTools.LINE_BREAK + "COMPLETE: Scheudler\n  IN: " + StopWatch.nanosecondsToString(endTime - startTime) + FileTools.LINE_BREAK);
		workingSet.setSchedule(positionIDs);
		workingSet.save(FileManager.SF.AFTER_SCHEDULE);
		/* > Get list of PositionIDs
		 * > Sort list according to priority
		 * > Work through the list
		 *   1. Get the matching priority queue
		 *   2. Sort
		 *   3. Assign the Server
		 *      a. Mark the server unavailable
		 *      b. Update that positionID
		 *      c. Calculate new average fill
		 *      d. Update the servers current priority
		 * > Return list of completed positionIDs
		 * 
		 */
	}
}
