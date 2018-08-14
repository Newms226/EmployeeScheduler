package assignment;

public enum AssignmentStatusFlag {
	BELLOW_PERSONAL_MIN (false){
		public AssignmentStatusFlag downgrade() {return BELLOW_DESIRED;}
	},
	BELLOW_DESIRED (false) {
		public AssignmentStatusFlag downgrade() {return BELLOW_DESIRED_MAX;}
	},
	BELLOW_DESIRED_MAX (true) {
		public AssignmentStatusFlag downgrade() {return BELLOW_ACTUAL_MAX;}
	},
	BELLOW_ACTUAL_MAX (true){
		public AssignmentStatusFlag downgrade() {return PAST_ACTUAL_MAX;}
	},
	PAST_ACTUAL_MAX(false) {
		public AssignmentStatusFlag downgrade() {
			throw new UnsupportedOperationException("FAILURE: Cannot downgrade PAST_ACTUAL_MAX");
		}
	},
	HOUSE_ONLY (false){
		public AssignmentStatusFlag downgrade() {
			throw new UnsupportedOperationException("FAILURE: Cannot downgrade HOUSE_ONLY");
		}
	};
	
	public final boolean ACCOUNT_FOR_FUTURE;
	
	private AssignmentStatusFlag(boolean future) {
		ACCOUNT_FOR_FUTURE = future;
	}
	
	public abstract AssignmentStatusFlag downgrade();
}
