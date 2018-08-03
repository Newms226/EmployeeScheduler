package emp;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import driver.Driver;
import restaurant.PositionType;
import tools.NumberTools;
import util.PrimativeDoubleArrayList;

public class EmployeePriority implements Comparable<EmployeePriority>, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5892393713077521485L;

	public static final double GRACE_FACTOR = .5,
            DATE_FACTOR = .4,
            QUAL_FACTOR = .1,
            FILLED_FACTOR = .2,

            // Grace Variables:
            DEFAULT_GRACE = 7,
            LOW_PROMOTE_GRACE = 8,
            MID_PROMOTE_GRACE = 9,
            HIGH_PROMOTE_GRACE = 10,
            
            // Hour effects
            PAST_DESIRED = .5,
            PAST_MAX = 0;
	
	Employee employee;
	PrimativeDoubleArrayList pastPriorites;
	
	// Start Day variables:
	LocalDate accurateDay;
	
	// Grace variables
	double grace;
	
	// Priority Variables
	double staticPriority,
	               currentPriority,
	               fillFactor;
	
	public EmployeePriority(Employee employee) {
		this(employee, DEFAULT_GRACE);
	}
	
	public EmployeePriority(Employee employee, double grace) {
		if (grace < 1 || grace > 10) {
			throw new IllegalArgumentException(grace + " is not a valid grace. [1,10]");
		}
		
		this.grace = grace;
		this.employee = employee;
		
		pastPriorites = new PrimativeDoubleArrayList();
		
		calculateStaticPriority();
	}
	
	public double getStaticPriority() {
		return staticPriority;
	}
	
	double getCurrentPrioirty() {
		return currentPriority;
	}
	
	double getCurrentPriority(double currentAverageFill) {
	//				if (filledShifts >= DESIRED_HOURS) {
	//					if (Driver.debugging) System.out.println(NAME + " is past their desired hours");
	//					currentPriority = PAST_DESIRED;
	//				}
	//				if (filledShifts >= MAX_HOURS) {
	//					if (Driver.debugging) System.out.println(NAME + " is past their max hours.");
	//					currentPriority = PAST_MAX;
	//				}
		double fillDouble = getFillDouble(currentAverageFill);
		if (fillDouble == 0) {
			Driver.masterLog.info("FILL DOUBLE CAME OUT AS 0, RETURNING STATIC");
			return staticPriority;
		}
		currentPriority = staticPriority + fillDouble;
		Driver.setUpLog.config("UPDATE: " + employee.NAME + "'s current priority is " + NumberTools.format(currentPriority)
				+ " from " + NumberTools.format(staticPriority) + " - (" + 
				NumberTools.format(FILLED_FACTOR) + " * " +  NumberTools.format(fillDouble)
				+ ")");
		
		return pastPriorites.add(currentPriority);
	}
	
	private double getFillDouble(double averageCount) {
		Driver.deciderLog.finer("Getting fill factor for:" + employee.NAME + ". Fill: " + NumberTools.format(employee.currentHours)
				+ " AverageFill: " + NumberTools.format(averageCount));
		if (employee.currentHours == averageCount) {
			Driver.setUpLog.finer(employee.NAME + " has the same number of hours as the average. No change from fill double");
			return 0;
		}
		
		double x = employee.currentHours - averageCount,
		       y = x * x * FILLED_FACTOR;
		if (x > 0) y	 = -y;
		
		Driver.deciderLog.finer("FILL DOUBLE: (" + NumberTools.format(employee.currentHours) + " - " + NumberTools.format(averageCount) 
				+ ") = "+ NumberTools.format(y));
		fillFactor = y;
		return y;
	}
	
	private double getDateDouble() {
		return DATE_FACTOR * NumberTools.normalizeToRange(
				employee.START_DATE.until(LocalDate.now(), ChronoUnit.DAYS), // Value
				0,                                                  // Begin range
				employee.restaurant.getDaysSinceOpen());                     // End Range
	}
	
	private double getGraceDouble() {
		return GRACE_FACTOR * grace;
	}
	
	private double getQualDouble() {
		return QUAL_FACTOR * PositionType.getQualifactionDouble(employee.qualifiedFor);
	}
	
	public String getSimplePriorityString(double currentAverageFill) {
		return new StringBuffer(NumberTools.format(currentPriority) + "\n\tD: " + NumberTools.format(getDateDouble()) 
			+ " G: " + NumberTools.format(getGraceDouble()) + " Q: " + NumberTools.format(getQualDouble()) 
			+ " + F: " + NumberTools.format(fillFactor)).toString();
	}
	
	public double[] getPriorityArray() {
		return pastPriorites.toArray(); 
	}
	
	public List<Double> getPriorityList(){
		return pastPriorites.asList();
	}
	
	double calculateStaticPriority() {
		double dateDouble = getDateDouble(),
				   graceDouble = getGraceDouble(),
				   qualDouble = getQualDouble(),
				   toReturn = dateDouble + graceDouble + qualDouble;
			
		Driver.setUpLog.config("Calculated static Priority of " + employee.NAME + " at " + NumberTools.format(toReturn)
				+ " from:"
			+ "\n  Date"
			+ "\n     Value: " + employee.START_DATE.until(LocalDate.now(), ChronoUnit.DAYS)
			+ "\n     Normalized from range: [0, " + NumberTools.format(employee.restaurant.getDaysSinceOpen()) + "]"
			+ "\n     Resulting in: " + NumberTools.format(dateDouble)
			+ "\n  Grace:"
			+ "\n     Value: " + grace
			+ "\n     Normalized from range: [0, 10]"
			+ "\n     Resulting in: " + NumberTools.format(graceDouble)
			+ "\n  Qualification:"
			+ "\n     Qualified For: " + Arrays.toString(employee.qualifiedFor.toArray())
			+ "\n     Normalized from range: [0," + PositionType.MAX_POSITION_VALUE +  "]"
			+ "\n     Resulting in: " + NumberTools.format(qualDouble)
		);
		
		currentPriority = staticPriority = toReturn;
		pastPriorites.add(currentPriority);
		return toReturn;
	}
	
	public void setGrace(double newGrace) {
		if (newGrace < 1 || newGrace > 10) {
			IllegalArgumentException e = new IllegalArgumentException(newGrace + " is not a valid grace. [1,10]");
			Driver.setUpLog.log(Level.SEVERE, e.getMessage(), e);
			throw e;
		}
		internalSetGrace(newGrace);
	}
	
	void internalSetGrace(double newGrace) {
		grace = newGrace;
		Driver.setUpLog.info("Setting " + employee.NAME + "'s grace to " + newGrace);
		calculateStaticPriority();
	}
	
	/* NOTE: This method works with potentially in-accurate priority values!
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(EmployeePriority that) {
		if (this.currentPriority < that.currentPriority) return -1;
		if (this.currentPriority > that.currentPriority) return 1;
		return 0;
	}
}