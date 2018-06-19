package decider;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import database.EmployeeSet;
import database.PositionID;
import driver.Driver;
import emp.Employee;
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
				if (Driver.debugging) System.out.println(" FOUND: " + toExamine);
				break;
			}
		}
		
		if (Driver.debugging && clone.stack.isEmpty()) {
			System.out.println("ERROR: " + op + " was not present in the list, returning an empty operation stack");
		}
		
		
		return clone; // TODO: Return empty clone or null?
	}
	
	public String toCSV() {
		if (stack.size() == 0) {
			return "EMPTY STACK";
		}
		StringBuffer buffer = new StringBuffer();
		stack.stream()
			.forEach(o -> buffer.append(o.toCSV() + "\n"));
		return buffer.toString();
	}
	
	public static OperationStack fromCSV(String[] csvFileLines, EmployeeSet<? extends Employee> list){
		OperationStack toReturn = new OperationStack();
		for (int i = csvFileLines.length - 1; i >= 0; i--) {
			if (csvFileLines[i].split(",")[0].equals(AssignmentOperation.class.getCanonicalName())) {
				toReturn.push(AssignmentOperation.fromCSV(csvFileLines[i], list));
			} else {
				throw new Error("Bottomed out in OperationStack.fromCSV: " + csvFileLines[i].split(",")[0]);
			}
		}
		return toReturn;
	}
	
	public OperationStack clone() {
		// TODO
		return null;
	}
	
	public List<PositionID<? extends Employee>> extractPositionIDs(){
		if (Driver.debugging) System.out.println("EXTRACTING...");
		return stack.stream()
			.filter(o -> o.getClass().equals(AssignmentOperation.class))
			.map(a -> ((AssignmentOperation)a).getPositionID())
			.collect(Collectors.toList());
	}
	
	public int count() {
		return stack.size();
	}
}
