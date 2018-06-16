package database;
import java.io.Serializable;

import tools.NumberTools;

public enum ShiftType implements Serializable {
	LUNCH, 
	DINNER;
	
	public static ShiftType buildShiftType() {
		while (true) {
			try {
				System.out.println("Which shift type would you like to add?");
				for (int i = 0; i < values().length; i++) {
					System.out.println(i + " " + values()[i]);
				}
				return getFromInt(NumberTools.generateInt(true, 0, values().length - 1));
			} catch (Exception e) {
				System.out.println(e.getMessage() + " Try Again.");
			}
		}
	}
	
	static ShiftType getFromInt(int x) {
		switch (x) {
			case 0: return LUNCH;
			case 1: return DINNER;
			default:
				throw new IllegalArgumentException("Invalid entry: " + x + " [0," + (values().length - 1) + "]");
		}
	}
}