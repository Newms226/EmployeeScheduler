package emp;

import java.util.logging.Level;

import driver.Driver;

public class ClassNotEqualException extends RuntimeException {
	private static final long serialVersionUID = -7810345225876661177L;
	private Class<?> actualClass, expectedClass;
	
	public ClassNotEqualException(Class<?> expectedClass, Class<?> actualClass) {
		this.actualClass = actualClass;
		this.expectedClass = expectedClass;
	}
	
	public static void assertEqual(Class<?> expectedClass, Class<?> actualClass) {
		if (!test(expectedClass, actualClass)) {
			ClassNotEqualException e = new ClassNotEqualException(expectedClass, actualClass);
			Driver.setUpLog.log(Level.SEVERE, e.getMessage(), e);
			throw e;
		}
		//else System.out.println("PASS");
	}
	
	public static boolean test(Class<?> expectedClass, Class<?> actualClass) {
		return actualClass.equals(expectedClass);
	}
	
	@Override
	public String getMessage() {
		return "Invalid Class Type. " + actualClass.getName() + " is not " + expectedClass.getName(); 
	}
	
	public static void main(String[] args) {
		assertEqual(Server.class, Employee.class);
	}
}

