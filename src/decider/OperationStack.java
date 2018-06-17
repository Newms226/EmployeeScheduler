package decider;

import java.io.Serializable;
import java.util.ArrayList;

import driver.Driver;
import tools.FileTools;

public class OperationStack implements Cloneable, Serializable {
	private static final long serialVersionUID = 7548634254297027242L;
	private ArrayList<Operation<? extends Cloneable>> stack;
	
//	Class<? extends Operation> classType;
	
	public OperationStack(/*Class<? extends Operation> classType*/) {
//		this.classType = classType;
		stack = new ArrayList<>();
	}
	
	public Operation<?> peek() {
		return stack.get(stack.size() - 1);
	}
	
	public Operation<?> pop() {
		return stack.remove(stack.size() - 1);
	}
	
	public Operation<?> push(Operation<?> operation) {
		stack.add(operation);
		return operation;
	}
	
	// TODO: Save before? FileManager enum like "BEFORE_ROLL_BACK"
	OperationStack rollBack(Operation<? extends Cloneable> op) {
		if (Driver.debugging) System.out.println(FileTools.LINE_BREAK + "ATTEMPTING TO ROLL BACK TO: " + op);
		if (stack.isEmpty()) {
			if (Driver.debugging) System.out.println("ERROR: Cannot roll back, empty operation stack");
			return null;
		}
		
		OperationStack clone = clone();
		
		Operation<? extends Cloneable> toExamine;
		while (!clone.stack.isEmpty()) {
			toExamine = clone.pop();
			toExamine.rollback(); // NOTE: This process returns an opstack WITHOUT op
			if (toExamine.equals(op)) {
				if (Driver.debugging) System.out.println("  FOUND: " + toExamine);
				break;
			}
		}
		
		if (Driver.debugging && clone.stack.isEmpty()) {
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
	
	public int count() {
		return stack.size();
	}
}
