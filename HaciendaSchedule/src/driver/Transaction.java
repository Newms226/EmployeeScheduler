package driver;

public class Transaction {
	public final boolean insert; // true if insert, false if deletion
	private boolean success;
	
	// choice & other choices
	
	public Transaction() {
		insert = true;
	}
	
	public void rollBack() {
		
	}
	
	public Transaction nextChoice() {
		return null;
	}
	
	public String toString() {
		return null;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
