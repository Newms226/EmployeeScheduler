package driver;
import java.util.List;

import MyTime.Day;
import database.PositionID;
import database.ShiftType;
import tools.NumberTools;

public class Shift {
	public static final int MAX_PRIORITY = 10;
	final ShiftType type;
	
	public final List<PositionID> positions;
	
	public final double priority;
	
	public final Day day;

	public Shift(Day day, List<PositionID> positions, ShiftType shiftType, double priority) {
		NumberTools.rangeCheck1_10(priority);
		this.positions = positions;
		this.type = shiftType;
		this.priority = priority;
		this.day = day;
	}
	
//	List<PositionID> getPositionIDs() {
//		
//	}
	
//	public static void testPriority() {
//		ArrayList<PositionID> positions = new ArrayList<>();
//		positions.add(new PositionID(Day.FRIDAY, PositionType.Closer, ShiftType.DINNER, 10));
//		positions.add(new PositionID(Day.FRIDAY, PositionType.Closer, ShiftType.DINNER, 10));
//		positions.add(new PositionID(Day.FRIDAY, PositionType.Head_Wait, ShiftType.DINNER, 9));
//		positions.add(new PositionID(Day.FRIDAY, PositionType.Sales, 5));
//		Shift friday = new Shift(Day.FRIDAY, positions, ShiftType.DINNER, 10);
//		
//		ArrayList<Position> positions2 = new ArrayList<>();
//		positions2.add(new Position(PositionType.Closer, 10));
//		positions2.add(new Position(PositionType.Closer, 10));
//		positions2.add(new Position(PositionType.Head_Wait, 9));
//		positions2.add(new Position(PositionType.Sales, 2));
//		Shift thursday = new Shift(Day.THURSDAY, positions2, ShiftType.DINNER, 8);
//		
//		ArrayList<Position> positions3 = new ArrayList<>();
//		positions3.add(new Position(PositionType.Closer, 10));
//		positions3.add(new Position(PositionType.Closer, 10));
//		positions3.add(new Position(PositionType.Head_Wait, 9));
//		positions3.add(new Position(PositionType.Sales, 1));
//		Shift monday = new Shift(Day.MONDAY, positions3, ShiftType.DINNER, 2);
//		
//		ArrayList<PositionID> shiftIDs = new ArrayList<>(friday.extractPositionIDs());
//		shiftIDs.addAll(thursday.extractPositionIDs());
//		shiftIDs.addAll(monday.extractPositionIDs());
//		
//		shiftIDs.stream().sorted(PositionID.REVERSE_ORDER).forEach(i -> System.out.println(i.toString() + "\n"));
//	}
	
//	public static void main(String[] args) {
//		Shift.testPriority();
//	}

}
