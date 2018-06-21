package emp;
import java.time.LocalDate;

import restaurant.PositionID;
import restaurant.PositionType;

public class HouseShift extends Server {
	private static final long serialVersionUID = 50945142347011571L;

	public HouseShift() {
		super("House Shift", -1, LocalDate.now(), PositionType.getHouseQualList());
		employeeType = EmployeeType.House;
	}
	
	@Override
	public boolean canWork(PositionID<? extends Employee> ID) {
		return true;
	}
	
	@Override
	public String toCSV() {
		return "HOUSE";
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	@Override
	public void accept(PositionID<? extends Employee> ID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rollBack(PositionID<? extends Employee> ID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void markAvailable(PositionID<? extends Employee> ID) {
		// do nothing
	}

	@Override
	public void markUnavailable(PositionID<? extends Employee> ID) {
		// do nothing
		
	}

	@Override
	public boolean ofType(Class<?> type) {
		// TODO Auto-generated method stub
		return false;
	}

}
