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
		Driver.deciderLog.entering(this.getClass().getName(), "run");
		long startTime = System.nanoTime();
		List<PositionID<? extends Employee>> positionIDs = workingSet.setUp.getPositionIDsCOPY();
		Driver.deciderLog.finest("Copied PositionIDs");
		
		if (positionIDs.isEmpty()) {
			Driver.deciderLog.severe("PositionIDs are empty: Likely copy failed");
			throw new IllegalArgumentException("ERROR: PositionIDs is empty!");
		}
		workingSet.save(FileManager.SF.BEFORE_SCHEDULE);
		
		// TODO: This should never happen, ensured by the working set
		// ClassNotEqualException.assertEqual(workingSet.employeeType, positionIDs.get(0).employeeType);
		OperationStack opStack = new OperationStack();
		QualifiedEmployeeListMap<E> qualMap = new QualifiedEmployeeListMap<>(workingSet.employeeList, workingSet.restaurant.getGlobalMaxHours());
		
		averager = new Averager();
		PositionID<? extends Employee> ID;
		positionIDs.sort(PositionID.DESCENDING_PRIORITY_ORDER);
		Driver.deciderLog.finest("Current List: ");
		positionIDs.stream().forEachOrdered(i -> Driver.deciderLog.finest(i.toString()));
		Driver.deciderLog.info("Begin schedule");
		for (Iterator<PositionID<? extends Employee>> it = positionIDs.iterator(); it.hasNext(); ) {
			ID = it.next();
			Driver.deciderLog.info("ASSIGNING: " + ID);
			opStack.push(new AssignmentOperation(ID, 
					                             qualMap.getList((PositionID<E>) ID),
					                             averager)).run();
//			if (Driver.debugging) {
//				System.out.println("CURRENT LIST: ");
//				positionIDs.stream().forEachOrdered(i -> System.out.println(i));
//			}
		}
		long endTime = System.nanoTime();
		Driver.deciderLog.info("COMPLETE: Scheudler\n  IN: " + StopWatch.nanosecondsToString(endTime - startTime));
		workingSet.setResultSet(opStack, qualMap, opStack.extractPositionIDs());
		
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
