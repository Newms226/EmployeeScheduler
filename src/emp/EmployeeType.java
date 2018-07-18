package emp;

import java.util.Arrays;

import tools.DriverTools;

public enum EmployeeType {
	Bartender, 
	Server, 
	Cook, // TODO
	ServerAssistant, // TODO 
	Host, // TODO 
	House; // TODO
	
	
	public static EmployeeType build() {
		EmployeeType toReturn = null;
		boolean valid = false;
		do {
			try {
				toReturn = valueOf(DriverTools.generateString("Please enter the employee's type."
						+ "\nOptions: " + Arrays.toString(values())));
				valid = true;
			} catch (IllegalArgumentException e) {
				System.out.println(e.getMessage() + "\nTry Again.");
			}
		} while (!valid);
		return toReturn;
	}
	
	public static void main(String[] args) {
		System.out.println(build().name());
	}
}
