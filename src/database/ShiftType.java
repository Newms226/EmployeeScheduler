package database;
import java.io.Serializable;
import java.util.logging.Level;

import driver.Driver;
import tools.NumberTools;

public enum ShiftType implements Serializable {
	LUNCH, 
	DINNER;
	
	public static ShiftType buildShiftType() {
		Driver.databaseLog.finest("Building shift type");
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
		Driver.databaseLog.log(Level.FINER, "In getFromInt({0})", x);
		switch (x) {
			case 0: return LUNCH;
			case 1: return DINNER;
			default:
				IllegalArgumentException e =  new IllegalArgumentException("Invalid entry: " + x + " [0," + (values().length - 1) + "]");
				Driver.databaseLog.log(Level.SEVERE, e.getMessage(), e);
				throw e;
		}
	}
}