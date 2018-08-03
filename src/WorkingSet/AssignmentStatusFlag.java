package WorkingSet;

public enum AssignmentStatusFlag {
	BELLOW_PERSONAL_MIN (false){
		public AssignmentStatusFlag downgrade() {return BELLOW_DESIRED;}
	},
	BELLOW_DESIRED (false) {
		public AssignmentStatusFlag downgrade() {return BELLOW_PERSONAL_MAX;}
	},
	BELLOW_PERSONAL_MAX (true) {
		public AssignmentStatusFlag downgrade() {return BELLOW_GLOBAL_MAX;}
	},
	BELLOW_GLOBAL_MAX (true){
		public AssignmentStatusFlag downgrade() {return OVERTIME;}
	},
	OVERTIME (true){
		public AssignmentStatusFlag downgrade() {return HOUSE_ONLY;}
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
