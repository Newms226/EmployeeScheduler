package decider;

import java.util.ArrayList;

import driver.Driver;
import tools.FileTools;

public class OperationStack extends ArrayList<Operation<? extends Cloneable>> implements Cloneable {
	private static final long serialVersionUID = 7548634254297027242L;
	
//	Class<? extends Operation> classType;
	
	public OperationStack(/*Class<? extends Operation> classType*/) {
//		this.classType = classType;
	}
	
	public Operation<?> peek() {
		return get(size() - 1);
	}
	
	public Operation<?> pop() {
		return remove(size() - 1);
	}
	
	public Operation<?> push(Operation<?> operation) {
		add(operation);
		return operation;
	}
	
	// TODO: Save before? FileManager enum like "BEFORE_ROLL_BACK"
	OperationStack rollBack(Operation<? extends Cloneable> op) {
		if (Driver.debugging) System.out.println(FileTools.LINE_BREAK + "ATTEMPTING TO ROLL BACK TO: " + op);
		if (isEmpty()) {
			if (Driver.debugging) System.out.println("ERROR: Cannot roll back, empty operation stack");
			return null;
		}
		
		OperationStack clone = clone();
		
		Operation<? extends Cloneable> toExamine;
		while (!clone.isEmpty()) {
			toExamine = clone.pop();
			toExamine.rollback(); // NOTE: This process returns an opstack WITHOUT op
			if (toExamine.equals(op)) {
				if (Driver.debugging) System.out.println("  FOUND: " + toExamine);
				break;
			}
		}
		
		if (Driver.debugging && clone.isEmpty()) {
			System.out.println("ERROR: " + op + " was not present in the list, returning an empty operation stack");
		}
		
		
		return clone; // TODO: Return empty clone or null?
	}
	
	public String toCSV() {
		// TODO
		return "OPERATION STACK N/A\n";
	}
	
	public static OperationStack fromCSV(String csvFile){
		return null; // TODO
	}
	
	public OperationStack clone() {
		// TODO
		return null;
	}
}
