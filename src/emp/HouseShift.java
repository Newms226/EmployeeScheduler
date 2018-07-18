package emp;
import java.time.LocalDate;
import java.util.logging.Logger;

import Availability.SchedulableTimeChunk;
import driver.Driver;
import restaurant.PositionType;

public class HouseShift extends Employee {
	
	/******************************************************************************
	 *                                                                            *
	 *                               Static Fields                                *
	 *                                                                            *
	 ******************************************************************************/
	private static final long serialVersionUID = 50945142347011571L;
	private static final Logger log = Driver.deciderLog;

	public HouseShift() {
		super("HouseShift", -1, LocalDate.now(), 0, 0, 0, 0, PositionType.getNewKid(), EmployeeType.House);
		Driver.deciderLog.fine("Generated new house shift");
	}
	
	/******************************************************************************
	 *                                                                            *
	 *                             Question Methods                               *
	 *                                                                            *
	 ******************************************************************************/
	
	@Override
	public boolean canWork(SchedulableTimeChunk chunk) {
		return true;
	}
	
	@Override
	public boolean availableToWork(SchedulableTimeChunk chunk) {
		return true;
	}
	
	@Override
	public boolean isEverAvailableFor(SchedulableTimeChunk chunk) {
		return true;
	}
	
	@Override
	public boolean qualifiedFor(SchedulableTimeChunk ID) {
		if (ID.positionType == PositionType.Sales) {
			return true;
		}
		
		// else
		log.severe("UNSUPPORTED: Asked if a House Shift was qualifed for " + ID.positionType);
		return false;
	}
	
	@Override
	public boolean bellowMinimumHours() {
		return true;
	}

	@Override
	public boolean bellowDesiredHours() {
		return true;
	}
	
	@Override
	public boolean bellowPersonalMax() {
		return true;
	}

	@Override
	public boolean bellowGlobalMax() {
		return true;
	}
	
	/******************************************************************************
	 *                                                                            *
	 *                             Schedule Methods                               *
	 *                                                                            *
	 ******************************************************************************/
	
	@Override
	public void accept(SchedulableTimeChunk chunk) {
		if (chunk.positionType != PositionType.Sales) {
			log.severe("ASSIGINING " + chunk.positionType + " TO A HOUSE SHIFT FOR " + chunk);
		}
		super.accept(chunk);
	}
}
