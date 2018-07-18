package Availability;

import java.util.Comparator;
import java.util.logging.Level;

import emp.Employee;
import restaurant.PositionType;
import tools.NumberTools;

/**
 * 
 * @author Michael Newman
 *
 */
public class SchedulableTimeChunk extends TimeChunk {
	
	/******************************************************************************
	 *                                                                            *
	 *                                                                            *
	 *                        Static Fields & Methods                             *
	 *                                                                            *
	 *                                                                            *
	 ******************************************************************************/
	
	private static final long serialVersionUID = 1712752644013694520L;

	/******************************************************************************
	 *                                                                            *
	 *                        Static Constructor Methods                          *
	 *                                                                            *
	 ******************************************************************************/
	
	public static SchedulableTimeChunk build() {
		return new SchedulableTimeChunk(TimeChunk.build(),
                NumberTools.generateDouble("Please enter a priority for this shift."
                		                  + "\nIt is recomended that you aim for an even distribution"
                		                  + "\nthrough all of the week's shifts.",
        		                  		  true, 1, 10),
                PositionType.build());
	}

	public static SchedulableTimeChunk from(TimeChunk chunk, 
			                                double priority,  
			                                PositionType positionType) {
		try {
			return new SchedulableTimeChunk(new TimeChunk(chunk), priority, positionType);
		} catch (IllegalArgumentException e) {
			log.log(Level.SEVERE, "FAILED TO CONSTUCT: " + e.getMessage(), e);
			return null;
		}
	}
	
	/******************************************************************************
	 *                                                                            *
	 *                               Static Fields                                *
	 *                                                                            *
	 ******************************************************************************/
	
	public static final Comparator<SchedulableTimeChunk> PRIORITY_ORDER = 
			(a, b) -> {
				if (a.priority < b.priority) return 1;
				if (a.priority > b.priority) return -1;
				return 0;
			};
	
	public static final Comparator<SchedulableTimeChunk> VIEW_ORDER = 
			(a, b) -> {
				if (a.dayStart < b.dayStart) return -1;
				if (a.dayStart > b.dayStart) return 1;
				if (a.positionType.compareTo(b.positionType) < 0) return -1;
				if (a.positionType.compareTo(b.positionType) > 0) return 1;
				if (a.indexStart < b.indexStart) return -1;
				if (a.indexStart > b.indexStart) return 1;
				return 0;
			};
			
	public static final Comparator<SchedulableTimeChunk> SHIFT_ID_ORDER = 
			(a, b) -> {
				if (a.indexStart < b.indexStart) return -1;
				if (a.indexStart > b.indexStart) return 1;
				if (a.indexEnd < b.indexEnd) return -1;
				if (a.indexEnd > b.indexEnd) return 1;
				return 0;
			};
			
			
	/******************************************************************************
	 *                                                                            *
	 *                                                                            *
	 *                        Instance Fields & Methods                           *
	 *                                                                            *
	 *                                                                            *
	 ******************************************************************************/
	
	public final PositionType positionType;

	private Employee employee;
	private double priority;

	/**
	 * @param chunk
	 */
	private SchedulableTimeChunk(TimeChunk chunk, 
			                     double priority, 
			                     PositionType positionType) {
		super(chunk);
		
		NumberTools.assertWithinRange1_10(priority);
		this.priority = priority;
		
		this.positionType = positionType;
		log.finest("CONSTRUCTOR: Created new ScheduleableTimeChunk:\n\t" + this);
	}
	
	/**
	 * 
	 * @param employee
	 * @return
	 */
	public boolean setEmployee(Employee employee) {
		if (employee == null) {
			log.log(Level.SEVERE, "NULL POINTER: Attepted to set " + this + " to a null employee. Returning false",
					new IllegalArgumentException());
			return false;
		}
		
		if (this.employee != null) {
			log.severe("OVERWRITE: Setting " + employee + " to " + this + " when " + this.employee + " was already scheduled"
					+ "\n\tWILL EXECUTE" );
		}
		
		// TODO: This is technically redundant
//		if (positionType.employeeTypeIsCompatible(employee.getEmployeeType())) {
			this.employee = employee;
			log.fine("SUCCESS: Scheduled " + employee + " to " + this);
			return true;
//		}
//		
//		// else
//		log.log(Level.SEVERE,
//				"FAILURE: Attempted to set " + employee + " to " + this + " when they were not qualified",
//				new IllegalArgumentException());
//		return false;
	}
	
	public Employee getEmployee() {
		return employee;
	}
	
	public double getPriority() {
		return priority;
	}

	void overidePriority(double priority) {
		try {
			NumberTools.assertWithinRange1_10(priority);
		} catch (IllegalArgumentException e) {
			log.log(Level.SEVERE, "FAILURE: Attempted to set priority to " + NumberTools.format(priority) 
				+ " when it was not within range.\n\t", e);
		}
		log.config("OVERIDE: Priority was set to " + NumberTools.format(priority) + " from " + NumberTools.format(this.priority));
		this.priority = priority; 
	}

	/******************************************************************************
	 *                                                                            *
	 *                                                                            *
	 *                              Override Methods                              *
	 *                                                                            *
	 *                                                                            *
	 ******************************************************************************/
	
	@Override
	public String toString() {
		if (employee == null) return super.toString();
		
		// else
		return super.toString() + " " + positionType + "\n\t" + employee;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null) return false;
		if (!o.getClass().equals(getClass())) return false;
		
		SchedulableTimeChunk that = (SchedulableTimeChunk) o;
		if (indexStart != that.indexStart) return false;
		if (indexEnd != that.indexEnd) return false;
		if (priority != that.priority) return false;
		if (positionType != that.positionType) return false;
		
		if (that.employee == null && employee != null) return false;
		if (that.employee != null && employee == null) return false;
		if (that.employee == null && employee == null) return true;
		if (!employee.equals(that.employee)) return false;
		
		return true;
	}
	
	@Override
	public SchedulableTimeChunk clone() {
		return SchedulableTimeChunk.from(TimeChunk.fromIndex(indexStart, indexEnd), 
				                         priority, 
				                         positionType);
	}
	
	public static void main(String[] args) {
		SchedulableTimeChunk test = SchedulableTimeChunk.from(TimeChunk.from(5, 17, 0, 5, 20, 0), 6, PositionType.Sales);
	}
	
}
