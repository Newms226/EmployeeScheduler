package emp;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import driver.Driver;
import tools.NumberTools;

public class EmployeePriority implements Comparable<EmployeePriority> {
	
	public static final double GRACE_FACTOR = .5,
            DATE_FACTOR = .3,
            QUAL_FACTOR = .2,
            FILLED_FACTOR = 1,

            // Grace Variables:
            DEFAULT_GRACE = 7,
            LOW_PROMOTE_GRACE = 8,
            MID_PROMOTE_GRACE = 9,
            HIGH_PROMOTE_GRACE = 10,
            
            // Hour effects
            PAST_DESIRED = .5,
            PAST_MAX = 0;
	
	Employee employee;
	
	// Start Day variables:
	LocalDate accurateDay;
	
	// Grace variables
	double grace;
	
	// Priority Variables
	double staticPriority,
	               currentPriority;
	
	public EmployeePriority(Employee employee) {
		this(employee, DEFAULT_GRACE);
	}
	
	public EmployeePriority(Employee employee, double grace) {
		if (grace < 1 || grace > 10) {
			throw new IllegalArgumentException(grace + " is not a valid grace. [1,10]");
		}
		this.grace = grace;
		this.employee = employee;
		calculateStaticPriority();
	}
	
	public double getStaticPriority() {
		return staticPriority;
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
		double fillDouble = FILLED_FACTOR * getFillDouble(currentAverageFill);
		currentPriority = staticPriority + fillDouble;
		if (Driver.debugging) System.out.println("   " + employee.NAME + "'s current priority is " + NumberTools.format(currentPriority)
				+ " from " + NumberTools.format(staticPriority) + " - (" + 
				NumberTools.format(FILLED_FACTOR) + " * " +  NumberTools.format(fillDouble)
				+ ")");
		
		return currentPriority;
	}
	
	private double getFillDouble(double averageCount) {
		if (Driver.debugging) {
			System.out.println("   Getting fill factor for:" + employee.NAME + ". Fill: " + NumberTools.format(employee.filledShifts)
				+ " AverageFill: " + NumberTools.format(averageCount));
		}
		if (employee.filledShifts == averageCount) {
			if (Driver.debugging) {
				System.out.println(employee.NAME + " has the same number of shfits as the average. No change from fill double");
			}
			return 0;
		}
		
		double x = employee.filledShifts - averageCount,
		       y = x * x * FILLED_FACTOR;
		if (x > 0) y	 = -y;
		
		if (Driver.debugging) {
			System.out.println("   " + Character.toString((char) 402) + "(" + NumberTools.format(employee.filledShifts) + " - " + NumberTools.format(averageCount) 
				+ ") = "+ NumberTools.format(y));
		}
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
	
	double calculateStaticPriority() {
		if (Driver.debugging) {
			double dateDouble = getDateDouble(),
				   graceDouble = getGraceDouble(),
				   qualDouble = getQualDouble(),
				   toReturn = dateDouble + graceDouble + qualDouble;
			System.out.print("Calculated static Priority of " + employee.NAME + " at " + NumberTools.format(toReturn)
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
				+ "\n     Qualified For: ");
				employee.qualifiedFor.stream().forEach(s-> System.out.print(s.toString() + " ") );
				System.out.println(
				  "\n     Normalized from range: [0," + PositionType.MAX_POSITION_VALUE +  "]"
				+ "\n     Resulting in: " + NumberTools.format(qualDouble)
			);
			currentPriority = staticPriority = toReturn;
			return toReturn;
		}
		currentPriority = staticPriority = getGraceDouble() + getQualDouble() + getDateDouble();
		return staticPriority;
	}
	
	public void setGrace(double newGrace) {
		if (newGrace < 1 || newGrace > 10) {
			throw new IllegalArgumentException(newGrace + " is not a valid grace. [1,10]");
		}
		internalSetGrace(newGrace);
	}
	
	void internalSetGrace(double newGrace) {
		grace = newGrace;
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