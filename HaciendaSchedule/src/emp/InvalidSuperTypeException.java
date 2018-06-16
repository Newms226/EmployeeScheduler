package emp;

public class InvalidSuperTypeException extends Throwable {
	private static final long serialVersionUID = -7810345225876661177L;
	private Class<?> actualClass, expectedSuperClass;
	
	public <T> InvalidSuperTypeException(Class<? super T> expectedSuperClass, Class<?> actualClass) {
		this.actualClass = actualClass;
		this.expectedSuperClass = expectedSuperClass;
	}
	
	@Override
	public String getMessage() {
		return "Invalid Class Type. " + actualClass.getName() + " does not extend " + expectedSuperClass.getName(); 
	}
}
