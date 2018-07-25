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
	
	private int MAX_HOURS,
	            DESIRED_HOURS,
	            MIN_HOURS; 
	public double currentHours;

	public final Restaurant restaurant;
	
	ConsoleMenu serverMod;
	
	ArrayList<PositionType> qualifiedFor;
	ArrayList<SchedulableTimeChunk> assignedShifts;
	
	public final EmployeeType employeeType;
	
	
	public Employee(String name, int ID, LocalDate startDate, ArrayList<PositionType> possibleShifts) {
		this (name, ID, startDate, 0, 40, 35, 0, possibleShifts, EmployeeType.Server);
		Driver.setUpLog.log(Level.CONFIG, "Generated generic employee {0}", name);
	}
	
	public Employee(String name, int ID, LocalDate startDate,
			double pay, int maxHours, int desiredHours, int minHours,
			ArrayList<PositionType> possibleShifts, EmployeeType employeeType)
	{
		name = name.trim();
		this.NAME = Character.toUpperCase(name.charAt(0)) + name.substring(1, name.length());
		this.ID = ID;
		this.PAY = pay;
		this.MAX_HOURS = maxHours;
		this.DESIRED_HOURS = desiredHours;
		this.MIN_HOURS = minHours;
		this.qualifiedFor = possibleShifts;
		this.START_DATE = startDate;
		this.restaurant = Restaurant.HACIENDA;
		employeePriority = new EmployeePriority(this);
		availability = new Availability();
		this.employeeType = employeeType;
		
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
	 *                             Question Methods                               *
	 *                                                                            *
	 ******************************************************************************/
	
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
	
	public boolean availableToWork(SchedulableTimeChunk chunk) {
		log.finer("Testing: " + NAME + " is available during " + chunk.getInfoString());
		if (currentAvailability.isAvailable(chunk)) {
			log.finer("SUCCESS");
			return true;
		}
		
		// else
		log.finer("FAILURE");
		return false;
	}
	
	public boolean isEverAvailableFor(SchedulableTimeChunk chunk) {
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
	
	public boolean bellowMinimumHours() {
		return currentHours <= MIN_HOURS;
	}

	public boolean bellowDesiredHours() {
		return currentHours <= DESIRED_HOURS;
	}
	
	public boolean bellowPersonalMax() {
		return currentHours <= MAX_HOURS;
	}
	
	// TODO: Only some employees are allowed to broach this limit. They have their own 'overtimeLimit'
	public boolean bellowGlobalMax() {
		return currentHours <= restaurant.globalMaxHours;
	}
	
	/******************************************************************************
	 *                                                                            *
	 *                             Schedule Methods                               *
	 *                                                                            *
	 ******************************************************************************/
	
	public void accept(SchedulableTimeChunk chunk) {
		log.fine("SCHEDULED: " + NAME + " for " + chunk.getInfoString() 
				+ "\n\tCurrent Hours: " + currentHours);
		assignedShifts.add(chunk);
		currentHours += (double) chunk.getMinutes() / 60;
		currentAvailability.schedule(chunk);
		log.finest("SUCCESS: SCHEDULED"
				+ "\n\tCurrent hours: " + currentHours
				+ "\n\tAssigned Shifts: " + assignedShifts);
	}
	
	public boolean rollBack(SchedulableTimeChunk chunk) {
		log.fine("ROLLBACK: " + NAME + " for " + chunk
				+ "\n\tCurrent Hours: " + currentHours);
		
		if(!assignedShifts.contains(chunk)) {
			log.warning("FAILURE: " + NAME + " was not assigned for " + chunk);
			return false;
		}
		
		// else
		assignedShifts.remove(chunk);
		currentHours -= (double) chunk.getMinutes() / 60;
		log.finest("SUCCESS: ROLLBACK"
				+ "\n\tCurrent hours: " + currentHours
				+ "\n\tAssigned Shifts: " + assignedShifts);
		return false;
	}
	
	/******************************************************************************
	 *                                                                            *
	 *                        Setter & Getter Methods                             *
	 *                                                                            *
	 ******************************************************************************/
	
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
	
	void setCurrentHours(double currentHours) {
		log.warning("UPDATE: " + NAME + "'s hours were set to " + currentHours);
		this.currentHours = currentHours;
	}
	
	void addToCurrentHours(double toAdd) {
		log.warning("UPDATE: " + NAME + "'s hours by adding " + toAdd);
		currentHours += toAdd;
	}
	
	public void prepare(Week week) {
		this.currentAvailability = availability.getWorkingAvailability(week);
		assignedShifts = new ArrayList<>();
		currentHours = 0;
	}
	
	public String getCurrentStatusString() {
		StringBuffer buffer = new StringBuffer(NAME + " PRIORITY: " + 
				NumberTools.format(employeePriority.getCurrentPrioirty()) + "\n");
		
		buffer.append("  HOURS > Current: " + NumberTools.format(currentHours)
				+ " Min: " + NumberTools.format(MIN_HOURS)
				+ " Desired: " + NumberTools.format(DESIRED_HOURS) 
				+ " P.Max: " + NumberTools.format(MAX_HOURS) + "\n");
		
		buffer.append("  CURRENT SHIFTS > " + assignedShifts);
		
		return buffer.toString();
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
