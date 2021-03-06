package assignment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import driver.Driver;

public class OperationStack implements Cloneable, Serializable {
	private static final long serialVersionUID = 7548634254297027242L;
	private List<Operation<? extends Cloneable>> stack;
	
//	Class<? extends Operation> classType;
	
	public OperationStack(/*Class<? extends Operation> classType*/) {
//		this.classType = classType;
		stack = new ArrayList<>();
	}
	
	public Operation<?> peek() {
		return stack.get(stack.size() - 1);
	}
	
	public Operation<?> pop() {
		Operation<?> toPop = stack.get(stack.size() - 1);
		Driver.deciderLog.log(Level.FINER, "poped", toPop);
		stack.remove(toPop);
		return toPop;
	}
	
	public Operation<?> push(Operation<?> operation) {
		Driver.deciderLog.log(Level.FINER, "pusing", operation);
		stack.add(operation);
		return operation;
	}
	
	// TODO: Save before? FileManager enum like "BEFORE_ROLL_BACK"
	OperationStack rollBack(Operation<? extends Cloneable> op) {
		Driver.deciderLog.log(Level.INFO, "ATTEMPTING TO ROLL BACK TO: {0}",  op);
		if (stack.isEmpty()) {
			Driver.deciderLog.severe("Cannot roll back, empty operation stack");
			return null;
		}
		
		OperationStack clone = clone();
		
		Operation<? extends Cloneable> toExamine;
		while (!clone.stack.isEmpty()) {
			toExamine = clone.pop();
			toExamine.rollback(); // NOTE: This process returns an opstack WITHOUT op
			if (toExamine.equals(op)) {
				Driver.deciderLog.log(Level.INFO, "Found: {0}", toExamine);
				break;
			}
		}
		
		if (clone.stack.isEmpty()) {
			Driver.deciderLog.log(Level.SEVERE,
					"ERROR: {0} was not present in the list, returning an empty operation stack",
					op);
		}
		
		return clone; // TODO: Return empty clone or null?
	}
	
	public OperationStack clone() {
		// TODO
		return null;
	}
	
	public int count() {
		return stack.size();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (o == null) return false;
		
		if (!this.getClass().equals(o.getClass())) return false;
		
		OperationStack that = (OperationStack) o;
		
		if (stack.size() != that.stack.size()) return false;
		
		for (int i = 0; i < stack.size(); i++) {
			if (!stack.get(i).equals(that.stack.get(i))) return false;
		}
		
		
		return true;
	}
}
//
//public List<SchedulableTimeChunk> extractPositionIDs(){
//	Driver.deciderLog.entering(OperationStack.class.getName(), "extractPositionIDs");
//	return stack.stream()
//		.filter(o -> o.getClass().equals(AssignmentOperation.class))
//		.map(a -> ((AssignmentOperation)a).getPositionID())
//		.collect(Collectors.toList());
//}
//
//public String toCSV() {
//	Driver.fileManagerLog.entering(OperationStack.class.getName(), "toCSV");
//	if (stack.size() == 0) {
//		Driver.deciderLog.warning("EMPTY STACK");
//		return "EMPTY STACK";
//	}
//	StringBuffer buffer = new StringBuffer();
//	stack.stream()
//		.forEach(o -> buffer.append(o.toCSV() + "\n"));
//	Driver.fileManagerLog.exiting(OperationStack.class.getName(), "toCSV");
//	return buffer.toString();
//}
//
//public static OperationStack fromCSV(String[] csvFileLines, EmployeeSet<? extends Employee> list){
//	Driver.fileManagerLog.entering(OperationStack.class.getName(), "fromCSV");
//	OperationStack toReturn = new OperationStack();
//	for (int i = csvFileLines.length - 1; i >= 0; i--) {
//		if (csvFileLines[i].split(",")[0].equals(AssignmentOperation.class.getCanonicalName())) {
//			toReturn.push(AssignmentOperation.fromCSV(csvFileLines[i], list));
//		} else {
//			Error e = new Error("Bottomed out in OperationStack.fromCSV: " + csvFileLines[i].split(",")[0]);
//			Driver.deciderLog.log(Level.SEVERE, e.getMessage(), e);
//			throw e;
//		}
//	}
//	Driver.fileManagerLog.exiting(OperationStack.class.getName(), "fromCSV");
//	return toReturn;
//}
