package WorkingSet;

import java.time.LocalDateTime;

public interface Operation<T extends Cloneable> extends Cloneable {
	
//	public static enum OperationType {
//		a(AssignmentOperation.class){
//			public String toString() {return "Assginment Operation";}
//		};
//		
//		public final Class<? extends Operation<? extends Cloneable>> classType;
//		
//		private OperationType(Class<? extends Operation<? extends Cloneable>> classType) {
//			this.classType = classType;
//		}
//	}
	
	public T run();
	
	public boolean rollback();
	
	public boolean isRan();
	
	public boolean isRolledBack();
	
	public LocalDateTime getTime();
	
	public boolean equals(Object that); // for roll back
	
	public String toString();
//	
//	public static Operation<? extends Cloneable> fromCSV(String str);

}
