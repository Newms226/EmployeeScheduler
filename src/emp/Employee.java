package emp;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import Availability.Availability;
import Availability.SchedulableTimeChunk;
import Availability.WorkingAvailability;
import WorkingSet.AssignmentStatusFlag;
import driver.Driver;
import menu.ConsoleMenu;
import restaurant.PositionType;
import restaurant.Restaurant;
import time.Week;
import tools.CollectionTools;
import tools.NumberTools;

public class Employee implements Comparable<Employee>, Serializable {
	
	public static void main(String[] args) {}
	
	/******************************************************************************
	 *                                                                            *
	 *                               Static Fields                                *
	 *                                                                            *
	 ******************************************************************************/
	
	private static final long serialVersionUID = 9156517117805277061L;
	private static final Logger log = Driver.setUpLog;
	
	public static final Comparator<Employee> DESENDING_PRIORITY_ORDER = (a, b) -> -a.compareTo(b);
	public static final Comparator<Employee> NAME_ORDER = (a, b) -> a.NAME.compareToIgnoreCase(b.NAME);
//	public static final Comparator<Employee> PRIORITY_ORDER = (a, b) -> a.compareTo(b);
	
	
	/******************************************************************************
	 *                                                                            *
	 *                                                                            *
	 *                        Instance Fields & Methods                           *
	 *                                                                            *
	 *                                                                            *
	 ******************************************************************************/
	
	public final int ID;
	public final String NAME;
	public final LocalDate START_DATE;
	
	private double PAY;
	
	public transient EmployeePriority employeePriority; // TODO: Transient?
	public Availability availability;
	private WorkingAvailability currentAvailability;

	private int MAX_MINUTES,
	            DESIRED_MINUTES,
	            MIN_MINUTES,
	            GLOBAL_MAX_MINUTES;
	public short currentMinutes;
	
	private boolean overtimeAllowed;
	private int OVERTIME_LIMIT;

	public final Restaurant restaurant;
	
	ConsoleMenu serverMod;
	
	ArrayList<PositionType> qualifiedFor;
	ArrayList<SchedulableTimeChunk> assignedShifts;
	
	public final EmployeeType employeeType;
	
	
	public Employee(String name, int ID, LocalDate startDate, ArrayList<PositionType> possibleShifts) {
		this (name, ID, startDate, 0, 40*60, 35*60, 0, possibleShifts, EmployeeType.Server, false, 0);
		Driver.setUpLog.log(Level.CONFIG, "Generated generic employee {0}", name);
	}
	
	public Employee(String name, int ID, LocalDate startDate,
			double pay, int maxMinutes, int desiredMinutes, int minMinutes,
			ArrayList<PositionType> possibleShifts, EmployeeType employeeType,
			boolean overtimeAllowed, int overtimeMinutes)
	{
		name = name.trim();
		this.NAME = Character.toUpperCase(name.charAt(0)) + name.substring(1, name.length());
		this.ID = ID;
		this.PAY = pay;
		this.MAX_MINUTES = maxMinutes;
		this.DESIRED_MINUTES = desiredMinutes;
		this.MIN_MINUTES = minMinutes;
		this.qualifiedFor = possibleShifts;
		this.START_DATE = startDate;
		this.restaurant = Restaurant.HACIENDA;
		GLOBAL_MAX_MINUTES = restaurant.globalMaxHours * 60;
		employeePriority = new EmployeePriority(this);
		availability = new Availability();
		this.employeeType = employeeType;
		
		this.overtimeAllowed = overtimeAllowed;
		if (overtimeAllowed) {
			if (overtimeMinutes < maxMinutes) {
				IllegalArgumentException e = new IllegalArgumentException("CONSTRUCTOR FAILURE: Overtime"
						+ " minutes (" + overtimeMinutes + ") must be greater than max (" + maxMinutes + ")");
				log.log(Level.SEVERE, e.getMessage(), e);
			} else if (overtimeMinutes == maxMinutes) {
				log.warning("CONSTRUCTOR FAILURE: Overtime minutes == max minutes (" + maxMinutes + ")");
			} else {
				OVERTIME_LIMIT = overtimeMinutes; 
			}
		}
		
//		serverMod = new CMenu(NAME + " modification menu. What would you like to change?");
//		serverMod.add("Change available hours", null);
//		serverMod.add("View assgined shifts", () -> viewAssignedShifts());
//		// TODO
//		serverMod.add("Return", null, null, null);
//		
//		assignedShifts = new ArrayList<>();
	}
	
	/******************************************************************************
	 *                                                                            *
	 *                         Modification Methods                               *
	 *                                                                            *
	 ******************************************************************************/

	public void lowPromote() {
		Driver.setUpLog.fine("Low promote: " + NAME);
		employeePriority.internalSetGrace(EmployeePriority.LOW_PROMOTE_GRACE);
	}
	
	public void promote() {
		Driver.setUpLog.fine("Promote: " + NAME);
		employeePriority.internalSetGrace(EmployeePriority.MID_PROMOTE_GRACE);
	}
	
	public void highPromote() {
		Driver.setUpLog.fine("High promote: " + NAME);
		employeePriority.internalSetGrace(EmployeePriority.HIGH_PROMOTE_GRACE);
	}
	
	public double updateEmployeePriority(double averageFill) {
		return employeePriority.getCurrentPriority(averageFill);
	}
	
	public void changeQualifications() {
		StringBuffer buffer = new StringBuffer();
		qualifiedFor.stream().forEach(s -> buffer.append(s));
		Driver.setUpLog.config("Changing " + NAME + " qualifications. Present:\n\t" + buffer.toString());
		
		qualifiedFor = PositionType.buildServerPositions();
		
		StringBuffer buffer2 = new StringBuffer();
		qualifiedFor.stream().forEach(s-> buffer2.append(s));
		Driver.setUpLog.config("Changied " + NAME + " qualifications to\n\t" + buffer.toString());
	}
	
	public void menu() {
		serverMod.selection();
	}
	
	/******************************************************************************
	 *                                                                            *
	 *                                Query Methods                               *
	 *                                                                            *
	 ******************************************************************************/
	
	public boolean queryMinutes(SchedulableTimeChunk chunk, AssignmentStatusFlag currentStatus) {
		int minutes = chunk.getMinutes();
		
		switch (currentStatus) {
			case BELLOW_PERSONAL_MIN:
				return bellowMinimumHours();
			case BELLOW_DESIRED:
				return bellowDesiredHours();
			case BELLOW_PERSONAL_MAX:
				return bellowPersonalMaxAfter(minutes);
			case BELLOW_GLOBAL_MAX:
				return bellowGlobalMaxAfter(minutes);
			case OVERTIME:
				if (overtimeAllowed) {
					return bellowOvertimeAfter(minutes);
				} else return false;
			case HOUSE_ONLY: // fall through > illegal query
			default:
				throw new IllegalArgumentException(currentStatus + 
						" is not a valid parameter for this method");
		}
	}
	
	// TODO: Dont allow certian employees to work a chunk which has a high priority (& vice versa)
	public boolean canWork(SchedulableTimeChunk chunk) {
		log.finer("WORK QUERY: " + NAME + " for " + chunk.getInfoString());
		if (qualifiedFor(chunk) && availableToWork(chunk)) {
			log.info("SUCCESS: " + NAME + " can work " + chunk);
			return true;
		}
			
		// else	
		log.finer("FAILURE: " + NAME + " can NOT work " + chunk);
		return false;
	}
	
	boolean availableToWork(SchedulableTimeChunk chunk) {
		log.finer("Testing: " + NAME + " is available during " + chunk.getInfoString());
		if (currentAvailability.isAvailable(chunk)) {
			log.finer("SUCCESS");
			return true;
		}
		
		// else
		log.finer("FAILURE");
		return false;
	}
	
	boolean isEverAvailableFor(SchedulableTimeChunk chunk) {
		log.finer("Testing if " + chunk.getInfoString() + " is inside " + NAME + "'s persistant availability");
		if (availability.isInsidePersistantAvailability(chunk)) {
			log.finer("SUCCESS");
			return true;
		}
		
		// else
		log.finer("FAILURE");
		return false;
	}
	
	public boolean qualifiedFor(SchedulableTimeChunk ID) {
		log.finer("Testing: " + NAME + " can work a " + ID.positionType + " shift");
		if (qualifiedFor.contains(ID.positionType)) {
			log.finer("SUCCESS");
			return true;
		}
		
		// else
		log.finer("FAILURE");
		return false;
	}
	
	boolean bellowMinimumHours() {
		return currentMinutes <= MIN_MINUTES;
	}

	boolean bellowDesiredHours() {
		return currentMinutes <= DESIRED_MINUTES;
	}
	
	boolean bellowPersonalMax() {
		return currentMinutes <= MAX_MINUTES;
	}
	
	boolean bellowPersonalMaxAfter(int minutes) {
		return currentMinutes + minutes <= MAX_MINUTES;
	}
	
	boolean bellowGlobalMax() {
		return currentMinutes <= GLOBAL_MAX_MINUTES;
	}
	
	boolean bellowGlobalMaxAfter(int minutes) {
		return currentMinutes + minutes <= GLOBAL_MAX_MINUTES;
	}
	
	boolean bellowOvertime() {
		return currentMinutes <= OVERTIME_LIMIT;
	}
	
	boolean bellowOvertimeAfter(int minutes) {
		return currentMinutes + minutes <= OVERTIME_LIMIT;
	}
	
	/******************************************************************************
	 *                                                                            *
	 *                             Schedule Methods                               *
	 *                                                                            *
	 ******************************************************************************/
	
	public void accept(SchedulableTimeChunk chunk) {
		log.fine("SCHEDULED: " + NAME + " for " + chunk.getInfoString() 
				+ "\n\tCurrent Hours: " + ((double)currentMinutes/60));
		assignedShifts.add(chunk);
		currentMinutes += chunk.getMinutes();
		currentAvailability.schedule(chunk);
		log.finest("SUCCESS: SCHEDULED"
				+ "\n\tCurrent hours: " + ((double)currentMinutes/60)
				+ "\n\tAssigned Shifts: " + assignedShifts);
	}
	
	public boolean rollBack(SchedulableTimeChunk chunk) {
		log.fine("ROLLBACK: " + NAME + " for " + chunk
				+ "\n\tCurrent Hours: " + ((double)currentMinutes / 60));
		
		if(!assignedShifts.contains(chunk)) {
			log.warning("FAILURE: " + NAME + " was not assigned for " + chunk);
			return false;
		}
		
		// else
		assignedShifts.remove(chunk);
		currentMinutes -= chunk.getMinutes();
		log.finest("SUCCESS: ROLLBACK"
				+ "\n\tCurrent hours: " + ((double)currentMinutes / 60)
				+ "\n\tAssigned Shifts: " + assignedShifts);
		return false;
	}
	
	/******************************************************************************
	 *                                                                            *
	 *                        Setter & Getter Methods                             *
	 *                                                                            *
	 ******************************************************************************/
	
	public boolean isOvertimeEnabled() {
		return overtimeAllowed;
	}
	
	void setOvertimeEnabled(boolean enable) {
		overtimeAllowed = enable;
	}
	
	public int getOvertimeLimit() {
		return OVERTIME_LIMIT;
	}
	
	void setOvertimeLimit(int overtimeLimit) {
		OVERTIME_LIMIT = overtimeLimit;
	}
	
	public int getMinMinutes() {
		return MIN_MINUTES;
	}
	
	public int getDesiredMinutes() {
		return DESIRED_MINUTES;
	}
	
	public int getPersonalMaxMinutes() {
		return MAX_MINUTES;
	}
	
	public List<PositionType> getQualifications(){
		return Collections.unmodifiableList(qualifiedFor);
	}
	
	public EmployeeType getEmployeeType() {
		return employeeType;
	}
	
	public double getCurrentPrioirty() {
		return employeePriority.getCurrentPrioirty();
	}
	
	public void viewAssignedShifts() {
		CollectionTools.collectionPrinter(assignedShifts, false);
	}
	
	void setCurrentHours(short currentMinutes) {
		log.warning("UPDATE: " + NAME + "'s hours were set to " + currentMinutes);
		this.currentMinutes = currentMinutes;
	}
	
	void addToCurrentHours(double toAdd) {
		log.warning("UPDATE: " + NAME + "'s minutes by adding " + toAdd);
		currentMinutes += toAdd;
	}
	
	public void prepare(Week week) {
		this.currentAvailability = availability.getWorkingAvailability(week);
		assignedShifts = new ArrayList<>();
		currentMinutes = 0;
	}
	
	public String getCurrentStatusString() {
		StringBuffer buffer = new StringBuffer(NAME + " PRIORITY: " + 
				NumberTools.format(employeePriority.getCurrentPrioirty()) + "\n");
		
		buffer.append("  MINUTES > Current: " + NumberTools.format(currentMinutes)
				+ " Min: " + NumberTools.format(MIN_MINUTES)
				+ " Desired: " + NumberTools.format(DESIRED_MINUTES) 
				+ " P.Max: " + NumberTools.format(MAX_MINUTES) + "\n");
		
		buffer.append("  CURRENT SHIFTS > " + assignedShifts);
		
		return buffer.toString();
	}
	
	public AssignmentStatusFlag getCurrentStatus() throws Exception {
		if (currentMinutes < MIN_MINUTES) return AssignmentStatusFlag.BELLOW_PERSONAL_MIN;
		if (currentMinutes < DESIRED_MINUTES) return AssignmentStatusFlag.BELLOW_DESIRED;
		if (currentMinutes < MAX_MINUTES) return AssignmentStatusFlag.BELLOW_PERSONAL_MAX;
		if (currentMinutes <= GLOBAL_MAX_MINUTES) return AssignmentStatusFlag.BELLOW_GLOBAL_MAX;
		if (overtimeAllowed && currentMinutes < OVERTIME_LIMIT) return AssignmentStatusFlag.OVERTIME;
		
		// else
		Exception e = new Exception("FAILURE: " + NAME + " bottomed out in getCurrentStatusFlag()");
		log.log(Level.SEVERE, e.getMessage(), e);
		return null;
	}
	
	/******************************************************************************
	 *                                                                            *
	 *                             Override Methods                               *
	 *                                                                            *
	 ******************************************************************************/
	
	@Override
	public int compareTo(Employee that) {
		return employeePriority.compareTo(that.employeePriority);
	}
	
	@Override
	public boolean equals(Object that) {
		if (that == null) return false;
		if (this == that) return true;
		if (!this.getClass().equals(that.getClass())) return false;
		
		
		if (this.ID != ((Employee)that).ID) return false;
		return true;
	}
	
	public String toString() {
		return NAME;
	}
}

//public String toCSV() {
//	Driver.fileManagerLog.finest("toCSV for " + NAME);
//	return ID + "," + NAME + "," + START_DATE + ","  + PAY + ","
//			+ ArrayTools.print2D(POSSIBLE_HOURS, false) + ","
//			+ currentHours + "," + MAX_HOURS + "," + DESIRED_HOURS + "," + MIN_HOURS + ","
//			+ qualifiedFor.toString().replaceAll(" ", "");
//}
//
//public static <E extends Employee> E fromCSV(Class<E> employeeType) {
//	if (employeeType.equals(Server.class)) {
//		return Server.fromCSV(employeeType);
//	}
//	Error e =  new Error("Bottomed out in Employee.fromCSV()");
//	Driver.fileManagerLog.log(Level.SEVERE, e.getMessage(), e);
//	throw e;
//}



//public boolean everAvailableFor(PositionID<? extends Employee> ID) {
//	Driver.deciderLog.finer("Testing: " + NAME + " EVER available for " + ID);
//	boolean test = false;
//	if (POSSIBLE_HOURS[ID.getDay().ordinal()][ID.getShiftType().ordinal()]) {
//		test = true;
//	}
//	Driver.deciderLog.finer(test + "");
//	return test;
//}
//
//public boolean currentlyAvailableFor(PositionID<? extends Employee> ID) {
//	Driver.deciderLog.finer("Testing: " + NAME + " currenlty available for " + ID);
//	boolean test = false;
//	if (availableHours[ID.getDay().ordinal()][ID.getShiftType().ordinal()]) {
//		test = true;
//	}
//	Driver.deciderLog.finer(test + "");
//	return test;
//}


//public boolean canWork(PositionID<? extends Employee> ID) {
//	Driver.deciderLog.fine("Testing " + NAME + " on ID: " + ID);
//
//	// TESTS: TODO: If you ask can Server x work a Cook shift, will that fail?
//	if (!qualifiedFor(ID)) return false;
//	if (!currentlyAvailableFor(ID)) return false;
//	
//	// PASSED:
//	Driver.deciderLog.fine("FULL PASS:" + NAME + " can work " + ID /*+ " Presently: " +
//				this.availableHours[ID.getDay().dayOfWeek][ID.getShiftType().ordinal()]*/);
//	return true;
//}
