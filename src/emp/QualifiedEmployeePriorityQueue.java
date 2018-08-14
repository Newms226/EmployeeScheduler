package emp;

import java.io.Serializable;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.logging.Logger;

import Availability.SchedulableTimeChunk;
import assignment.AssignmentStatusFlag;
import driver.Driver;
import restaurant.PositionType;

public class QualifiedEmployeePriorityQueue implements Serializable {
	
	/******************************************************************************
	 *                                                                            *
	 *                               Static Fields                                *
	 *                                                                            *
	 ******************************************************************************/
	
	private static final long serialVersionUID = 7898011253204194191L;
	private static final Logger log = Driver.deciderLog;
	private static final Comparator<Employee> comparator = Employee.DESENDING_PRIORITY_ORDER;
	
	/******************************************************************************
	 *                                                                            *
	 *                                                                            *
	 *                        Instance Fields & Methods                           *
	 *                                                                            *
	 *                                                                            *
	 ******************************************************************************/
	
	public final SchedulableTimeChunk chunk;
	
	private PriorityQueue<Employee> workingQueue,
	                                dumpIntoQueue;
	private AssignmentStatusFlag statusFlag;
	

	public QualifiedEmployeePriorityQueue(EmployeeSet employees, SchedulableTimeChunk chunk) {
		this.chunk = chunk;
		dumpIntoQueue = new PriorityQueue<>(comparator);
		statusFlag = AssignmentStatusFlag.BELLOW_DESIRED;
		
		workingQueue = new PriorityQueue<>(comparator);
		workingQueue.addAll(employees.filter(emp -> emp.canWork(chunk)));
		log.finer("INITIAL FILL FOR: " + chunk.getInfoString() + ">\n" + workingQueue);
		
		log.finest("CONSTRUCTOR: Created a new " + getClass().getName() + " object.\n" + this);
	}
	
	Employee foundEmployee;
	
	public Employee getEmployee() {
		// base case:
		if (statusFlag == AssignmentStatusFlag.HOUSE_ONLY
				|| workingQueue.isEmpty()) {
			return null;
		}
		
		foundEmployee = workingQueue.poll();
		if (foundEmployee.availableToWork(chunk)) {
			
		} 
		
		if (foundEmployee == null) {
			return getEmployee();
		} else {
			return foundEmployee;
		}
	}
	
	/******************************************************************************
	 *                                                                            *
	 *                       Getter & Setter Methods                              *
	 *                                                                            *
	 ******************************************************************************/
	
	public String getProcessString() {
		// TODO
		 return null;
	}
	
	public AssignmentStatusFlag getStatusFlag() {
		return statusFlag;
	}
	
	/******************************************************************************
	 *                                                                            *
	 *                              Override Methods                              *
	 *                                                                            *
	 ******************************************************************************/
	
	@Override
	public String toString() {
		// TODO
		return null;
	}
	
	@Override
	public boolean equals (Object o) {
		// TODO
		return true;
	}

}
