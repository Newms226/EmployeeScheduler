package emp;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
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
import assignment.AssignmentStatusFlag;
import driver.Driver;
import menu.ConsoleMenu;
import racer.StopWatch;
import restaurant.PositionType;
import restaurant.Restaurant;
import time.Week;
import tools.CollectionTools;
import tools.FileTools;
import tools.NumberTools;
import tools.SerialTools;

public class Employee implements Comparable<Employee>, Serializable {
	
	public static void main(String[] args) {
		Employee kim = new Employee("Kim", 1,  LocalDate.of(2017, 1, 1), PositionType.getKing());
		File file = new File(FileTools.DEFAULT_IO_DIRECTORY + "kim.txt");
		long startTime, endTime;
		startTime = System.nanoTime();
		SerialTools.seralizeObject(kim, file);
		endTime = System.nanoTime();
		System.out.println("SERIALIZED IN " + StopWatch.nanosecondsToString(endTime - startTime));
		
		startTime = System.nanoTime();
		Employee deserial = (Employee) SerialTools.deserializeObject(file);
		endTime = System.nanoTime();
		System.out.println("DE-SERIALIZED IN " + StopWatch.nanosecondsToString(endTime - startTime));
		System.out.println(kim.equals(deserial));
		
		file = new File(FileTools.DEFAULT_IO_DIRECTORY + "kimCSV.txt");
		startTime = System.nanoTime();
		try (PrintWriter writer = new PrintWriter(file)) {
			
			writer.write(kim.toCSV());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		endTime = System.nanoTime();
		System.out.println("TOCSV IN " + StopWatch.nanosecondsToString(endTime - startTime));
	}
	
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

	private int DESIRED_MAX_MINUTES,
	            DESIRED_MINUTES,
	            MIN_MINUTES,
	            ACTUAL_MAX_MINUTES,
	            GLOBAL_MAX_MINUTES;
	public short currentMinutes;
	
	private boolean overtimeAllowed;

	public final Restaurant restaurant;
	
	ConsoleMenu serverMod;
	
	ArrayList<PositionType> qualifiedFor;
	ArrayList<SchedulableTimeChunk> assignedShifts;
	
	public final EmployeeType employeeType;
	
	
	public Employee(String name, int ID, LocalDate startDate, ArrayList<PositionType> possibleShifts) {
		this (name, ID, startDate, 0, 40*60, 40*60, 35*60, 0, possibleShifts, EmployeeType.Server);
		Driver.setUpLog.log(Level.CONFIG, "Generated generic employee {0}", name);
	}
	
	public Employee(String name, int ID, LocalDate startDate, double pay, 
			int desiredMaxMinutes, int actualMaxMinutes, int desiredMinutes, int minMinutes,
			ArrayList<PositionType> possibleShifts, EmployeeType employeeType)
	{
		name = name.trim();
		this.NAME = Character.toUpperCase(name.charAt(0)) + name.substring(1, name.length());
		this.ID = ID;
		this.PAY = pay;
		this.DESIRED_MINUTES = desiredMinutes;
		this.MIN_MINUTES = minMinutes;
		this.ACTUAL_MAX_MINUTES = actualMaxMinutes;
		this.DESIRED_MAX_MINUTES = desiredMaxMinutes;
		this.qualifiedFor = possibleShifts;
		this.START_DATE = startDate;
		this.restaurant = Restaurant.HACIENDA;
		GLOBAL_MAX_MINUTES = restaurant.globalMaxHours * 60;
		employeePriority = new EmployeePriority(this);
		availability = new Availability();
		this.employeeType = employeeType;
		
		if (actualMaxMinutes > restaurant.globalMaxHours * 60) overtimeAllowed = true;
		
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
			case BELLOW_DESIRED_MAX:
				return bellowPersonalDesiredMaxAfter(minutes);
			case BELLOW_ACTUAL_MAX:
				return bellowPersonalActualMaxAfter(minutes);
			case HOUSE_ONLY: // fall through > illegal query
			default:
				throw new IllegalArgumentException(currentStatus + 
						" is not a valid parameter for this method");
		}
	}
	
	/**
	 * NOTE: This method DOES NOT TEST WHETHER AN EMPLOYEE IS QUALIFIED!
	 * Proper method call:
	 * > canWork() > to build the list of qualified employees
	 * > isAssignable() > to filter down that list for the future
	 */
	public boolean isAssignable(SchedulableTimeChunk chunk, AssignmentStatusFlag SF) {
		log.finer("ASSIGNABLE QUERY: " + NAME + " for " + chunk + " when " + SF);
		if (queryFuture(chunk).compareTo(SF) <= 0 && availableToWork(chunk)) {
			log.finer("SUCCESS: ASSIGNABLE QUERY");
			return true;
		}
		
		// else
		log.finer("FAILURE: ASSIGNABLE QUERY");
		return false;
	}
	
	private int futureMinutes;
	
	// TODO: Define a buffer zone around the hours to prevent overtime from occuring during the week
	public AssignmentStatusFlag queryFuture(SchedulableTimeChunk chunk) {
		if (currentMinutes < MIN_MINUTES) 
			return AssignmentStatusFlag.BELLOW_PERSONAL_MIN;
		
		futureMinutes = currentMinutes + chunk.getMinutes();
		if (futureMinutes <= DESIRED_MINUTES)
			return AssignmentStatusFlag.BELLOW_DESIRED;
		if (futureMinutes <= DESIRED_MAX_MINUTES)
			return AssignmentStatusFlag.BELLOW_DESIRED_MAX;
		if (futureMinutes <= ACTUAL_MAX_MINUTES)
			return AssignmentStatusFlag.BELLOW_ACTUAL_MAX;
		
		// else
		return AssignmentStatusFlag.PAST_ACTUAL_MAX;
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
	
	boolean bellowMinimumHours() {
		return currentMinutes <= MIN_MINUTES;
	}

	boolean bellowDesiredHours() {
		return currentMinutes <= DESIRED_MINUTES;
	}
	
	boolean bellowPersonalDesiredMax() {
		return currentMinutes <= DESIRED_MAX_MINUTES;
	}
	
	boolean bellowPersonalDesiredMaxAfter(int minutes) {
		return currentMinutes + minutes <= DESIRED_MAX_MINUTES;
	}
	
	boolean bellowPersonalActualMax() {
		return currentMinutes <= ACTUAL_MAX_MINUTES;
	}
	
	boolean bellowPersonalActualMaxAfter(int minutes) {
		return currentMinutes + minutes <= DESIRED_MAX_MINUTES;
	}
	
	boolean bellowGlobalMax() {
		return currentMinutes <= GLOBAL_MAX_MINUTES;
	}
	
	boolean bellowGlobalMaxAfter(int minutes) {
		return currentMinutes + minutes <= GLOBAL_MAX_MINUTES;
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
	
	public int getMinMinutes() {
		return MIN_MINUTES;
	}
	
	public int getDesiredMinutes() {
		return DESIRED_MINUTES;
	}
	
	public int getDesiredMaxMinutes() {
		return DESIRED_MAX_MINUTES;
	}
	
	public int getActualMaxMinutes() {
		return ACTUAL_MAX_MINUTES;
	}
	
	public List<PositionType> getQualifications(){
		return Collections.unmodifiableList(qualifiedFor);
	}
	
	public EmployeeType getEmployeeType() {
		return employeeType;
	}
	
	public double getCurrentPrioirty() {
		return employeePriority.getCurrentPriority();
	}
	
	public void viewAssignedShifts() {
		CollectionTools.collectionPrinter(assignedShifts, false);
	}
	
	void setCurrentHours(short currentMinutes) {
		log.warning("UPDATE: " + NAME + "'s minutes were set to " + currentMinutes);
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
		employeePriority.updateActualMaxMinutes(ACTUAL_MAX_MINUTES);
	}
	
	public <BUFFER extends CharSequence & Appendable & Serializable> void 
				appendCurrentStatusString(BUFFER buffer) throws IOException {
		buffer.append(NAME + " PRIORITY: " + 
				NumberTools.format(employeePriority.getCurrentPriority()) + "\n");
		
		buffer.append("  MINUTES > Current: " + NumberTools.format(currentMinutes)
				+ " Min: " + NumberTools.format(MIN_MINUTES)
				+ " Desired: " + NumberTools.format(DESIRED_MINUTES) 
				+ " D.Max: " + NumberTools.format(DESIRED_MAX_MINUTES)
				+ " A.Max: " + NumberTools.format(ACTUAL_MAX_MINUTES)
				+ "\n  CURRENT SHIFTS > ");
		
		appendAssignedShifts(buffer, false);
	}
	
	public <BUFFER extends CharSequence & Appendable & Serializable> void 
				appendAssignedShifts(BUFFER buffer, boolean newLine) throws IOException {
		for (SchedulableTimeChunk chunk: assignedShifts) {
			buffer.append(chunk.getInfoString() + (newLine ? "\n" : ""));
		}
		buffer.append("\n");
	}
	
	public AssignmentStatusFlag getCurrentStatus() throws Exception {
		if (currentMinutes <= MIN_MINUTES) return AssignmentStatusFlag.BELLOW_PERSONAL_MIN;
		if (currentMinutes <= DESIRED_MINUTES) return AssignmentStatusFlag.BELLOW_DESIRED;
		if (currentMinutes <= DESIRED_MAX_MINUTES) return AssignmentStatusFlag.BELLOW_DESIRED_MAX;
		if (currentMinutes <= ACTUAL_MAX_MINUTES) return AssignmentStatusFlag.BELLOW_ACTUAL_MAX;
		
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
	
	public String toCSV() {
		Driver.fileManagerLog.finest("toCSV for " + NAME);
		return ID + "," + NAME + "," + START_DATE + ","  + PAY + ","
				+ DESIRED_MAX_MINUTES + "," + DESIRED_MINUTES  + "," + MIN_MINUTES  + "," 
				+ ACTUAL_MAX_MINUTES  + "," + GLOBAL_MAX_MINUTES + "," + currentMinutes
				+ restaurant.NAME + "," + qualifiedFor.toString().replaceAll(" ", "")  + ","
				+ (assignedShifts == null ? "" : assignedShifts.toString().replaceAll(" ", "") + "," ) 
				+ availability.toCSV();
	}
}


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
